package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelPassword;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "rider_passwords", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RiderPassword extends OriginObject {

    @Id
    @Column(name = "rider_no")
    private Integer riderNo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @Embedded
    private ModelPassword modelPassword = new ModelPassword();

    public void newPassword(String password){
        modelPassword.newPassword(password);
    }

}
