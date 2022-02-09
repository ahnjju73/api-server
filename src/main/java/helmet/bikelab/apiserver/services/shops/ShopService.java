package helmet.bikelab.apiserver.services.shops;

import helmet.bikelab.apiserver.domain.Banks;
import helmet.bikelab.apiserver.domain.Estimates;
import helmet.bikelab.apiserver.domain.Settles;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.embeds.ModelBankAccount;
import helmet.bikelab.apiserver.domain.shops.ShopAddresses;
import helmet.bikelab.apiserver.domain.shops.ShopInfo;
import helmet.bikelab.apiserver.domain.shops.ShopPassword;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
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
        Page<Shops> allShopByPageableRequest = shopWorker.getAllShopByPageableRequest(pageableRequest);
        request.setResponse(allShopByPageableRequest);
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
        ModelBankAccount modelBankAccount = new ModelBankAccount();
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

    public void updateShopInfoLog(BikeUserLogTypes bikeUserLogTypes, Shops originShop, ShopInfo originShopInfo, ShopAddresses originShopAddress, UpdateShopRequest updatedObj, BikeUser fromUser){
        List<String> stringList = new ArrayList<>();
        if(bePresent(updatedObj.getName()) && !updatedObj.getName().equals(originShopInfo.getName())){
            if(bePresent(originShopInfo.getName()))
                stringList.add("정비소 이름을 <>" + originShopInfo.getName() + "</>에서 <>" + updatedObj.getName() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 이름을 <>" +  updatedObj.getName() + "</>(으)로 등록하였습니다.");
        }
        if(bePresent(updatedObj.getManagerName()) && !updatedObj.getManagerName().equals(originShopInfo.getManagerName())){
            if(bePresent(originShopInfo.getManagerName()))
                stringList.add("정비소 담당자 이름을 <>" + originShopInfo.getManagerName() + "</>에서 <>" + updatedObj.getManagerName() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 이름을 <>" +  updatedObj.getManagerName() + "</>(으)로 등록하였습니다.");
        }
        if(bePresent(updatedObj.getEmail()) && !updatedObj.getEmail().equals(originShop.getEmail())){
            if(bePresent(originShop.getEmail()))
                stringList.add("정비소 이메일을 <>" + originShop.getEmail() + "</>에서 <>" + updatedObj.getEmail() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 이메일을 <>" +  updatedObj.getEmail() + "</>(으)로 등록하였습니다.");
        }
        if(bePresent(updatedObj.getRegNum()) && !updatedObj.getRegNum().equals(originShop.getRegNum())){
            if(bePresent(originShop.getRegNum()))
                stringList.add("정비소 이메일을 <>" + originShop.getRegNum() + "</>에서 <>" + updatedObj.getRegNum() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 이메일을 <>" +  updatedObj.getRegNum() + "</>(으)로 등록하였습니다.");
        }
        if(bePresent(updatedObj.getPhone()) && !updatedObj.getPhone().equals(originShopInfo.getPhone())){
            if(bePresent(originShopInfo.getPhone()))
                stringList.add("정비소 연락처를 <>" + originShopInfo.getPhone() + "</>에서 <>" + updatedObj.getPhone() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 연락처를 <>" +  updatedObj.getPhone() + "</>(으)로 등록하였습니다.");
        }
        if(bePresent(updatedObj.getStartTime()) && !updatedObj.getStartTime().equals(originShopInfo.getStartTime())){
            if(bePresent(originShopInfo.getStartTime()))
                stringList.add("정비소 영업 시작시간을 <>" + originShopInfo.getStartTime() + "</>에서 <>" + updatedObj.getStartTime() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 영업 시작시간을 <>" +  updatedObj.getStartTime() + "</>(으)로 등록하였습니다.");
        }
        if(bePresent(updatedObj.getEndTime()) && !updatedObj.getEndTime().equals(originShopInfo.getEndTime())){
            if(bePresent(originShopInfo.getEndTime()))
                stringList.add("정비소 영업 시작시간을 <>" + originShopInfo.getEndTime() + "</>에서 <>" + updatedObj.getEndTime() + "</>(으)로 변경하였습니다.");
            else stringList.add("정비소 영업 시작시간을 <>" +  updatedObj.getEndTime() + "</>(으)로 등록하였습니다.");
        }

        if(bePresent(stringList) && stringList.size() > 0)
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
        fetchSettleDetailResponse.setConfirmedUser(bySettleId.getConfirmedUser());
        fetchSettleDetailResponse.setBankAccount(bySettleId.getBankAccount());
        fetchSettleDetailResponse.setEstimates(allBySettle_settleId);
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



        return request;
    }
}

