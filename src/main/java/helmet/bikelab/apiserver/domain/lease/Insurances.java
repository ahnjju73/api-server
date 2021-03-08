package helmet.bikelab.apiserver.domain.lease;


import helmet.bikelab.apiserver.domain.types.SecurityTypes;
import helmet.bikelab.apiserver.domain.types.converters.SecurityTypeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "insurances")
public class Insurances {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insurance_no", nullable = false)
    private Integer insuranceNo;

    @Column(name = "security", columnDefinition = "ENUM")
    @Convert(converter = SecurityTypeConverter.class)
    private SecurityTypes securityType;

    @Column(name = "insurance_age", columnDefinition = "TINYINT")
    private Integer age;

    @Column(name = "company_name", length = 45)
    private String companyName;

    @Column(name = "insurance_fee")
    private Integer insuranceFee;

    @Column(name = "bm_care")
    private Integer bmCare;

}
