package helmet.bikelab.apiserver.domain.bikelab;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", insertable = false, updatable = false)
    private BikeUser bikeUser;

    @Column(name = "password", length = 256)
    private String password;

    @Column(name = "salt", length = 256)
    private String salt;

    @Column(name = "bak_password", length = 256)
    private String bakPassword;

    @Column(name = "bak_salt", length = 256)
    private String bakSalt;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    public void makePassword(){
        BikeUser bikeUser = this.getBikeUser();
        String cryptedPassword = Crypt.newCrypt().SHA256(bikeUser.getEmail());
        String salt = Crypt.newCrypt().getSalt(128);
        String password = Crypt.newCrypt().getPassword(cryptedPassword, salt);
        this.setPassword(password);
        this.setSalt(salt);
    }

    public void newPassword(BikeUser user){
        this.bikeUser = user;
        this.bikeUserNo = user.getUserNo();
        makePassword();
    }

}
