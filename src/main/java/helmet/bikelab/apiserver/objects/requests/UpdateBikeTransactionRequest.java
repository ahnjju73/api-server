package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateBikeTransactionRequest extends OriginObject {
    // 구매처
    private String regNum;
    private String companyName;
    private Integer price;
    private Integer discount;
    private Integer consignmentPrice;
    private LocalDateTime receiveDt;

    // 판매처
    private String sellRegNum;
    private String sellCompanyName;
    private Integer sellPrice;
    private Integer sellConsignmentPrice;
    private Integer sellDiscount;

    private LocalDateTime soldDate;
    public void setReceiveDate(String receiveDt) {
        try {
            this.receiveDt = LocalDateTime.parse(receiveDt + "T00:00:00");
        }catch (Exception e){
            this.receiveDt = LocalDateTime.parse(receiveDt);
        }
    }

    public void setSoldDate(String soldDate) {
        try {
            this.soldDate = LocalDateTime.parse(soldDate + "T00:00:00");
        }catch (Exception e){
            this.soldDate = LocalDateTime.parse(soldDate);
        }
    }
}
