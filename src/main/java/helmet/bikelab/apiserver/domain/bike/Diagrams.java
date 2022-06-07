package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.types.converters.PartsBackUpConverter;
import helmet.bikelab.apiserver.domain.types.converters.PartsImagesConverter;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.PartsBackUpDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "diagrams")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Diagrams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagram_no")
    private Integer diagramNo;

    @Column(name = "diagram_id", unique = true)
    private String diagramId;

    @Column(name = "car_model")
    private String carModelCode;

    @ManyToOne
    @JoinColumn(name = "car_model", insertable = false, updatable = false)
    private CommonBikes carModel;

    @JsonIgnore
    @Column(name = "images", columnDefinition = "json")
    @Convert(converter = PartsImagesConverter.class)
    private List<ImageVo> imageList;

    @Column(name = "images", columnDefinition = "json", insertable = false, updatable = false)
    private String images;

}
