package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.BikeModelDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeaseInfoDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RiderDemandLeasesDto {
    private String riderId;
    private String leaseId;
    private BikeModelDto bike;
    private String demandLeaseStatus;
    private LeaseInfoDto leaseInfo;
    private String managementType;
    private String expireType;
    private Integer prepayment;
    private String insuranceType;
    private String paymentType;
    private Boolean isMaintenance;
    private String rejectMessage;
    private LocalDateTime createdAt;
    private LocalDateTime rejectedAt;

}
