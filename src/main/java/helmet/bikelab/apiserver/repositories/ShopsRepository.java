package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.shops.Shops;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface ShopsRepository extends JpaRepository<Shops, Integer> {

    Shops findByEmail(String email);
    Shops findByRegNum(String regNum);
    Shops findByShopId(String shopId);
    Page<Shops> findAll(Pageable pageable);
    Page<Shops> findAllByShopInfo_NameContaining(String keyword, Pageable pageable);

}
