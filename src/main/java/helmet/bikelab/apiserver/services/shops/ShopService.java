package helmet.bikelab.apiserver.services.shops;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.Banks;
import helmet.bikelab.apiserver.domain.Estimates;
import helmet.bikelab.apiserver.domain.Settles;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.ClientAddresses;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.embeds.ModelBankAccount;
import helmet.bikelab.apiserver.domain.lease.Fines;
import helmet.bikelab.apiserver.domain.shops.*;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.BusinessTypes;
import helmet.bikelab.apiserver.domain.types.SettleStatusTypes;
import helmet.bikelab.apiserver.domain.types.TimeTypes;
import helmet.bikelab.apiserver.objects.*;
import helmet.bikelab.apiserver.objects.requests.*;
import helmet.bikelab.apiserver.objects.requests.shops.*;
import helmet.bikelab.apiserver.objects.responses.FetchSettleDetailResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Crypt;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.ClientWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.ShopWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;
import static helmet.bikelab.apiserver.utils.Utils.randomPassword;

@Service
@RequiredArgsConstructor
public class ShopService extends SessService {

    private final ShopsRepository shopsRepository;
    private final ShopInfoRepository shopInfoRepository;
    private final ShopPasswordRepository shopPasswordRepository;
    private final ShopAddressesRepository shopAddressesRepository;
    private final ShopAttachmentRepository shopAttachmentRepository;
    private final EstimatesRepository estimatesRepository;
    private final RegularInspectionRepository regularInspectionRepository;
    private final RegularInspectionHistoryRepository regularInspectionHistoryRepository;
    private final AutoKey autoKey;
    private final ShopWorker shopWorker;
    private final ClientWorker clientWorker;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final SettleRepository settleRepository;
    private final BankRepository bankRepository;
    private final CommonWorker commonWorker;

    public BikeSessionRequest fetchHistoryOfShop(BikeSessionRequest request) {
        Map param = request.getParam();
        ShopListDto requestListDto = map(param, ShopListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.bike_user_log.getBikeUserLogInShopHistories", "bikelabs.bike_user_log.countAllBikeUserLogInShopHistories", "log_no");
        request.setResponse(responseListDto);
        return request;
    }

    @Transactional
    public BikeSessionRequest updatePasswordByShopId(BikeSessionRequest request) {
        Map param = request.getParam();
        String shopId = (String) param.get("shop_id");
        Shops shopByShopId = shopWorker.getShopByShopId(shopId);
        ShopPassword shopPassword = shopByShopId.getShopPassword();
        String plainPassword = randomPassword(8);
        shopPassword.makePassword(plainPassword);
        shopPasswordRepository.save(shopPassword);

        Map response = new HashMap();
        response.put("plain_password", plainPassword);
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchAllShops(BikeSessionRequest request) {
        PageableRequest pageableRequest = map(request.getParam(), PageableRequest.class);
        String keyword = (String) request.getParam().get("keyword");
        Page<Shops> allShopByPageableRequest = shopWorker.getAllShopByPageableRequest(pageableRequest, keyword);
        request.setResponse(allShopByPageableRequest);
        return request;
    }

    public BikeSessionRequest fetchAllShopsWithoutPage(BikeSessionRequest request) {
        String keyword = (String) request.getParam().get("keyword");
        List<Shops> shopList;
        if (bePresent(keyword))
            shopList = shopsRepository.findAllByShopInfo_NameContaining(keyword);
        else
            shopList = shopsRepository.findAll();
        request.setResponse(shopList);
        return request;
    }

    public BikeSessionRequest fetchShopDetailsByShopId(BikeSessionRequest request) {
        Map param = request.getParam();
        String shopId = (String) param.get("shop_id");
        Shops shopByShopId = shopWorker.getShopByShopId(shopId);
        request.setResponse(shopByShopId);
        return request;
    }

    @Transactional
    public BikeSessionRequest registerNewShop(BikeSessionRequest request) {
        AddShopRequest addShopRequest = map(request.getParam(), AddShopRequest.class);
        addShopRequest.checkValidation();
        BikeUser sessionUser = request.getSessionUser();
        if (shopWorker.checkIfEmailExists(addShopRequest.getEmail())) withException("401-008");
        if (shopWorker.checkIfRegNumExists(addShopRequest.getRegNum())) withException("401-009");
        String shopId = autoKey.makeGetKey("shop");
        Shops shop = new Shops();
        shop.setEmail(addShopRequest.getEmail());
        shop.setShopId(shopId);
        shop.setRate(bePresent(addShopRequest.getRate()) ? addShopRequest.getRate() : 70);
        shop.setRegNum(addShopRequest.getRegNum());
        shop.setBusinessType(BusinessTypes.getBusinessTypes(addShopRequest.getBusinessType()));
        shopsRepository.save(shop);

        ShopInfo shopInfo = new ShopInfo();
        shopInfo.setShop(shop);
        shopInfo.setPhone(addShopRequest.getPhone());
        shopInfo.setFaxNumber(addShopRequest.getFaxNumber());
        shopInfo.setShopNo(shop.getShopNo());
        shopInfo.setName(addShopRequest.getName());
        shopInfo.setManagerName(addShopRequest.getManagerName());
        shopInfo.setStartTime(LocalTime.parse(addShopRequest.getStartTime()));
        shopInfo.setEndTime(LocalTime.parse(addShopRequest.getEndTime()));
        ModelBankAccount modelBankAccount = new ModelBankAccount();
        modelBankAccount.setBankCode(addShopRequest.getBankCd());
        modelBankAccount.setAccount(addShopRequest.getAccount());
        modelBankAccount.setDepositor(addShopRequest.getDepositor());
        shopInfo.setBankAccount(modelBankAccount);
        shopInfoRepository.save(shopInfo);

        ShopPassword shopPassword = new ShopPassword();
        shopPassword.setShop(shop);
        shopPassword.setShopNo(shop.getShopNo());
//        String cryptedPassword = addShopRequest.getEmail();
        String cryptedPassword = Crypt.newCrypt().SHA256(addShopRequest.getEmail());
        shopPassword.makePassword(cryptedPassword);
        shopPasswordRepository.save(shopPassword);

        ShopAddresses shopAddresses = new ShopAddresses();
        shopAddresses.setShop(shop);
        shopAddresses.setShopNo(shop.getShopNo());
        shopAddresses.setModelAddress(addShopRequest.getAddress());
        shopAddresses.setLatitude(addShopRequest.getLatitude());
        shopAddresses.setLongitude(addShopRequest.getLongitude());
        shopAddressesRepository.save(shopAddresses);

        Map response = new HashMap();
        response.put("shop_id", shopId);
        request.setResponse(response);

        bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_CLIENT_ADDED, sessionUser.getUserNo(), shop.getShopNo().toString()));

        return request;
    }

