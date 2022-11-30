package helmet.bikelab.apiserver.domain.riders;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.types.InsAgeTypes;
import helmet.bikelab.apiserver.domain.types.InsuranceBikeTypes;
import helmet.bikelab.apiserver.domain.types.converters.InsAgeTypeConverter;
import helmet.bikelab.apiserver.domain.types.converters.InsuranceBikeTypeConverter;
import helmet.bikelab.apiserver.domain.types.converters.ModelAddressConverter;
import helmet.bikelab.apiserver.objects.AddressDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "rider_insurances")
public class RiderInsurances extends OriginObject {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rider_ins_no")
    private Integer riderInsNo;

    @Column(name = "rider_no")
    private Integer riderNo;

    @Column(name = "rider_ins_id")
    private String riderInsId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @OneToMany(mappedBy = "riderInsurances", fetch = FetchType.EAGER)
    private Set<RiderInsurancesDtl> riderInsurancesDtls;

    @OneToOne(mappedBy = "riderInsurance", fetch = FetchType.EAGER)
    private RiderInsuranceHistories riderInsuranceHistories;

    @Column(name = "rider_email")
    private String riderEmail;

    @Column(name = "rider_phone")
    private String riderPhone;

    @Column(name = "rider_name")
    private String riderName;

    @Column(name = "rider_ssn")
    private String riderSsn;

    @Column(name = "rider_address", columnDefinition = "JSON")
    @Convert(converter = ModelAddressConverter.class)
    AddressDto riderAddress;

    @Column(name = "rider_age", columnDefinition = "ENUM")
    @Convert(converter = InsAgeTypeConverter.class)
    private InsAgeTypes age;

    @Column(name = "rider_age", columnDefinition = "ENUM",insertable = false, updatable = false)
    private String ageCode;

    @Column(name = "bike_num")
    private String bikeNum;

    @Column(name = "vim_num")
    private String vimNum;

    @Column(name = "bike_type", columnDefinition = "ENUM")
    @Convert(converter = InsuranceBikeTypeConverter.class)
    private InsuranceBikeTypes bikeTypes;

    @Column(name = "bike_type", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String bikeTypesCode;

}
