package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.lease.LeaseInfo;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LeaseInfoDto {
    private Integer period;
    private String startDt;
    private String contractDt;
    private String note;
    private String endDt;

    public void setLeaseInfo(LeaseInfo leaseInfo){
        startDt = leaseInfo.getStart().toString();
        note = leaseInfo.getNote();
        if(leaseInfo.getContractDate()!=null)
            contractDt = leaseInfo.getContractDate().toString();
    }
}
