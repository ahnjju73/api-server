package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.types.InquiryStatusTypes;
import helmet.bikelab.apiserver.domain.types.converters.ConsultingDescriptionConverter;
import helmet.bikelab.apiserver.domain.types.converters.InquiryStatusTypesConverter;
import helmet.bikelab.apiserver.objects.ConsultingDescriptionDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "inquiry_rider_insurances")
public class InquiryRiderInsurances {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_no")
    private Integer inquiryNo;

    @Column(name = "inquiry_id")
    private String inquiryId;

    @Column(name = "name")
    private String name;

    @Column(name = "birth")
    private String birth;

    @Column(name = "phone")
    private String phone;

    @Column(name = "description", columnDefinition = "json")
    @Convert(converter = ConsultingDescriptionConverter.class)
    private List<ConsultingDescriptionDto> descriptions;

    @Column(name = "status", columnDefinition = "ENUM")
    @Convert(converter = InquiryStatusTypesConverter.class)
    private InquiryStatusTypes status = InquiryStatusTypes.PENDING;

    @Column(name = "status", columnDefinition = "ENUM", insertable = false, updatable = false)
    private InquiryStatusTypes statusCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "consulted_by")
    private Integer consultedBy;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consulted_by", insertable = false, updatable = false)
    private BikeUser consultedUser;
}
