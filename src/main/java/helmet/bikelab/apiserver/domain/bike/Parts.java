package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.UnitTypes;
import helmet.bikelab.apiserver.domain.types.converters.PartsBackUpConverter;
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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parts_no")
    private Long partNo;

    @Column(name = "parts_code_no")
    private Integer partsCodeNo;

    @Column(name = "parts_prices")
    private Integer partsPrices;

    @Column(name = "working_prices")
    private Integer workingPrices;

    @Column(name = "working_hours")
    private Double workingHours;

    @Column(name = "units", columnDefinition = "ENUM")
    private UnitTypes units = UnitTypes.EA;

    @Column(name = "bike_model_cd")
    private String bikeModelCode;

    @Column(name = "bakup", columnDefinition = "json")
    @Convert(converter = PartsBackUpConverter.class)
    private List<Parts> bakup;

}
