package helmet.bikelab.apiserver.domain.embeds;


import helmet.bikelab.apiserver.objects.requests.UpdateBikeTransactionRequest;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class ModelBikeTransaction extends ModelTransaction{
    public ModelBikeTransaction(){}
    public ModelBikeTransaction(String regNum, String companyName, Integer price){
        this.regNum = regNum;
        this.companyName = companyName;
        this.price = price;
    }

    /*
    구매처 정보
    지출일자 : receive_dt
     */
    @Column(name = "reg_num")
    private String regNum;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "price")
    private Integer price;

    // 탁송료 (구매처)
    @Column(name = "consignment_price")
    private Integer consignmentPrice;

    @Column(name = "discount")
    private Integer discount;


    /*
    판매처 정보
     */
    @Column(name = "sell_reg_num")
    private String sellRegNum;

    @Column(name = "sell_company_name")
    private String sellCompanyName;

    @Column(name = "sell_price")
    private Integer sellPrice;

    // 탁송료 (구매처)
    @Column(name = "sell_consignment_price")
    private Integer sellConsignmentPrice;

    @Column(name = "sell_discount")
    private Integer sellDiscount;

    @Column(name = "sold_dt")
    private LocalDateTime soldDate;

    public void updateTransactionInfo(UpdateBikeTransactionRequest transactionRequest){
        this.regNum = transactionRequest.getRegNum();
        this.companyName = transactionRequest.getCompanyName();
        this.price = transactionRequest.getPrice();
        this.consignmentPrice = transactionRequest.getConsignmentPrice();
        this.discount = transactionRequest.getDiscount();
        this.sellRegNum = transactionRequest.getSellRegNum();
        this.sellCompanyName = transactionRequest.getSellCompanyName();
        this.sellConsignmentPrice = transactionRequest.getSellConsignmentPrice();
        this.sellPrice = transactionRequest.getSellPrice();
        this.sellDiscount = transactionRequest.getSellDiscount();
        this.soldDate = transactionRequest.getSoldDate();
    }

}
