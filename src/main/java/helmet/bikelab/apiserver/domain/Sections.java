package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.domain.types.converters.ImageVoConverter;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "sections", catalog = SESSION.SCHEME_SERVICE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Sections extends OriginObject {

    public Sections(){}

    public Sections(String sectionName, CommonBikes carModel, List<ImageVo> images){
        this.carModel = carModel;
        this.carModelCode = carModel.getCode();
        this.imageList = images;
        this.images = getGsonInstance().toJson(images);
        this.sectionName = sectionName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_no")
    private Integer sectionNo;

    @Column(name = "car_model", length = 21)
    private String carModelCode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "car_model", insertable = false, updatable = false)
    private CommonBikes carModel;

    @Column(name = "section_name")
    private String sectionName;

    @JsonIgnore
    @Column(name = "images", columnDefinition = "json")
    @Convert(converter = ImageVoConverter.class)
    private List<ImageVo> imageList;

    @Column(name = "images", columnDefinition = "json", insertable = false, updatable = false)
    private String images;

}
