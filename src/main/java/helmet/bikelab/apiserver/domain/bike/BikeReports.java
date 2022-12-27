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
@Table(name = "bike_reports")
public class BikeReports extends OriginObject {

    public BikeReports(){}

    public BikeReports(Bikes bike, BikeReports bikeReport){
        this.bikeNo = bike.getBikeNo();
        this.bike = bike;
        this.updateData(bikeReport);
    }

    @Id
    @Column(name = "bike_no", nullable = false)
    private Integer bikeNo;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "startability", columnDefinition = "TINYINT")
    private Integer startability;

    @Column(name = "appearance", columnDefinition = "TINYINT")
    private Integer appearance;

    @Column(name = "year", columnDefinition = "TINYINT")
    private Integer year;

    @Column(name = "mileage", columnDefinition = "TINYINT")
    private Integer mileage;

    @Column(name = "braking", columnDefinition = "TINYINT")
    private Integer braking;

    public void updateData(BikeReports bikeReport){
        this.startability = bikeReport.getStartability();
        this.appearance = bikeReport.getAppearance();
        this.year = bikeReport.getYear();
        this.mileage = bikeReport.getMileage();
        this.braking = bikeReport.getBraking();
    }

}
