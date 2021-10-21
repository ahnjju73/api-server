package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.types.RiderVerifiedRequestTypes;
import helmet.bikelab.apiserver.domain.types.converters.RiderVerifiedRequestTypesConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "rider_verified")
public class RiderVerified {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verified_no")
    private Long verifiedNo;

    @Column(name = "request_type", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = RiderVerifiedRequestTypesConverter.class)
    private RiderVerifiedRequestTypes requestType = RiderVerifiedRequestTypes.REQUEST;

    @Column(name = "request_type", columnDefinition = "ENUM", nullable = false, insertable = false, updatable = false)
    private String requestTypeCode;

    @Column(name = "rider_no")
    private Integer riderNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @Column(name = "domain", length = 256)
    private String domain;

    @Column(name = "uri", length = 256)
    private String uri;

}
