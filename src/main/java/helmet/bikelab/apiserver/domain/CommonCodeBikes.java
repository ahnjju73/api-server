package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    @Column(name = "make")
    private String make;

    @Column(name = "comm_nm")
    private String model;

    @Column(name = "volume")
    private Double volume;

    @Column(name = "bike_type", columnDefinition = "ENUM")
    private BikeTypes bikeType;

    @JsonIgnore
    @Column(name = "discontinue")
    private Boolean discontinue = false;

}
