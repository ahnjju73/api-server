package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.shops.ShopAttachments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopAttachmentRepository extends JpaRepository<ShopAttachments, Integer> {

}
