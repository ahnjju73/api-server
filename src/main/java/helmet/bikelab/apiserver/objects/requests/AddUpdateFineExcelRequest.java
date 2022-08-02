package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateFineExcelRequest extends OriginObject {

    private Integer fee;
    private String fineDate;
    private String fineExpireDate;
    private String fineLocation;
    private String fineOffice;
    private String violationReason;
    private String bikeNum;

    public void validationCheck(){


    }
}
