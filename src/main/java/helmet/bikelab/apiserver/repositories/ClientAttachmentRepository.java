package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.BikeAttachments;
import helmet.bikelab.apiserver.domain.client.ClientAttachments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientAttachmentRepository extends JpaRepository<ClientAttachments, Integer> {
    List<ClientAttachments> findAllByClient_ClientId(String clientId);
    ClientAttachments findByAttachNo(Integer clientAttachmentNo);

}
