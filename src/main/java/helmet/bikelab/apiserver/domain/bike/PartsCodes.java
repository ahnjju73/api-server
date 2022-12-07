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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsCodes {

    public PartsCodes(){}

    public PartsCodes(PartsTypes partsType, String name, String nameEng){
        this.partsTypeNo = partsType.getPartsTypeNo();
        this.partsName = name;
        this.partsNameEng = nameEng;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parts_code_no", nullable = false)
    private Integer partsCodeNo;

    @Column(name = "parts_type_no", nullable = false)
    private Integer partsTypeNo;

    @ManyToOne
    @JoinColumn(name = "parts_type_no", insertable = false, updatable = false)
    private PartsTypes partsType;

    @Column(name = "parts_name", nullable = false)
    private String partsName;

    @Column(name = "parts_name_eng", nullable = false)
    private String partsNameEng;

    @Column(name = "usable", nullable = false)
    private Boolean usable = true;

}
