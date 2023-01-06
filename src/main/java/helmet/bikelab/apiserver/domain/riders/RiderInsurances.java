package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.types.InsAgeTypes;
import helmet.bikelab.apiserver.domain.types.InsuranceBikeTypes;
import helmet.bikelab.apiserver.domain.types.converters.InsAgeTypeConverter;
import helmet.bikelab.apiserver.domain.types.converters.InsuranceBikeTypeConverter;
import helmet.bikelab.apiserver.domain.types.converters.ModelAddressConverter;
import helmet.bikelab.apiserver.domain.types.converters.ModelAttachmentConverter;
import helmet.bikelab.apiserver.objects.AddressDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "rider_insurances")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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

    @Column(name = "shop_no")
    private Integer shopNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

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

    @Column(name = "contractor_phone")
    private String contractorPhone;

    @Column(name = "contractor_name")
    private String contractorName;

    @Column(name = "contractor_ssn")
    private String contractorSsn;

    @Column(name = "contractor_address", columnDefinition = "JSON")
    @Convert(converter = ModelAddressConverter.class)
    AddressDto contractorAddress;

    @JsonIgnore
    @Column(name = "attachments", columnDefinition = "JSON")
    @Convert(converter = ModelAttachmentConverter.class)
    private List<ModelAttachment> attachmentsList = new ArrayList<>();

    @Column(name = "attachments", columnDefinition = "JSON", updatable = false, insertable = false)
    private String attachments;

}
