package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "com_special_terms")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ComSpecialTerms extends OriginObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sterm_no")
    private Integer stermNo;

    @Column(name = "sterm_nm")
    private String stermName;

    @JsonIgnore
    @Column(name = "usable")
    private Boolean usable = false;

    @JsonIgnore
    @Column(name = "created_at", columnDefinition = "CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}
