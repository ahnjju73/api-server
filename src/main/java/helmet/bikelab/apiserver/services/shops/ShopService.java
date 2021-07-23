package helmet.bikelab.apiserver.services.shops;

import helmet.bikelab.apiserver.domain.shops.ShopAddresses;
import helmet.bikelab.apiserver.domain.shops.ShopInfo;
import helmet.bikelab.apiserver.domain.shops.ShopPassword;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.repositories.ShopAddressesRepository;
import helmet.bikelab.apiserver.repositories.ShopInfoRepository;
import helmet.bikelab.apiserver.repositories.ShopPasswordRepository;
import helmet.bikelab.apiserver.repositories.ShopsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static helmet.bikelab.apiserver.utils.Utils.randomPassword;

@Service
@RequiredArgsConstructor
public class ShopService extends SessService {

    private final ShopsRepository shopsRepository;
    private final ShopInfoRepository shopInfoRepository;
    private final ShopPasswordRepository shopPasswordRepository;
    private final ShopAddressesRepository shopAddressesRepository;
    private final AutoKey autoKey;

    @Transactional
    public BikeSessionRequest registerNewShop(BikeSessionRequest request){
        Map param = request.getParam();
        String name = (String)param.get("name");
        String email = (String)param.get("email");
        String regNo = (String)param.get("reg_no");
        String shopId = autoKey.makeGetKey("shop");
        Shops shop = new Shops();
        shop.setEmail(email);
        shop.setShopId(shopId);
        shop.setRegNum(regNo);
        shopsRepository.save(shop);

        ShopInfo shopInfo = new ShopInfo();
        shopInfo.setShop(shop);
        shopInfo.setShopNo(shop.getShopNo());
        shopInfo.setName(name);
        shopInfoRepository.save(shopInfo);

        String password = randomPassword(10);
        ShopPassword shopPassword = new ShopPassword();
        shopPassword.setShop(shop);
        shopPassword.setShopNo(shop.getShopNo());
        shopPassword.makePassword(password);
        shopPasswordRepository.save(shopPassword);

        ShopAddresses shopAddresses = new ShopAddresses();
        shopAddresses.setShop(shop);
        shopAddresses.setShopNo(shop.getShopNo());
        shopAddressesRepository.save(shopAddresses);

        return request;
    }

}

