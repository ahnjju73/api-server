package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.types.EstimateTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.domain.types.converters.EstimateTypeConverter;
import helmet.bikelab.apiserver.domain.types.converters.YesNoTypeConverter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
@Entity
@Getter
@Setter
@Table(name = "estimate_parts")
public class EstimateParts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long index;

    @Column(name = "parts_no")
    private Long partsNo;

    @ManyToOne
    @JoinColumn(name = "parts_no", insertable = false, updatable = false)
    private Parts part;

    @Column(name = "estimate_no")
    private Long estimateNo;

    @Column(name = "estimate_type", columnDefinition = "ENUM")
    @Convert(converter = EstimateTypeConverter.class)
    private EstimateTypes estimateType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "estimate_no", insertable = false, updatable = false)
    private Estimates estimate;

    @Column(name = "number")
    private Integer number;

    // Default 는 계산된 금액이지만, 사용자가 변경할수도 있는 금액이다.
    @Column(name = "price")
    private Integer price;

    @Column(name = "parts_price")
    private Integer partsPrice;

    @Column(name = "working_price")
    private Integer workingPrice;

    @Column(name = "hours")
    private Double hours;

    @Column(name = "is_free_support", columnDefinition = "ENUM")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes isFreeSupport;

    @Column(name = "is_free_support", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String isFreeSupportCode;

    @Column(name = "client_discount_rate")
    private Double clientDiscountRate;

    @Column(name = "parts_type_discount_rate")
    private Double partsTypeDiscountRate;

    @Column(name = "parts_name", length = 45)
    private String partsName;

    @Column(name = "parts_type_name", length = 45)
    private String partsTypeName;

    public Integer getNumber() {
        return number == null ? 1 : number;
    }

    public Integer getWorkingPrice() {
        return workingPrice == null ? 0 : workingPrice;
    }

}