    @Transactional
    public BikeSessionRequest updateShopInfo(BikeSessionRequest request) {
        UpdateShopRequest shopRequest = map(request.getParam(), UpdateShopRequest.class);
        shopRequest.checkValidation();
        BikeUser sessionUser = request.getSessionUser();
        Shops shopByShopId = shopWorker.getShopByShopId(shopRequest.getShopId());
        ShopInfo shopInfo = shopByShopId.getShopInfo();
        ShopAddresses shopAddress = shopByShopId.getShopAddress();
        ModelBankAccount modelBankAccount = new ModelBankAccount();
        updateShopInfoLog(BikeUserLogTypes.COMM_SHOP_UPDATED, shopByShopId, shopInfo, shopAddress, shopRequest, sessionUser);
        if (!shopByShopId.getEmail().equals(shopRequest.getEmail()) && shopWorker.checkIfEmailExists(shopRequest.getEmail()))
            withException("401-008");
        if (!shopByShopId.getRegNum().equals(shopRequest.getRegNum()) && shopWorker.checkIfRegNumExists(shopRequest.getRegNum()))
            withException("401-009");

        shopByShopId.setBusinessType(BusinessTypes.getBusinessTypes(shopRequest.getBusinessType()));
        shopByShopId.setEmail(shopRequest.getEmail());
        shopByShopId.setRegNum(shopRequest.getRegNum());
        shopByShopId.setRate(shopRequest.getRate());
        shopsRepository.save(shopByShopId);

        shopInfo.setPhone(shopRequest.getPhone());
        shopInfo.setFaxNumber(shopRequest.getFaxNumber());
        shopInfo.setName(shopRequest.getName());
        shopInfo.setManagerName(shopRequest.getManagerName());
        shopInfo.setStartTime(LocalTime.parse(shopRequest.getStartTime()));
        shopInfo.setEndTime(LocalTime.parse(shopRequest.getEndTime()));

        modelBankAccount.setBankCode(shopRequest.getBankCd());
        modelBankAccount.setAccount(shopRequest.getAccount());
        modelBankAccount.setDepositor(shopRequest.getDepositor());
        shopInfo.setBankAccount(modelBankAccount);
        shopInfoRepository.save(shopInfo);

        shopAddress.setModelAddress(shopRequest.getAddress());
        shopAddress.setLatitude(shopRequest.getLatitude());
        shopAddress.setLongitude(shopRequest.getLongitude());
        shopAddressesRepository.save(shopAddress);

        return request;
    }

