package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import helmet.bikelab.apiserver.domain.types.CommonWorkingPK;
import helmet.bikelab.apiserver.domain.types.converters.BikeTypesConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@IdClass(CommonWorkingPK.class)
@Table(name = "com_comm_working")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommonWorking {

    @Id
    @Column(name = "volume")
    private Double volume;

    @Id
    @Column(name = "bike_type", columnDefinition = "ENUM")
    private String bikeTypeCode;

    @Column(name = "bike_type", columnDefinition = "ENUM", insertable = false, updatable = false)
    @Convert(converter = BikeTypesConverter.class)
    private BikeTypes bikeType = BikeTypes.GAS;

    @Column(name = "working_price")
    private Integer workingPrice;


}
