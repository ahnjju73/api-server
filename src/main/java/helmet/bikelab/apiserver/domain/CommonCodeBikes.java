package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeTypesConverter;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.BikeModelDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "com_comm_bikes")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommonCodeBikes {
    @Id
    @Column(name = "comm_cd", nullable = false, length = 21)
    private String code;

    @Column(name = "manuf_no")
    private Integer manufacturerNo;

    @ManyToOne
    @JoinColumn(name = "manuf_no", insertable = false, updatable = false)
    private Manufacturers manufacturer;

    @Column(name = "comm_nm")
    private String model;

    @Column(name = "volume")
    private Double volume;

    @Column(name = "bike_type", columnDefinition = "ENUM")
    @Convert(converter = BikeTypesConverter.class)
    private BikeTypes bikeType = BikeTypes.GAS;

    @Column(name = "bike_type", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String bikeTypeCode;

    @JsonIgnore
    @Column(name = "discontinue")
    private Boolean discontinue = false;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "ins_dt", columnDefinition = "CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "upt_dt", columnDefinition = "CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void setManufacturer(Manufacturers manufacturer) {
        this.manufacturer = manufacturer;
        this.manufacturerNo = manufacturer.getManufacturerNo();
    }

    @JoinColumn
    public void updateData(BikeModelDto bikeModelDto){
        this.setModel(bikeModelDto.getModel());
        this.setBikeType(bikeModelDto.getBikeType());
        this.setVolume(bikeModelDto.getVolume());
        this.setManufacturer(bikeModelDto.getManufacturers());
    }
}
