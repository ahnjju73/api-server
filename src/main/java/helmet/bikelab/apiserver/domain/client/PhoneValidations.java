package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "phone_validations", catalog = SESSION.SCHEME_SERVICE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PhoneValidations extends OriginObject {

    @Id
    @Column(name = "phone")
    private String phone;

    @Column(name = "validation_code", length = 45)
    private String validationCode;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "validation_at")
    private LocalDateTime validationAt;

}
