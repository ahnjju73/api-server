package helmet.bikelab.apiserver.domain.bikelab;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelPassword;
import helmet.bikelab.apiserver.utils.Crypt;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bike_user_passwords", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeUserPassword {

    @Id
    @Column(name = "user_no", length = 21)
    private Integer bikeUserNo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", insertable = false, updatable = false)
    private BikeUser bikeUser;

    @Embedded
    private ModelPassword modelPassword = new ModelPassword();

    public void makePassword(){
        BikeUser bikeUser = this.getBikeUser();
        String cryptedPassword = Crypt.newCrypt().SHA256(bikeUser.getEmail());
        String salt = Crypt.newCrypt().getSalt(128);
        String password = Crypt.newCrypt().getPassword(cryptedPassword, salt);
        modelPassword.setPassword(password);
        modelPassword.setSalt(salt);
    }

    public void newPassword(BikeUser user){
        this.bikeUser = user;
        this.bikeUserNo = user.getUserNo();
        makePassword();
    }

    public void newPassword(String email){
        modelPassword.newPassword(email);
    }

}
