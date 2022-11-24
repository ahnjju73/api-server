package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.InquiryRiderInsurances;
import helmet.bikelab.apiserver.domain.types.InquiryStatusTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRiderInsurancesRepository extends JpaRepository<InquiryRiderInsurances, Integer> {
    Page<InquiryRiderInsurances> findAllByOrderByInquiryNoDesc(Pageable pageable);
    Page<InquiryRiderInsurances> findAllByStatusOrderByInquiryNoDesc(InquiryStatusTypes inquiryStatusTypes, Pageable pageable);
    Page<InquiryRiderInsurances> findAllByNameContainingOrderByInquiryNoDesc(String name, Pageable pageable);
    Page<InquiryRiderInsurances> findAllByPhoneContainingOrderByInquiryNoDesc(String phone, Pageable pageable);
    Page<InquiryRiderInsurances> findAllByPhoneContainingAndStatusOrderByInquiryNoDesc(String phone, InquiryStatusTypes status, Pageable pageable);

    InquiryRiderInsurances findByInquiryId(String inquiryId);
}
