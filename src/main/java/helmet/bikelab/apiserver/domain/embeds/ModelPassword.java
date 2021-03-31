package helmet.bikelab.apiserver.domain.embeds;


import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.utils.Crypt;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class ModelPassword {

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

    public void modifyPassword(String password){
        String sha256Password = Crypt.newCrypt().SHA256(password);
        String salt = Crypt.newCrypt().getSalt(128);
        String cryptedPassword = Crypt.newCrypt().getPassword(sha256Password, salt);
        this.bakPassword = this.password;
        this.bakSalt = this.salt;
        this.setPassword(cryptedPassword);
        this.setSalt(salt);
    }

    public void newPassword(String email){
        makePassword(email);
    }

    private void makePassword(String email){
        String cryptedPassword = Crypt.newCrypt().SHA256(email);
        String salt = Crypt.newCrypt().getSalt(128);
        String password = Crypt.newCrypt().getPassword(cryptedPassword, salt);
        this.setPassword(password);
        this.setSalt(salt);
    }


}
