package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.CommonCodeInsurances;
import helmet.bikelab.apiserver.domain.bikelab.Inquiries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiriesRepository extends JpaRepository<Inquiries, String> {
    Inquiries findByInquiryNo(Long inquiryNo);
}
