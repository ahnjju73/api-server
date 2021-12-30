package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.domain.types.converters.YesNoTypeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "parts_types")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parts_type_no", nullable = false)
    private Integer partsTypeNo;

    @Column(name = "parts_type")
    private String partsType;

    @Column(name = "order_no", columnDefinition = "TINYINT")
    private Integer orderNo;

    @Column(name = "usable")
    private Boolean usable = true;

    @Column(name = "is_free_support", columnDefinition = "ENUM")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes isFreeSupport = YesNoTypes.NO;

    @Column(name = "is_free_support", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String isFreeSupportCode;

}
