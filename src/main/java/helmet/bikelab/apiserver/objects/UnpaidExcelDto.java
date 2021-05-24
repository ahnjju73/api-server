package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UnpaidExcelDto {
    private String leaseId;
    private String bikeId;
    private String clientId;
    private String vimNumber;
    private String bikeNumber;
    private String clientName;
    private Integer unpaidFee;
}
