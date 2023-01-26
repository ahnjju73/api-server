package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.domain.types.BusinessTypes;
import helmet.bikelab.apiserver.domain.types.InquiryStatusTypes;
import helmet.bikelab.apiserver.domain.types.InquiryType;
import helmet.bikelab.apiserver.domain.types.converters.BusinessTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.InquiryStatusTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.InquiryTypeConverter;
import helmet.bikelab.apiserver.domain.types.converters.ModelAddressConverter;
import helmet.bikelab.apiserver.objects.AddressDto;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "inquiries", catalog = SESSION.SCHEME_SERVICE)
public class Inquiries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_no")
    private Long inquiryNo;

    @Column(name = "status", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = InquiryStatusTypesConverter.class)
    private InquiryStatusTypes inquiryStatusTypes = InquiryStatusTypes.PENDING;

    @Column(name = "business_type", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = BusinessTypesConverter.class)
    private BusinessTypes businessType = BusinessTypes.CORPORATE;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 45)
    private String phone;

    @Column(name = "company")
    private String company;

    @Column(name = "type", columnDefinition = "ENUM")
    @Convert(converter = InquiryTypeConverter.class)
    private InquiryType inquiryType = InquiryType.LEASE;

    @Column(name = "type", insertable = false, updatable = false)
    private String inquiryTypeCode;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "confirmed_user_no")
    private Integer confirmedUserNo;

    @ManyToOne
    @JoinColumn(name = "confirmed_user_no", insertable = false, updatable = false)
    private BikeUser confirmedUser;

    @Column(name = "address", columnDefinition = "JSON")
    @Convert(converter = ModelAddressConverter.class)
    private AddressDto address;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

}

