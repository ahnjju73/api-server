package helmet.bikelab.apiserver.objects;

import helmet.bikelab.apiserver.domain.types.EstimateTypes;
import lombok.Data;

import java.io.Serializable;

@Data
public class EstimatePartsPK implements Serializable {
    private Long partsNo;
    private Long estimateNo;
    private EstimateTypes estimateType;

}
