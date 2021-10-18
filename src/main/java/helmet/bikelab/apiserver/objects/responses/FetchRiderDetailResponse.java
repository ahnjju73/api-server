package helmet.bikelab.apiserver.objects.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.RiderLeaseRequestedTypes;
import helmet.bikelab.apiserver.domain.types.RiderVerifiedTypes;
import helmet.bikelab.apiserver.domain.types.converters.RiderLeaseRequestedTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.RiderVerifiedTypesConverter;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.RiderInfoDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchRiderDetailResponse extends OriginObject {
    private Integer riderNo;
    private String riderId;
    private LocalDateTime createdAt;
    private List<BikeDto> leasingBikes;
    private RiderInfoDto riderInfo;

    private RiderVerifiedTypes verifiedType;
    private String verifiedTypeCode;
    private LocalDateTime verifiedAt;
    private LocalDateTime verifiedRequestAt;
    private String verifiedRejectMessage;

    private RiderLeaseRequestedTypes leaseRequestedType;
    private String leaseRequestedTypeCode;
    private LocalDateTime leaseRequestedAt;
    private String leaseRequestUrl;

    public void setVerifiedType(RiderVerifiedTypes verifiedType) {
        this.verifiedType = verifiedType;
        this.verifiedTypeCode = verifiedType.getVerifiedType();
    }

    public void setLeaseRequestedType(RiderLeaseRequestedTypes leaseRequestedType) {
        this.leaseRequestedType = leaseRequestedType;
        this.leaseRequestedTypeCode = leaseRequestedType.getLeaseRequested();
    }
}
