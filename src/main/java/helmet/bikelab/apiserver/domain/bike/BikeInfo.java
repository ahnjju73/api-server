package helmet.bikelab.apiserver.domain.bike;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeInfoTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeInfoTypesConverter;
import helmet.bikelab.apiserver.objects.requests.BikeInfoDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(name = "bike_info")
public class BikeInfo extends OriginObject {

    public BikeInfo(){}

    public BikeInfo(Bikes bike, BikeInfoDto infoDto){
        this.bikeNo = bike.getBikeNo();
        this.bike = bike;
        this.updateBikeInfo(infoDto);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_no", nullable = false)
    private Integer infoNo;

    @Column(name = "bike_no", nullable = false)
    private Integer bikeNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "info_type", columnDefinition = "ENUM")
    @Convert(converter = BikeInfoTypesConverter.class)
    private BikeInfoTypes infoType;

    @Column(name = "info_type", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String infoTypeCode;

    @Column(name = "price")
    private Integer price;

    @Column(name = "paid_at", columnDefinition = "DATE")
    private LocalDate paidAt;

    public void updateBikeInfo(BikeInfoDto infoDto){
        this.infoType = infoDto.getInfoType();
        this.infoTypeCode = infoDto.getInfoType().getInfoType();
        this.price = infoDto.getPrice();
        this.paidAt = infoDto.getPaidAt();
    }

}
