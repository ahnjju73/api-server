package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@Table(name = "com_comm_insurances")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommonCodeInsurances {
    @Id
    @Column(name = "comm_cd", nullable = false, length = 21)
    private String code;

    @Column(name = "up_comm_cd")
    private String upperCode;

    @Column(name = "comm_nm")
    private String name;
}
