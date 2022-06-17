package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.embeds.ModelReview;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.types.BusinessTypes;
import helmet.bikelab.apiserver.domain.types.EstimateStatusTypes;
import helmet.bikelab.apiserver.domain.types.PayerTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.domain.types.converters.BusinessTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.EstimateStatusTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.PayerTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.YesNoTypeConverter;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "estimates", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Estimates extends OriginObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estimate_no")
    private Long estimateNo;

    @Column(name = "estimate_id", length = 21, unique = true, nullable = false)
    private String estimateId;

    @Column(name = "payer_types", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = PayerTypesConverter.class)
    private PayerTypes payerTypes = PayerTypes.COMPANY;

    @Column(name = "payer_types", columnDefinition = "ENUM", nullable = false, insertable = false, updatable = false)
    private String payerTypeCode;

    @Column(name = "status", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = EstimateStatusTypesConverter.class)
    private EstimateStatusTypes estimateStatusType = EstimateStatusTypes.IN_PROGRESS;

    @Column(name = "status", columnDefinition = "ENUM", nullable = false, insertable = false, updatable = false)
    private String estimateStatusTypeCode;

    @Column(name = "client_no")
    private Integer clientNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @Column(name = "shop_no")
    private Integer shopNo;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    @JsonIgnore
    @Column(name = "rider_no")
    private Integer riderNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @JsonIgnore
    @Column(name = "bike_no", nullable = false)
    private Integer bikeNo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "is_released", columnDefinition = "ENUM")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes isReleased = YesNoTypes.NO;

    @Column(name = "accident", columnDefinition = "ENUM")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes accident = YesNoTypes.NO;

    @Column(name = "accident", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String accidentCode = YesNoTypes.NO.getYesNo();

    @Column(name = "rate_accident")
    private Integer rateAccident;

    @Column(name = "created_at", columnDefinition = "default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "pending1_at")
    private LocalDateTime pending1At;

    @Column(name = "pending2_at")
    private LocalDateTime pending2At;

    @Column(name = "declined1_at")
    private LocalDateTime declined1At;

    @Column(name = "declined2_at")
    private LocalDateTime declined2At;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "confirm_at")
    private LocalDateTime confirmAt;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @Column(name = "total_price")
    private Integer totalPrice = 0;

    @Column(name = "supply_price")
    private Integer supplyPrice = 0;

    @JsonIgnore
    @Column(name = "bm_support_fee")
    private Integer bmSupportFee = 0;

    //카드수수료
    @Column(name = "service_fee")
    private Integer serviceFee = 0;

    //정비소
    @Column(name = "shop_fee")
    private Integer shopFee = 0;

    //30%
    @Column(name = "bm_fee")
    private Integer bmFee = 0;

    //샵금액에서 나갈 세금
    @Column(name = "shop_tax")
    private Integer shopTax = 0;

    @Column(name = "bm_tax")
    private Integer bmTax = 0;

    @Column(name = "paid_fee")
    private Integer paidFee = 0;

    @JsonIgnore
    @Column(name = "imp_uid", length = 64)
    private String importUid;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "decline_reason", columnDefinition = "MEDIUMTEXT")
    private String declineReason;

    @Column(name = "odometer")
    private Integer odometer;

    @Column(name = "business_type", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = BusinessTypesConverter.class)
    private BusinessTypes businessType = BusinessTypes.CORPORATE;

    @Column(name = "business_type", columnDefinition = "ENUM", nullable = false, insertable = false, updatable = false)
    private String businessTypeCode;

    @Column(name = "reg_no", length = 45)
    private String regNum;

    @Embedded
    private ModelReview review;

    @Column(name = "settle_no")
    private Long settleNo;

    @ManyToOne
    @JoinColumn(name = "settle_no", insertable = false, updatable = false)
    private Settles settle;
    ;

    @JsonIgnore
    public void payingStart(){
        this.paidAt = LocalDateTime.now();
        this.serviceFee = 0;
        this.shopFee = 0;
        this.bmFee = 0;
        this.shopTax = 0;
        this.bmTax = 0;
    }

    @JsonIgnore
    public void estimatePaymentDone(Integer price){
        double taxRate = 0.1;
        Double shopRate = (shop.getRate() * 0.01);
        Double bikemasterRate = ((100 - shop.getRate()) * 0.01);
        int serviceFee = ((Double)(price * 0.03)).intValue();
        int totalPrice = price - serviceFee;
        int shopTotalFee = ((Double)(totalPrice * shopRate)).intValue();
        int bikemasterTotalFee = totalPrice - shopTotalFee;
        int shopTax = ((Double)(shopTotalFee * taxRate)).intValue();
        int shopFee = shopTotalFee - shopTax;
        int bikemasterTax = ((Double)(bikemasterTotalFee * taxRate)).intValue();
        int bikemasterFee = bikemasterTotalFee - bikemasterTax;

        this.totalPrice = price;
        this.serviceFee = serviceFee;
        this.shopFee = shopFee;
        this.shopTax = shopTax;
        this.bmFee = bikemasterFee;
        this.bmTax = bikemasterTax;
    }

    public void isAvailableReview(){
        if(review != null && bePresent(review.getReview())) withException("3303-005");
    }


}
