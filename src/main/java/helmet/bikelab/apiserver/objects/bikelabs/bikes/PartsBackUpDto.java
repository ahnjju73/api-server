package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.domain.types.UnitTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsBackUpDto {

    private Long partNo;
    private String partsId;
    private Integer partsCodeNo;
    private PartsCodes partsCode;
    private Integer partsPrices;
    private Integer workingPrices;
    private Double workingHours;
    private UnitTypes units = UnitTypes.EA;
    private String bikeModelCode;
    private CommonBikes bikeModel;
    private LocalDateTime updatedAt = LocalDateTime.now();
    private String isFreeSupportCode;
    private YesNoTypes isFreeSupport;

}
