package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.types.UnitTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.domain.types.converters.PartsBackUpConverter;
import helmet.bikelab.apiserver.domain.types.converters.PartsImagesConverter;
import helmet.bikelab.apiserver.domain.types.converters.UnitTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.YesNoTypeConverter;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.PartsBackUpDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "parts")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Parts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parts_no")
    private Long partNo;

    @Column(name = "parts_id", unique = true)
    private String partsId;

    @Column(name = "parts_code_no")
    private Integer partsCodeNo;

    @ManyToOne
    @JoinColumn(name = "parts_code_no", insertable = false, updatable = false)
    private PartsCodes partsCode;

    @Column(name = "parts_prices")
    private Integer partsPrices;

    @Column(name = "working_prices")
    private Integer workingPrices;

    @Column(name = "working_hours")
    private Double workingHours;

    @Column(name = "units", columnDefinition = "ENUM")
    @Convert(converter = UnitTypesConverter.class)
    private UnitTypes units = UnitTypes.EA;

    @Column(name = "units", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String unitsCode;

    @Column(name = "bike_model_cd")
    private String bikeModelCode;

    @ManyToOne
    @JoinColumn(name = "bike_model_cd", insertable = false, updatable = false)
    private CommonCodeBikes bikeModel;

    @Column(name = "is_free_support", columnDefinition = "ENUM")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes isFreeSupport = YesNoTypes.NO;

    @Column(name = "is_free_support", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String isFreeSupportCode;

    @Column(name = "bakup", columnDefinition = "json")
    @Convert(converter = PartsBackUpConverter.class)
    private List<PartsBackUpDto> backUpList;

    @Column(name = "images", columnDefinition = "json")
    @Convert(converter = PartsImagesConverter.class)
    private List<PartsImages> images;

}