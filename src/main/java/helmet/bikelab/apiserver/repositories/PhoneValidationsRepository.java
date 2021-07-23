package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.PhoneValidations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneValidationsRepository extends JpaRepository<PhoneValidations, Long> {

    PhoneValidations findByPhoneAndValidationCodeAndValidationAtIsNull(String phone, String validationCode);
    PhoneValidations findByPhoneAndValidationCodeAndValidationAtIsNotNull(String phone, String validationCode);
    PhoneValidations findByPhone(String phone);

}
