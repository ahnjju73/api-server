package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.shops.ShopAttachments;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.objects.requests.PageableRequest;
import helmet.bikelab.apiserver.repositories.ShopsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopWorker extends SessService {

    private final ShopsRepository shopsRepository;

    public Page<Shops> getAllShopByPageableRequest(PageableRequest pageableRequest, String keyword){
        if(keyword == null || keyword.isBlank()){
            return getAllShopByPageableRequest(pageableRequest);
        }
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        Page<Shops> all = shopsRepository.findAllByShopInfo_NameContaining(keyword, pageable);
        return all;
    }

    public Page<Shops> getAllShopByPageableRequest(PageableRequest pageableRequest){
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        Page<Shops> all = shopsRepository.findAll(pageable);
        return all;
    }

    public Boolean checkIfEmailExists(String email){
        Shops byEmail = shopsRepository.findByEmail(email);
        return bePresent(byEmail);
    }

    public Boolean checkIfRegNumExists(String regNum){
        Shops byEmail = shopsRepository.findByRegNum(regNum);
        return bePresent(byEmail);
    }

    public Shops getShopByShopId(String shopId){
        Shops byShopId = shopsRepository.findByShopId(shopId);
        if(!bePresent(byShopId)) withException("401-101");
        return byShopId;
    }

    public ShopAttachments removeAttachment(ShopAttachments shopAttachments, String uuid){
        for(ModelAttachment ma : shopAttachments.getAttachmentsList()){
            if(ma.getUuid().equals(uuid)){
                shopAttachments.getAttachmentsList().remove(ma);
            }
        }
        return shopAttachments;
    }

}
