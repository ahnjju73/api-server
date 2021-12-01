package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.PartsTypeDiscountClient;
import helmet.bikelab.apiserver.domain.bike.PartsTypes;
import helmet.bikelab.apiserver.domain.types.PartsTypeDiscountClientPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartsTypeDiscountClientRepository extends JpaRepository<PartsTypeDiscountClient, PartsTypeDiscountClientPK> {

    void deleteAllByClientNo(Integer clientNo);

}
