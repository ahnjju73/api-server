package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateRiderInsuranceDtlRequest {
    private Integer dtlNo;
    private String insCompany;
    private String insNum;
    private String ssn;
    private ModelAddress address;
    private String age;
    private String insRange;
    private BankInfoDto bankInfoDto;

    private String usage;
    private String additionalStandard;

    private Integer liabilityMan;
    private Integer liabilityCar;
    private Integer liabilityMan2;
    private Integer selfCoverMan;
    private Integer selfCoverCar;
    private Integer noInsuranceCover;

    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private Integer insFee;

    private String description;

    public void setStartDt(String startDt){
        try {
            this.startDt = LocalDateTime.parse(startDt);
        }catch (Exception e){
            this.startDt = LocalDateTime.parse(startDt + "T00:00:00");
        }
    }

    public void setEndDt(String endDt){
        try {
            this.endDt = LocalDateTime.parse(endDt);
        }catch (Exception e){
            this.endDt = LocalDateTime.parse(endDt + "T00:00:00");
        }
    }
}
