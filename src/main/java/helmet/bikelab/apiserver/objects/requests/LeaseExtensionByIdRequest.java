package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LeaseExtensionByIdRequest extends LeaseByIdRequest {

    private Integer period;
    private LocalDate startDt;

    public void checkValidation(){
        if(!bePresent(startDt)) withException("853-001");
        if(!bePresent(period)) withException("853-002");
        if(period < 1) withException("853-003");
    }

    public LocalDate getEndDate(){
        return startDt.plusMonths(period);
    }

    public LocalDate getEndDateByLeaseInfo(){
        return startDt.plusMonths(period + 1);
    }

    public void setStartDt(String startDt) {
        if(bePresent(startDt)){
            try{
                LocalDate parse = LocalDate.parse(startDt);
                this.startDt = parse;
            }catch (Exception e){

            }
        }
    }
}
