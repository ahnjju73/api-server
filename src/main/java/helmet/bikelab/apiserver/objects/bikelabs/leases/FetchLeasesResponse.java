package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.lease.LeaseExpense;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.bikelabs.fine.FetchFinesResponse;
import helmet.bikelab.apiserver.objects.bikelabs.release.ReleaseDto;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.InsuranceDto;
import helmet.bikelab.apiserver.objects.requests.StopLeaseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private List<FetchFinesResponse> fines;
    private UserDto createdUser;
    private UserDto submittedUser;
    private UserDto approvalUser;
    private List<LeasePaymentDto> leasePayments = new ArrayList<>();
    private StopLeaseDto stopLeaseInfo;
    private List<ExpenseDto> expense;
    private String demandLeaseId;
    private String riderId;
    private String bakRiderLeaseAttachments;
    private String bakRiderLeaseSpecialTerms;
}
