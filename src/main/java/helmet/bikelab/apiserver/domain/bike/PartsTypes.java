package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "parts_types")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsTypes {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parts_type_no", nullable = false)
    private Integer partsTypeNo;

    @Column(name = "parts_type")
    private String partsType;

    @Column(name = "order_no")
    private Integer orderNo;

    @Column(name = "usable")
    private String usable = "1";
}
