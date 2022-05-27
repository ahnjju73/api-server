package helmet.bikelab.apiserver.services.shops;

import helmet.bikelab.apiserver.domain.Banks;
import helmet.bikelab.apiserver.domain.Estimates;
import helmet.bikelab.apiserver.domain.Settles;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.ClientAddresses;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.embeds.ModelBankAccount;
import helmet.bikelab.apiserver.domain.shops.ShopAddresses;
import helmet.bikelab.apiserver.domain.shops.ShopInfo;
import helmet.bikelab.apiserver.domain.shops.ShopPassword;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.SettleStatusTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.requests.ClientListDto;
import helmet.bikelab.apiserver.objects.requests.PageableRequest;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.requests.shops.AddShopRequest;
import helmet.bikelab.apiserver.objects.requests.shops.ShopListDto;
import helmet.bikelab.apiserver.objects.requests.shops.UpdateShopRequest;
import helmet.bikelab.apiserver.objects.responses.FetchSettleDetailResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Crypt;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.ShopWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;
import static helmet.bikelab.apiserver.utils.Utils.randomPassword;

@Service
@RequiredArgsConstructor
public class ShopService extends SessService {

    private final ShopsRepository shopsRepository;
    private final ShopInfoRepository shopInfoRepository;
    private final ShopPasswordRepository shopPasswordRepository;
    private final ShopAddressesRepository shopAddressesRepository;
    private final EstimatesRepository estimatesRepository;
    private final AutoKey autoKey;
    private final ShopWorker shopWorker;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final SettleRepository settleRepository;
    private final BankRepository bankRepository;
    private final CommonWorker commonWorker;

