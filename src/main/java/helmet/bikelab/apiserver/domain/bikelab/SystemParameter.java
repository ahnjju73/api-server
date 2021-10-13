package helmet.bikelab.apiserver.domain.bikelab;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.xnio.Property;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "system_parameters", catalog = SESSION.SCHEME_SERVICE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@NoArgsConstructor
public class SystemParameter {

    @Id
    @Column(name = "parameter_no")
    private Integer parameterNo;

    @Column(name = "val")
    private String value;

    @Column(name = "rmk")
    private String remark;
}
