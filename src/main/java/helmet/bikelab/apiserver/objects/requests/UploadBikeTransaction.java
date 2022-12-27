package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UploadBikeTransaction extends OriginObject {

    private String companyName;
    private String regNum;
    private Integer price;
    private LocalDateTime receiveDt;
    private Integer discount;
    private Integer consignmentPrice;
    // 탁송료 (구매처)
    private String receiveDescription;


    /*
    판매처 정보
     */
    private String sellCompanyName;
    private String sellRegNum;
    private Integer sellPrice;
    private LocalDateTime soldDate;
    private String sellDescription;
    private Integer sellDiscount;
    // 탁송료 (구매처)
    private Integer sellConsignmentPrice;


    public void checkValidation(StringBuilder errorText){
        if(!bePresent(companyName)) errorText.append("구매처 상호명 입력해주세요.\n");
        if(!bePresent(regNum)) errorText.append("구매처 사업자번호 입력해주세요.\n");
        if(!bePresent(price)) errorText.append("구매처 금액 입력해주세요.\n");
        if(!bePresent(receiveDt)) errorText.append("구매처 수령일자 입력해주세요.\n");
        if(!bePresent(discount)) errorText.append("구매 할인률 입력해주세요.\n");
        if(!bePresent(consignmentPrice)) errorText.append("탁송료 입력해주세요.\n");

        if(!bePresent(sellCompanyName)) errorText.append("판매처 상호명 입력해주세요.\n");
        if(!bePresent(sellRegNum)) errorText.append("판매처 사업자번호 입력해주세요.\n");
        if(!bePresent(sellPrice)) errorText.append("판매처 금액 입력해주세요.\n");
        if(!bePresent(soldDate)) errorText.append("판매처 수령일자 입력해주세요.\n");
        if(!bePresent(sellDiscount)) errorText.append("판매 할인률 입력해주세요.\n");
        if(!bePresent(sellConsignmentPrice)) errorText.append("탁송료 입력해주세요.\n");

    }

    // Setter
    public void setReceiveDt(String receiveDt) {
        try{
            if(bePresent(receiveDt)) this.receiveDt = LocalDateTime.parse(receiveDt);
        }catch (Exception e){

        }
    }

    public void setSoldDate(String soldDate) {
        try{
            if(bePresent(soldDate)) this.soldDate = LocalDateTime.parse(soldDate);
        }catch (Exception e){

        }
    }

    public void setReceiveDt(LocalDateTime receiveDt) {
        this.receiveDt = receiveDt;
    }

    public void setSoldDate(LocalDateTime soldDate) {
        this.soldDate = soldDate;
    }
}
