package helmet.bikelab.apiserver.domain.bike;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.domain.types.converters.PartsBackUpConverter;
import helmet.bikelab.apiserver.domain.types.converters.PartsImagesConverter;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.PartsBackUpDto;
import helmet.bikelab.apiserver.objects.requests.DiagramInfoRequest;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Diagrams extends OriginObject {

    public Diagrams(){}

    public Diagrams(DiagramInfoRequest diagramInfoRequest){
        this.diagramId = UUID.randomUUID().toString().replace("-", "");
        this.carModelCode = diagramInfoRequest.getCarModel();
        this.name = diagramInfoRequest.getName();
        if(bePresent(diagramInfoRequest.getImages())){
            this.imageList = diagramInfoRequest.getImages().stream().map(elm -> {
                elm.copyObjectToOrigin();
                return new ImageVo(MediaTypes.IMAGE, elm.getFilename(), elm.getFileKey());
            }).collect(Collectors.toList());
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagram_no")
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


}
