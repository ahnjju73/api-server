package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.domain.types.converters.PartsImagesConverter;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.requests.DiagramInfoRequest;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name = "diagrams")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Diagrams extends OriginObject {

    public Diagrams(){}

    public Diagrams(DiagramInfoRequest diagramInfoRequest){
        this.diagramId = UUID.randomUUID().toString().replace("-", "");
        this.carModelCode = diagramInfoRequest.getCarModel();
        this.name = diagramInfoRequest.getName();
        if(bePresent(diagramInfoRequest.getImages())){
            this.imageList = extractImageVo(diagramInfoRequest.getImages());
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagram_no")
    @JsonIgnore
    private Integer diagramNo;

    @Column(name = "diagram_id", unique = true)
    private String diagramId;

    @Column(name = "car_model")
    private String carModelCode;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "car_model", insertable = false, updatable = false)
    private CommonBikes carModel;

    @JsonIgnore
    @Column(name = "images", columnDefinition = "json")
    @Convert(converter = PartsImagesConverter.class)
    private List<ImageVo> imageList;

    @Column(name = "images", columnDefinition = "json", insertable = false, updatable = false)
    private String images;

    @Column(name = "deleted_at", columnDefinition = "DATETIME")
    private LocalDateTime deletedAt;

    public List<ImageVo> extractImageVo(List<PresignedURLVo> image){
        if(bePresent(image)){
            List<ImageVo> imageVos = image.stream().map(elm -> {
                elm.copyObjectToOrigin();
                return new ImageVo(MediaTypes.IMAGE, elm.getFilename(), elm.getFileKey());
            }).collect(Collectors.toList());
            return imageVos;
        }else return null;
    }

    public void setImageList(List<PresignedURLVo> image){
        List<ImageVo> imageVos = extractImageVo(image);
        if(bePresent(imageVos)){
            if(bePresent(imageList)){
                imageList.addAll(imageVos);
            }else {
                imageList = imageVos;
            }
        }
    }

    public Diagrams deleteImageById(String id){
        if(bePresent(imageList)){
            List<ImageVo> collect = imageList.stream()
                    .filter(elm -> elm.getId().equals(id))
                    .collect(Collectors.toList());
            imageList.removeAll(collect);
        }
        return this;
    }

}
