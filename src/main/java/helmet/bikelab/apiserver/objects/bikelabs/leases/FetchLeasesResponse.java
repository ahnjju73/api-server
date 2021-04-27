package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.BikeDto;
import helmet.bikelab.apiserver.objects.bikelabs.release.ReleaseDto;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.InsuranceDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchLeasesResponse {
    private String leaseId;
    private String bikeId;
    private String clientId;
    private String insuranceId;
    private String releaseId;
    private String status;
    private String contractType;
    private String managementType;
    private String takeLoc;
    private LocalDateTime takeAt;
    private LocalDateTime releaseAt;
    private LocalDateTime createdAt;
    private BikeDto bike;
    private ClientDto client;
    private InsuranceDto insurance;
    private ReleaseDto release;
    private LeaseInfoDto leaseInfo;
    private LeasePriceDto leasePrice;
}
