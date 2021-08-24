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
@Table(name = "parts_codes")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsCodes {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parts_code_no", nullable = false)
    private Integer partsCodeNo;


    @Column(name = "parts_type_no", nullable = false)
    private Integer partsTypeNo;

    @Column(name = "parts_name", nullable = false)
    private String partsName;

    @Column(name = "usable", nullable = false)
    private Boolean usable;





}