    public BikeSessionRequest fetchHistoryOfShop(BikeSessionRequest request){
        Map param = request.getParam();
        ShopListDto requestListDto = map(param, ShopListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.bike_user_log.getBikeUserLogInShopHistories", "bikelabs.bike_user_log.countAllBikeUserLogInShopHistories", "log_no");
        request.setResponse(responseListDto);
        return request;
    }

    @Transactional
    public BikeSessionRequest updatePasswordByShopId(BikeSessionRequest request){
        Map param = request.getParam();
        String shopId = (String)param.get("shop_id");
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

    public BikeSessionRequest fetchAllShops(BikeSessionRequest request){
        PageableRequest pageableRequest = map(request.getParam(), PageableRequest.class);
        String keyword = (String) request.getParam().get("keyword");
        Page<Shops> allShopByPageableRequest = shopWorker.getAllShopByPageableRequest(pageableRequest, keyword);
        request.setResponse(allShopByPageableRequest);
        return request;
    }

    public BikeSessionRequest fetchAllShopsWithoutPage(BikeSessionRequest request){
        String keyword = (String) request.getParam().get("keyword");
        List<Shops> shopList;
        if(bePresent(keyword))
            shopList = shopsRepository.findAllByShopInfo_NameContaining(keyword);
        else
            shopList = shopsRepository.findAll();
        request.setResponse(shopList);
        return request;
    }

    public BikeSessionRequest fetchShopDetailsByShopId(BikeSessionRequest request){
        Map param = request.getParam();
        String shopId = (String)param.get("shop_id");
        Shops shopByShopId = shopWorker.getShopByShopId(shopId);
        request.setResponse(shopByShopId);
        return request;
    }

    @Transactional
    public BikeSessionRequest registerNewShop(BikeSessionRequest request){
        AddShopRequest addShopRequest = map(request.getParam(), AddShopRequest.class);
        addShopRequest.checkValidation();
        BikeUser sessionUser = request.getSessionUser();
        if(shopWorker.checkIfEmailExists(addShopRequest.getEmail())) withException("401-008");
        if(shopWorker.checkIfRegNumExists(addShopRequest.getRegNum())) withException("401-009");
        String shopId = autoKey.makeGetKey("shop");
        Shops shop = new Shops();
        shop.setEmail(addShopRequest.getEmail());
        shop.setShopId(shopId);
        shop.setRegNum(addShopRequest.getRegNum());
        shopsRepository.save(shop);

        ShopInfo shopInfo = new ShopInfo();
        shopInfo.setShop(shop);
        shopInfo.setPhone(addShopRequest.getPhone());
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
    public BikeSessionRequest updateShopInfo(BikeSessionRequest request){
        UpdateShopRequest shopRequest = map(request.getParam(), UpdateShopRequest.class);
        shopRequest.checkValidation();
        BikeUser sessionUser = request.getSessionUser();
        Shops shopByShopId = shopWorker.getShopByShopId(shopRequest.getShopId());
        ShopInfo shopInfo = shopByShopId.getShopInfo();
        ShopAddresses shopAddress = shopByShopId.getShopAddress();
        ModelBankAccount modelBankAccount = new ModelBankAccount();
        updateShopInfoLog(BikeUserLogTypes.COMM_SHOP_UPDATED, shopByShopId, shopInfo, shopAddress, shopRequest, sessionUser);
        if(!shopByShopId.getEmail().equals(shopRequest.getEmail()) && shopWorker.checkIfEmailExists(shopRequest.getEmail())) withException("401-008");
        if(!shopByShopId.getRegNum().equals(shopRequest.getRegNum()) && shopWorker.checkIfRegNumExists(shopRequest.getRegNum())) withException("401-009");

        shopByShopId.setEmail(shopRequest.getEmail());
        shopByShopId.setRegNum(shopRequest.getRegNum());
        shopsRepository.save(shopByShopId);

        shopInfo.setPhone(shopRequest.getPhone());
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
        if (bePresent(updatedObj.getPhone()) && !updatedObj.getPhone().equals(originShopInfo.getPhone())) {
            if (bePresent(originShopInfo.getPhone()))
                stringList.add("정비소 연락처를 <>" + originShopInfo.getPhone() + "</>에서 <>" + updatedObj.getPhone() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 연락처를 <>" + updatedObj.getPhone() + "</>(으)로 등록하였습니다.");
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
        }else if (bePresent(updatedObj.getBankCd()) && bePresent(originShopInfo.getBankAccount())){ // 기존 정보는 있고, 업데이트 있을 때
            if (originShopInfo.getBankAccount().getBankCode() == null) {  // 업데이트된 은행 정보가 null 이 아니고, 기존 은행코드가 없으면
                stringList.add("입금 은행을 <>" + bankInfo.getBankName() + "</>(으)로 등록하였습니다.");
            }else if (bePresent(updatedObj.getBankCd()) && bePresent(originShopInfo.getBankAccount().getBankCode())) { // 기본정보와 업데이트 정보가 모두 있을 때
                if (!updatedObj.getBankCd().equals(originShopInfo.getBankAccount().getBankCode())) { // 기존과 업데이트 정보가 서로 다를 때
                    stringList.add("입금 은행을 <>" + originShopInfo.getBankAccount().getBank().getBankName() + "</>에서 <>" + bankInfo.getBankName() + "</>(으)로 변경하였습니다.");
                }
            }
        }else if (!bePresent(updatedObj.getBankCd())){ // 업데이트 정보가 null 일 때
            if (bePresent(originShopInfo.getBankAccount())){
                if (bePresent(originShopInfo.getBankAccount().getBankCode()))
                    stringList.add("입금 은행 정보를 삭제했습니다.");
            }
        }

        if (bePresent(updatedObj.getAccount()) && !bePresent(originShopInfo.getBankAccount())) { // 기존정보 없거나, 업데이트가 있을 때
            stringList.add("입금 계좌번호를 <>" + updatedObj.getAccount() + "</>(으)로 등록하였습니다.");
        }else if (bePresent(updatedObj.getAccount()) && bePresent(originShopInfo.getBankAccount())){ // 기존정보는 있고, 업데이트 있을 때
            if (!bePresent(originShopInfo.getBankAccount().getAccount())) { // 업데이트 정보가 null 아니고, 기존계좌정보가 null일 때
                stringList.add("입금 계좌번호를 <>" + updatedObj.getAccount() + "</>(으)로 등록하였습니다.");
            }else if (!updatedObj.getAccount().equals(originShopInfo.getBankAccount().getAccount())) { // 업데이트 정보와 기존정보가 다를 때)
                stringList.add("입금 계좌번호를 <>" + originShopInfo.getBankAccount().getAccount() + "</>에서 <>" + updatedObj.getAccount() + "</>(으)로 변경하였습니다.");
            }
        }else if (!bePresent(updatedObj.getAccount())){
            if (bePresent(originShopInfo.getBankAccount())) { // 업데이트 계좌정보 null 이고, 기존 정보 있을 때
                if (bePresent(originShopInfo.getBankAccount().getAccount()))
                    stringList.add("입금 계좌 정보를 삭제했습니다.");
            }
        }


        if (bePresent(updatedObj.getDepositor()) && !bePresent(originShopInfo.getBankAccount())) { // 기존정보 없거나, 업데이트가 있을 때
            stringList.add("예금주를 <>" + updatedObj.getDepositor() + "</>(으)로 등록하였습니다.");
        }else if(bePresent(updatedObj.getDepositor()) && bePresent(originShopInfo.getBankAccount())){ // 기존정보는 있고, 업데이트 있을 때
            if(!bePresent(originShopInfo.getBankAccount().getDepositor())) { // 업데이트 정보가 null 아니고, 기존계좌정보가 null일 때
                stringList.add("예금주를 <>" + updatedObj.getDepositor() + "</>(으)로 등록하였습니다.");
            }else if(!updatedObj.getDepositor().equals(originShopInfo.getBankAccount().getDepositor())) { // 업데이트 정보와 기존정보가 다를 때)
                stringList.add("예금주를 <>" + originShopInfo.getBankAccount().getDepositor() + "</>에서 <>" + updatedObj.getDepositor() + "</>(으)로 변경하였습니다.");
            }
        }else if(!bePresent(updatedObj.getDepositor())){
            if(bePresent(originShopInfo.getBankAccount())) {
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
        FetchSettleDetailResponse fetchSettleDetailResponse  = new FetchSettleDetailResponse();
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
        Integer retroact = (Integer)param.get("retroact");
        Settles bySettleId = settleRepository.findBySettleId(settleId);
        bySettleId.setConfirmedAt(LocalDateTime.now());
        bySettleId.setConfirmedUserNo(request.getSessionUser().getUserNo());
        bySettleId.setDeductible(retroact);
        bySettleId.setSettleStatus(SettleStatusTypes.COMPLETED);
        settleRepository.save(bySettleId);
        return request;
    }
}