    public void updateShopInfoLog(BikeUserLogTypes bikeUserLogTypes, Shops originShop, ShopInfo originShopInfo, ShopAddresses originShopAddress, UpdateShopRequest updatedObj, BikeUser fromUser) {
        List<String> stringList = new ArrayList<>();
        Banks bankInfo = bankRepository.findByBankCode(updatedObj.getBankCd());
        ShopAddresses shopAddresses = originShop.getShopAddress() == null ? new ShopAddresses() : originShop.getShopAddress();
        ModelAddress modelAddress = shopAddresses.getModelAddress() == null ? new ModelAddress() : shopAddresses.getModelAddress();
        ModelAddress address = updatedObj.getAddress();

        if (bePresent(updatedObj.getName()) && !updatedObj.getName().equals(originShopInfo.getName())) {
            if (bePresent(originShopInfo.getName()))
                stringList.add("정비소 이름을 <>" + originShopInfo.getName() + "</>에서 <>" + updatedObj.getName() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 이름을 <>" + updatedObj.getName() + "</>(으)로 등록하였습니다.");
        }
        if (bePresent(updatedObj.getManagerName()) && !updatedObj.getManagerName().equals(originShopInfo.getManagerName())) {
            if (bePresent(originShopInfo.getManagerName()))
                stringList.add("정비소 담당자 이름을 <>" + originShopInfo.getManagerName() + "</>에서 <>" + updatedObj.getManagerName() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 이름을 <>" + updatedObj.getManagerName() + "</>(으)로 등록하였습니다.");
        }
        if (bePresent(updatedObj.getEmail()) && !updatedObj.getEmail().equals(originShop.getEmail())) {
            if (bePresent(originShop.getEmail()))
                stringList.add("정비소 이메일을 <>" + originShop.getEmail() + "</>에서 <>" + updatedObj.getEmail() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 이메일을 <>" + updatedObj.getEmail() + "</>(으)로 등록하였습니다.");
        }
        if (bePresent(updatedObj.getRegNum()) && !updatedObj.getRegNum().equals(originShop.getRegNum())) {
            if (bePresent(originShop.getRegNum()))
                stringList.add("정비소 사업자번호를 <>" + originShop.getRegNum() + "</>에서 <>" + updatedObj.getRegNum() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 사업자번호를 <>" + updatedObj.getRegNum() + "</>(으)로 등록하였습니다.");
        }
        if (bePresent(updatedObj.getBusinessType()) && !updatedObj.getBusinessType().equals(originShop.getBusinessTypeCode())) {
            BusinessTypes businessTypes = BusinessTypes.getBusinessTypes(updatedObj.getBusinessType());
            if (bePresent(originShop.getBusinessType()))
                stringList.add("정비소 사업 타입을 <>" + originShop.getBusinessType().getBusiness() + "</>에서 <>" + businessTypes.getBusiness() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 사업 타입을 <>" + businessTypes.getBusiness() + "</>(으)로 등록하였습니다.");
        }
        if (bePresent(updatedObj.getPhone()) && !updatedObj.getPhone().equals(originShopInfo.getPhone())) {
            if (bePresent(originShopInfo.getPhone()))
                stringList.add("정비소 연락처를 <>" + originShopInfo.getPhone() + "</>에서 <>" + updatedObj.getPhone() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 연락처를 <>" + updatedObj.getPhone() + "</>(으)로 등록하였습니다.");
        }
        if (bePresent(updatedObj.getFaxNumber()) && !updatedObj.getFaxNumber().equals(originShopInfo.getFaxNumber())) {
            if (bePresent(originShopInfo.getFaxNumber()))
                stringList.add("정비소 팩스 번호를 <>" + originShopInfo.getPhone() + "</>에서 <>" + updatedObj.getPhone() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 팩스 번호를 <>" + updatedObj.getPhone() + "</>(으)로 등록하였습니다.");
        }
//        if(bePresent(updatedObj.getStartTime()) && !updatedObj.getStartTime().equals(originShopInfo.getStartTime())){
//            if(bePresent(originShopInfo.getStartTime()))
//                stringList.add("정비소 영업 시작시간을 <>" + originShopInfo.getStartTime() + "</>에서 <>" + updatedObj.getStartTime() + "</>(으)로 변경하였습니다.");
//            else stringList.add("정비소 영업 시작시간을 <>" +  updatedObj.getStartTime() + "</>(으)로 등록하였습니다.");
//        }
        if (bePresent(updatedObj.getStartTime()) && !LocalTime.parse(updatedObj.getStartTime()).equals(originShopInfo.getStartTime())) {
            if (bePresent(originShopInfo.getStartTime()))
                stringList.add("정비소 영업 시작시간을 <>" + originShopInfo.getStartTime() + "</>에서 <>" + updatedObj.getStartTime() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 영업 시작시간을 <>" + updatedObj.getStartTime() + "</>(으)로 등록하였습니다.");
        }
//        if(bePresent(updatedObj.getEndTime()) && !updatedObj.getEndTime().equals(originShopInfo.getEndTime())){
//            if(bePresent(originShopInfo.getEndTime()))
//                stringList.add("정비소 영업 종료시간을 <>" + originShopInfo.getEndTime() + "</>에서 <>" + updatedObj.getEndTime() + "</>(으)로 변경하였습니다.");
//            else stringList.add("정비소 영업 종료시간을 <>" +  updatedObj.getEndTime() + "</>(으)로 등록하였습니다.");
//        }
        if (bePresent(updatedObj.getEndTime()) && !LocalTime.parse(updatedObj.getEndTime()).equals(originShopInfo.getEndTime())) {
            if (bePresent(originShopInfo.getEndTime()))
                stringList.add("정비소 영업 종료시간을 <>" + originShopInfo.getEndTime() + "</>에서 <>" + updatedObj.getEndTime() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 영업 종료시간을 <>" + updatedObj.getEndTime() + "</>(으)로 등록하였습니다.");
        }

        if (bePresent(updatedObj.getBankCd()) && !bePresent(originShopInfo.getBankAccount())) { // 기존정보 없고, 업데이트가 있을 때
            stringList.add("입금 은행을 <>" + bankInfo.getBankName() + "</>(으)로 등록하였습니다.");
        } else if (bePresent(updatedObj.getBankCd()) && bePresent(originShopInfo.getBankAccount())) { // 기존 정보는 있고, 업데이트 있을 때
            if (originShopInfo.getBankAccount().getBankCode() == null) {  // 업데이트된 은행 정보가 null 이 아니고, 기존 은행코드가 없으면
                stringList.add("입금 은행을 <>" + bankInfo.getBankName() + "</>(으)로 등록하였습니다.");
            } else if (bePresent(updatedObj.getBankCd()) && bePresent(originShopInfo.getBankAccount().getBankCode())) { // 기본정보와 업데이트 정보가 모두 있을 때
                if (!updatedObj.getBankCd().equals(originShopInfo.getBankAccount().getBankCode())) { // 기존과 업데이트 정보가 서로 다를 때
                    stringList.add("입금 은행을 <>" + originShopInfo.getBankAccount().getBank().getBankName() + "</>에서 <>" + bankInfo.getBankName() + "</>(으)로 변경하였습니다.");
                }
            }
        } else if (!bePresent(updatedObj.getBankCd())) { // 업데이트 정보가 null 일 때
            if (bePresent(originShopInfo.getBankAccount())) {
                if (bePresent(originShopInfo.getBankAccount().getBankCode()))
                    stringList.add("입금 은행 정보를 삭제했습니다.");
            }
        }

        if (bePresent(updatedObj.getAccount()) && !bePresent(originShopInfo.getBankAccount())) { // 기존정보 없거나, 업데이트가 있을 때
            stringList.add("입금 계좌번호를 <>" + updatedObj.getAccount() + "</>(으)로 등록하였습니다.");
        } else if (bePresent(updatedObj.getAccount()) && bePresent(originShopInfo.getBankAccount())) { // 기존정보는 있고, 업데이트 있을 때
            if (!bePresent(originShopInfo.getBankAccount().getAccount())) { // 업데이트 정보가 null 아니고, 기존계좌정보가 null일 때
                stringList.add("입금 계좌번호를 <>" + updatedObj.getAccount() + "</>(으)로 등록하였습니다.");
            } else if (!updatedObj.getAccount().equals(originShopInfo.getBankAccount().getAccount())) { // 업데이트 정보와 기존정보가 다를 때)
                stringList.add("입금 계좌번호를 <>" + originShopInfo.getBankAccount().getAccount() + "</>에서 <>" + updatedObj.getAccount() + "</>(으)로 변경하였습니다.");
            }
        } else if (!bePresent(updatedObj.getAccount())) {
            if (bePresent(originShopInfo.getBankAccount())) { // 업데이트 계좌정보 null 이고, 기존 정보 있을 때
                if (bePresent(originShopInfo.getBankAccount().getAccount()))
                    stringList.add("입금 계좌 정보를 삭제했습니다.");
            }
        }


        if (bePresent(updatedObj.getDepositor()) && !bePresent(originShopInfo.getBankAccount())) { // 기존정보 없거나, 업데이트가 있을 때
            stringList.add("예금주를 <>" + updatedObj.getDepositor() + "</>(으)로 등록하였습니다.");
        } else if (bePresent(updatedObj.getDepositor()) && bePresent(originShopInfo.getBankAccount())) { // 기존정보는 있고, 업데이트 있을 때
            if (!bePresent(originShopInfo.getBankAccount().getDepositor())) { // 업데이트 정보가 null 아니고, 기존계좌정보가 null일 때
                stringList.add("예금주를 <>" + updatedObj.getDepositor() + "</>(으)로 등록하였습니다.");
            } else if (!updatedObj.getDepositor().equals(originShopInfo.getBankAccount().getDepositor())) { // 업데이트 정보와 기존정보가 다를 때)
                stringList.add("예금주를 <>" + originShopInfo.getBankAccount().getDepositor() + "</>에서 <>" + updatedObj.getDepositor() + "</>(으)로 변경하였습니다.");
            }
        } else if (!bePresent(updatedObj.getDepositor())) {
            if (bePresent(originShopInfo.getBankAccount())) {
                if (bePresent(originShopInfo.getBankAccount().getDepositor()))
                    stringList.add("예금주 정보를 삭제했습니다.");
            }
        }

        if (bePresent(address) && !address.getAddress().equals(modelAddress.getAddress())) {
            stringList.add("고객사 주소를 변경하였습니다.");
        }

        if (bePresent(stringList) && stringList.size() > 0)
            bikeUserLogRepository.save(addLog(bikeUserLogTypes, fromUser.getUserNo(), originShop.getShopNo().toString(), stringList));
    }

    public BikeSessionRequest fetchBanks(BikeSessionRequest request) {
        List<Banks> all = bankRepository.findAll();
        request.setResponse(all);
        return request;
    }

    public BikeSessionRequest fetchSettles(BikeSessionRequest request) {
        Map param = request.getParam();
        RequestListDto requestListDto = map(param, RequestListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "estimate.settles.fetchAllSettles", "estimate.settles.countAllSettles", "settle_no");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchSettleDetail(BikeSessionRequest request) {
        Map param = request.getParam();
        String settleId = (String) param.get("settle_id");
        Settles bySettleId = settleRepository.findBySettleId(settleId);
        List<Estimates> allBySettle_settleId = estimatesRepository.findAllBySettle_SettleId(settleId);
        FetchSettleDetailResponse fetchSettleDetailResponse = new FetchSettleDetailResponse();
        fetchSettleDetailResponse.setSettleId(settleId);
        fetchSettleDetailResponse.setShop(bySettleId.getShop());
        fetchSettleDetailResponse.setCreatedAt(bySettleId.getCreatedAt());
        fetchSettleDetailResponse.setConfirmedAt(bySettleId.getConfirmedAt());
        fetchSettleDetailResponse.setConfirmedUserId(bySettleId.getConfirmedUser() == null ? null : bySettleId.getConfirmedUser().getUserId());
        fetchSettleDetailResponse.setBankAccount(bySettleId.getBankAccount());
        fetchSettleDetailResponse.setEstimates(allBySettle_settleId);
        fetchSettleDetailResponse.setSettleStatus(bySettleId.getSettleStatus() == null ? null : bySettleId.getSettleStatus().getStatus());
        fetchSettleDetailResponse.setDeductible(bySettleId.getDeductible());
        request.setResponse(fetchSettleDetailResponse);
        return request;
    }

    public BikeSessionRequest completeSettle(BikeSessionRequest request) {
        Map param = request.getParam();
        String settleId = (String) param.get("settle_id");
        Integer retroact = (Integer) param.get("retroact");
        Settles bySettleId = settleRepository.findBySettleId(settleId);
        bySettleId.setConfirmedAt(LocalDateTime.now());
        bySettleId.setConfirmedUserNo(request.getSessionUser().getUserNo());
        bySettleId.setDeductible(retroact);
        bySettleId.setSettleStatus(SettleStatusTypes.COMPLETED);
        settleRepository.save(bySettleId);
        return request;
    }

    public BikeSessionRequest generatePresignedUrl(BikeSessionRequest request) {
        Map param = request.getParam();
        String filename = (String) param.get("filename");
        PresignedURLVo presignedURLVo;
        if(filename.indexOf(".") >= 0) {
            String name = filename.substring(0, filename.lastIndexOf("."));
            String extension = filename.substring(filename.lastIndexOf(".") + 1);
            presignedURLVo = commonWorker.generatePreSignedUrl(name, extension);
        }else
            presignedURLVo = commonWorker.generatePreSignedUrl(filename, null);
        request.setResponse(presignedURLVo);
        return request;
    }

    @Transactional
    public BikeSessionRequest addAttachments(BikeSessionRequest request){
        Map param = request.getParam();
        AddShopAttachmentRequest addShopAttachmentRequest = map(param, AddShopAttachmentRequest.class);
        Shops shopByShopId = shopWorker.getShopByShopId(addShopAttachmentRequest.getShopId());
        ShopAttachments shopAttachments = shopByShopId.getShopAttachments() == null ? new ShopAttachments() : shopByShopId.getShopAttachments();
        shopAttachments.setShopNo(shopByShopId.getShopNo());
        List<ModelAttachment> attachmentsList = shopAttachments.getAttachmentsList();
        if(!bePresent(attachmentsList))
            attachmentsList = new ArrayList<>();
        List<ModelAttachment> toAdd = addShopAttachmentRequest.getAttachments()
                .stream().map(presignedURLVo -> {
                    AmazonS3 amazonS3 = AmazonUtils.amazonS3();
                    String fileKey = "shop-attachment/" + shopByShopId.getShopId() + "/" + presignedURLVo.getFileKey();
                    CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
                    amazonS3.copyObject(objectRequest);
                    ModelAttachment shopAttachment = new ModelAttachment();
                    shopAttachment.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
                    shopAttachment.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
                    shopAttachment.setUri("/" + fileKey);
                    shopAttachment.setFileName(presignedURLVo.getFilename());
                    return shopAttachment;
                }).collect(Collectors.toList());
        attachmentsList.addAll(toAdd);
        shopAttachments.setAttachmentsList(attachmentsList);
        shopAttachmentRepository.save(shopAttachments);
        return request;
    }

    public BikeSessionRequest fetchAttachments(BikeSessionRequest request){
        Map param = request.getParam();
        String shopId = (String) param.get("shop_id");
        Shops shopByShopId = shopWorker.getShopByShopId(shopId);
        request.setResponse(bePresent(shopByShopId.getShopAttachments()) ? shopByShopId.getShopAttachments().getAttachmentsList() : new ArrayList<>());
        return request;
    }

    public BikeSessionRequest deleteAttachment(BikeSessionRequest request) {
        Map param = request.getParam();
        DeleteShopAttachmentRequest deleteShopAttachmentRequest = map(param, DeleteShopAttachmentRequest.class);
        Shops shopByShopId = shopWorker.getShopByShopId(deleteShopAttachmentRequest.getShopId());
        ShopAttachments shopAttachments = shopWorker.removeAttachment(shopByShopId.getShopAttachments(), deleteShopAttachmentRequest.getUuid());
        shopAttachmentRepository.save(shopAttachments);
        return request;
    }


    @Transactional
    public BikeSessionRequest addRegularInspection(BikeSessionRequest request){
        AddUpdateRegularInspectionRequest addUpdateRegularInspectionRequest = map(request.getParam(), AddUpdateRegularInspectionRequest.class);
        RegularInspections regularInspections = new RegularInspections();
        String inspectionId = autoKey.makeGetKey("regular_inspect");
        Clients clients = clientWorker.getClientByClientId(addUpdateRegularInspectionRequest.getClientId());
        Shops shopByShopId = shopWorker.getShopByShopId(addUpdateRegularInspectionRequest.getShopId());
        regularInspections.setInspectId(inspectionId);
        regularInspections.setClientNo(clients.getClientNo());
        regularInspections.setGroupNo(clients.getGroupNo());
        regularInspections.setShopNo(shopByShopId.getShopNo());
        List<ModelAttachment> attachments = new ArrayList<>();
        if(bePresent(addUpdateRegularInspectionRequest.getNewAttachments())) {
            List<ModelAttachment> newAttachments = addUpdateRegularInspectionRequest.getNewAttachments()
                    .stream().map(presignedURLVo -> {
                        AmazonS3 amazonS3 = AmazonS3Client.builder()
                                .withRegion(Regions.AP_NORTHEAST_2)
                                .withCredentials(AmazonUtils.awsCredentialsProvider())
                                .build();
                        String fileKey = "regular-inspection/" + regularInspections.getInspectId() + "/" + presignedURLVo.getFileKey();
                        CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
                        amazonS3.copyObject(objectRequest);
                        ModelAttachment leaseAttachment = new ModelAttachment();
                        leaseAttachment.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
                        leaseAttachment.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
                        leaseAttachment.setUri("/" + fileKey);
                        leaseAttachment.setFileName(presignedURLVo.getFilename());
                        return leaseAttachment;
                    }).collect(Collectors.toList());
            attachments.addAll(newAttachments);
        }
        regularInspections.setAttachmentsList(attachments);
        regularInspections.setInspectDt(addUpdateRegularInspectionRequest.getInspectDt());
        regularInspections.setIncludeDt(addUpdateRegularInspectionRequest.getIncludeDt());
        regularInspections.setCreatedAt(LocalDateTime.now());
        if(bePresent(regularInspections.getTimes())) {
            RegularInspections byTimesAndIncludeDt = regularInspectionRepository.findByClient_ClientIdAndTimesAndIncludeDt(addUpdateRegularInspectionRequest.getClientId(), regularInspections.getTimes(), regularInspections.getIncludeDt());
            if(bePresent(byTimesAndIncludeDt)) {
                byTimesAndIncludeDt.setTimes(null);
                regularInspectionRepository.save(byTimesAndIncludeDt);
            }
        }
        regularInspectionRepository.save(regularInspections);
        return request;
    }

    public BikeSessionRequest fetchInspections(BikeSessionRequest request){
        FetchRegularInspectionRequest fetchRegularInspectionRequest = map(request.getParam(), FetchRegularInspectionRequest.class);
//        Pageable pageable = PageRequest.of(fetchRegularInspectionRequest.getPage(), fetchRegularInspectionRequest.getSize());
//        if(bePresent(fetchRegularInspectionRequest.getClientId())){
//            Page<RegularInspections> allByClient_clientId = regularInspectionRepository.findAllByClient_ClientId(fetchRegularInspectionRequest.getClientId(), pageable);
//            request.setResponse(allByClient_clientId);
//        }else if(bePresent(fetchRegularInspectionRequest.getGroupId())){
//            Page<RegularInspections> allByGroup_groupId = regularInspectionRepository.findAllByGroup_GroupId(fetchRegularInspectionRequest.getGroupId(), pageable);
//            request.setResponse(allByGroup_groupId);
//        }else if(bePresent(fetchRegularInspectionRequest.getStartDt()) && bePresent(fetchRegularInspectionRequest.getEndDt())){
//            Page<RegularInspections> allByInspectDateBetween = regularInspectionRepository.findAllByInspectDtBetween(fetchRegularInspectionRequest.getStartDt(), fetchRegularInspectionRequest.getEndDt(), pageable);
//            request.setResponse(allByInspectDateBetween);
//        }else{
//            request.setResponse(regularInspectionRepository.findAllByOrderByInspectDtDesc(pageable));
//        }
        Map result = new HashMap();
        Map<String, List<RegularInspections>> contents = new HashMap();
        List<Clients> clientListByGroupId;
        if(bePresent(fetchRegularInspectionRequest.getGroupId())) {
            clientListByGroupId = clientWorker.getClientListByGroupId(fetchRegularInspectionRequest.getGroupId());
        }
        else{
            clientListByGroupId = clientWorker.getAllClientList();
        }
//        int from = fetchRegularInspectionRequest.getPage() * fetchRegularInspectionRequest.getPage();
//        int to = from + fetchRegularInspectionRequest.getSize();
//        clientListByGroupId = clientListByGroupId.subList(from, to > clientCnt ? to : clientCnt);
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(bePresent(fetchRegularInspectionRequest.getStandardDt())){
            localDateTime = fetchRegularInspectionRequest.getStandardDt();
        }
        LocalDateTime start = localDateTime.minusMonths(2).withDayOfMonth(1);
        LocalDateTime end = localDateTime.plusMonths(3).withDayOfMonth(1);
        String startStadards = start.format(formatter);
        String endStadards = end.format(formatter);
        List<Integer> clientList = new ArrayList<>();
        for (Clients c : clientListByGroupId) {
            clientList.add(c.getClientNo());
            contents.put(c.getClientId(), new ArrayList<>());
        }
        List<RegularInspections> regularInspections = regularInspectionRepository.findAllByClientNoInAndIncludeDtBetween(clientList, startStadards, endStadards);
        for (RegularInspections ri : regularInspections) {
            (contents.get(ri.getClient().getClientId())).add(ri);
        }
        List<List<RegularInspections>> inspectionsByClients = new ArrayList<>();
        for(String key : contents.keySet()){
            if((contents.get(key)).size() > 0){
                inspectionsByClients.add(contents.get(key));
            }
        }
//        result.put("inspections", inspectionsByClients);
//        result.put("total_elements", clientCnt);
//        result.put("page", fetchRegularInspectionRequest.getPage());
//        result.put("size", fetchRegularInspectionRequest.getSize());
        request.setResponse(inspectionsByClients);
        return request;
    }

    public BikeSessionRequest fetchInspectionsByGroups(BikeSessionRequest request){
//        FetchRegularInspectionRequest fetchRegularInspectionRequest = map(request.getParam(), FetchRegularInspectionRequest.class);
//        Map<String, List<RegularInspections>> result = new HashMap();
//        List<Clients> clientListByGroupId = clientWorker.getClientListByGroupId(fetchRegularInspectionRequest.getGroupId());
//        int start = fetchRegularInspectionRequest.getPage() * fetchRegularInspectionRequest.getSize();
//        LocalDateTime localDateTime = LocalDateTime.now().minusMonths(3);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDateTime now = LocalDateTime.now().minusMonths(3).withDayOfMonth(1);
//        String stadards = now.format(formatter);
//        localDateTime.toLocalDate();
////        clientListByGroupId = clientListByGroupId.subList(start, start + fetchRegularInspectionRequest.getSize() + 1 > clientListByGroupId.size() ? clientListByGroupId.size() : start + fetchRegularInspectionRequest.getSize() + 1);
//        List<Integer> clientList = new ArrayList<>();
//        for (Clients c : clientListByGroupId) {
//            clientList.add(c.getClientNo());
//            result.put(c.getClientId(), new ArrayList<>());
//        }
//        List<RegularInspections> regularInspections = regularInspectionRepository.findAllByClientNoInAndIncludeDtIsGreaterThanEqual(clientList, stadards);
//        for (RegularInspections ri : regularInspections) {
//            (result.get(ri.getClient().getClientId())).add(ri);
//        }
//        List<List<RegularInspections>> inspectionsByClients = new ArrayList<>();
//        for(String key : result.keySet()){
//            if((result.get(key)).size() > 0){
//                inspectionsByClients.add(result.get(key));
//            }
//        }
//        Map<String, Object> toReturn = new HashMap<>();
//        toReturn.put("inspections", inspectionsByClients);
////        toReturn.put("total_page", (int)Math.ceil((double)clientListByGroupId.size() / fetchRegularInspectionRequest.getSize()) - 1);
//        request.setResponse(toReturn);
        return request;
    }

    public BikeSessionRequest fetchInspectionDetail(BikeSessionRequest request) {
        FetchRegularInspectionRequest fetchRegularInspectionRequest = map(request.getParam(), FetchRegularInspectionRequest.class);
        RegularInspections regularInspections = regularInspectionRepository.findByInspectId(fetchRegularInspectionRequest.getInspectId());
        request.setResponse(regularInspections);
        return request;
    }

    @Transactional
    public BikeSessionRequest changeInspectDate(BikeSessionRequest request){
//        ChangeInspectionDateRequest changeInspectionDateRequest = map(request.getParam(), ChangeInspectionDateRequest.class);
//        RegularInspections regularInspections = regularInspectionRepository.findByInspectId(changeInspectionDateRequest.getInspectId());
//        regularInspections.setIncludeDt(changeInspectionDateRequest.getChangeDt());
//        regularInspectionRepository.save(regularInspections);
        return request;
    }


    @Transactional
    public BikeSessionRequest updateInspection(BikeSessionRequest request){
        AddUpdateRegularInspectionRequest addUpdateRegularInspectionRequest = map(request.getParam(), AddUpdateRegularInspectionRequest.class);
        RegularInspections regularInspections = regularInspectionRepository.findByInspectId(addUpdateRegularInspectionRequest.getInspectId());
        String log = changeLog(addUpdateRegularInspectionRequest, regularInspections, request.getSessionUser().getBikeUserInfo().getName());
        if(!log.isBlank()) {
            RegularInspectionHistories regularInspectionHistories = regularInspectionHistoryRepository.findByRegularInspections_InspectId(regularInspections.getInspectId());
            if(!bePresent(regularInspectionHistories)){
                regularInspectionHistories = new RegularInspectionHistories();
                regularInspectionHistories.setInspectNo(regularInspections.getInspectNo());
            }
            RiderInsHistoriesDto historiesDto = new RiderInsHistoriesDto();
            historiesDto.setLog(log);
            historiesDto.setUpdatedAt(LocalDateTime.now());
            regularInspectionHistories.getHistories().add(0, historiesDto);
            regularInspectionHistoryRepository.save(regularInspectionHistories);
        }
        Clients clients = clientWorker.getClientByClientId(addUpdateRegularInspectionRequest.getClientId());
        Shops shopByShopId = shopWorker.getShopByShopId(addUpdateRegularInspectionRequest.getShopId());
        regularInspections.setClientNo(clients.getClientNo());
        regularInspections.setGroupNo(clients.getGroupNo());
        regularInspections.setShopNo(shopByShopId.getShopNo());
        List<ModelAttachment> attachments = addUpdateRegularInspectionRequest.getAttachments() != null ? addUpdateRegularInspectionRequest.getAttachments() : new ArrayList<>();
        deletedAttachments(regularInspections.getAttachmentsList(), attachments).stream().forEach(ma -> {
            AmazonS3 amazonS3 = AmazonUtils.amazonS3();
            amazonS3.deleteObject(ENV.AWS_S3_ORIGIN_BUCKET, ma.getUri());
        });
        if(bePresent(addUpdateRegularInspectionRequest.getNewAttachments())) {
            List<ModelAttachment> newAttachments = addUpdateRegularInspectionRequest.getNewAttachments()
                    .stream().map(presignedURLVo -> {
                        AmazonS3 amazonS3 = AmazonS3Client.builder()
                                .withCredentials(AmazonUtils.awsCredentialsProvider())
                                .build();
                        String fileKey = "regular-inspection/" + regularInspections.getInspectId() + "/" + presignedURLVo.getFileKey();
                        CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
                        amazonS3.copyObject(objectRequest);
                        ModelAttachment leaseAttachment = new ModelAttachment();
                        leaseAttachment.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
                        leaseAttachment.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
                        leaseAttachment.setUri("/" + fileKey);
                        leaseAttachment.setFileName(presignedURLVo.getFilename());
                        return leaseAttachment;
                    }).collect(Collectors.toList());
            attachments.addAll(newAttachments);
        }
        regularInspections.setAttachmentsList(attachments);
        regularInspections.setInspectDt(addUpdateRegularInspectionRequest.getInspectDt());
        regularInspections.setIncludeDt(addUpdateRegularInspectionRequest.getIncludeDt());
        if(bePresent(regularInspections.getTimes())) {
            RegularInspections byTimesAndIncludeDt = regularInspectionRepository.findByClient_ClientIdAndTimesAndIncludeDt(addUpdateRegularInspectionRequest.getClientId(), regularInspections.getTimes(), regularInspections.getIncludeDt());
            if(bePresent(byTimesAndIncludeDt)) {
                byTimesAndIncludeDt.setTimes(null);
                regularInspectionRepository.save(byTimesAndIncludeDt);
            }
        }
        regularInspectionRepository.save(regularInspections);
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteInspection(BikeSessionRequest request){
        ChangeInspectionDateRequest deleteInspectionRequest = map(request.getParam(), ChangeInspectionDateRequest.class);
        regularInspectionHistoryRepository.deleteByRegularInspections_InspectId(deleteInspectionRequest.getInspectId());
        regularInspectionRepository.deleteByInspectId(deleteInspectionRequest.getInspectId());
        return request;
    }

    private String changeLog(AddUpdateRegularInspectionRequest request, RegularInspections regularInspections, String updaterName){
        String change = "";
        Clients regClient = regularInspections.getClient();
        Clients client = clientWorker.getClientByClientId(request.getClientId());
        Shops shop = shopWorker.getShopByShopId(request.getShopId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월");
        if(regClient.getClientNo() != client.getClientNo()){
            change += "고객사를 <>" + regClient.getClientInfo().getName() + "</>에서 <>" + client.getClientInfo().getName() + "</>로 변경하였습니다.";
        }
        if(shop.getShopNo() != regularInspections.getShopNo()){
            change += "정비소를 <>" + regularInspections.getShop().getShopInfo().getName() + "</>에서 <>" + shop.getShopInfo().getName() + "</>로 변경하였습니다.";
        }
        if(!request.getIncludeDt().equals(regularInspections.getIncludeDt())){
            change += "정기점검 적용 날짜를 <>" + dateFormatting(regularInspections.getIncludeDt()) + "</>에서 <>" + dateFormatting(request.getIncludeDt()) + "</>로 변경하였습니다.";
        }
        if(!request.getInspectDt().equals(regularInspections.getInspectDt())){
            change += "정기점검 날짜를 <>" + regularInspections.getInspectDt().format(formatter) + "</>에서 <>" + request.getInspectDt().format(formatter) + "</>로 변경하였습니다.";
        }
        if(bePresent(regularInspections.getTimes())){
            if(!bePresent(request.getOrder())){
                change += "정기점검 순서를 <>" + regularInspections.getTimes() + "</>에서 없음으로 수정하였습니다.";
            }else if(!regularInspections.getTimes().getTime().equals(request.getOrder())){
                change += "정기점검 순서를 <>" + regularInspections.getTimes() + "</>에서 <>" + TimeTypes.getType(request.getOrder()) + "</>로 변경하였습니다.";
            }
        }else{
            if(bePresent(request.getOrder())){
                change += "정기점검 순서를 <>" + TimeTypes.getType(request.getOrder()) + "</>로 설정하였습니다.";
            }
        }
        if(!change.isBlank()){
            change = "수정자 <>" + updaterName + "</>님께서 수정하셨습니다.\n" + change;
        }
        return change;
    }

    private String dateFormatting(String date){
        String year = date.substring(0, date.indexOf("-"));
        date = date.substring(date.indexOf("-") + 1);
        String month = date.substring(0, date.indexOf("-"));
        return String.format("%s년 %s월", year, month);
    }

    private List<ModelAttachment> deletedAttachments(List<ModelAttachment> origin, List<ModelAttachment> updated){
        List<ModelAttachment> deleted = new ArrayList<>();
        if(origin.size() == updated.size()){
            return deleted;
        }else{
            for(int i = 0; i < origin.size(); i++){
                if(updated.indexOf(origin.get(i)) < 0){
                    deleted.add(origin.get(i));
                }
            }
        }
        return deleted;
    }

}