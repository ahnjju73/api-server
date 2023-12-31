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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "com_comm_manuf")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Manufacturers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manuf_no")
    private Integer manufacturerNo;

    @Column(name = "manuf")
    private String manufacturer;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "created_at", columnDefinition = "CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}
