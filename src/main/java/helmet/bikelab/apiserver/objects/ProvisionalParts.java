package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProvisionalParts extends OriginObject {
    private Long idx;
    private Long partsNo;
    private Integer number;
    private String estimateType;
    private String partsName;
    private String partsTypeName;
    private Integer partsPrice;
    private Double hours;
    private String isFreeSupport;
    private Integer originPartsPrice;
    private Integer originWorkingPrice;
    private Integer workingPrice;
    private Integer totalPrice;

    public void setParts(ProvisionalParts parts) {
        idx = parts.getIdx();
        partsNo = parts.partsNo;
        number = parts.number;
        estimateType = parts.estimateType;
        partsName = parts.partsName;
        partsTypeName = parts.partsTypeName;
        partsPrice = parts.partsPrice;
        hours = parts.hours;
        isFreeSupport = parts.isFreeSupport;
        originPartsPrice = parts.originPartsPrice;
        originWorkingPrice = parts.originWorkingPrice;
        workingPrice = parts.workingPrice;
    }
}
