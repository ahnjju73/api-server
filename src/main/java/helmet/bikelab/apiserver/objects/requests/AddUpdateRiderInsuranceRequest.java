package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.objects.BankInfoDto;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.RiderInfoDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateRiderInsuranceRequest extends OriginObject {
    private Integer dtlNo;
    private String insCompany;
    private String insNum;
    private ModelAddress address;
    private ModelAddress contractorAddress;
    private String age;
    private String insRange;
    private RiderInfoDto riderInfoDto;
    private RiderInfoDto contractInfoDto;
    private BankInfoDto bankInfoDto;

    private String bikeNum;
    private String vimNum;
    private String bikeType;
    private String shopId;

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

    private List<PresignedURLVo> newAttachments;
    private List<ModelAttachment> attachments;
    private String description;


    public void checkValidation(){
        if(!bePresent(insCompany))
            withException("");
        if(!bePresent(insNum))
            withException("");
        if(!bePresent(age))
            withException("");
        if(!bePresent(insRange))
            withException("");
        if(!bePresent(vimNum))
            withException("");
        if(!bePresent(bikeType))
            withException("");
        if(!bePresent(usage))
            withException("");
        if(!bePresent(additionalStandard))
            withException("");
    }

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
            this.endDt = LocalDateTime.parse(endDt + "T23:59:59");
        }
    }

}
