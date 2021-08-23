package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelPassword;
import helmet.bikelab.apiserver.utils.Crypt;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "client_passwords", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClientPassword {

    @Id
    @Column(name = "client_no")
    private Integer clientNo;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @Embedded
    private ModelPassword modelPassword = new ModelPassword();

    public void makePassword(){
        Clients client = this.getClient();
        String cryptedPassword = Crypt.newCrypt().SHA256(client.getEmail());
        String salt = Crypt.newCrypt().getSalt(128);
        String password = Crypt.newCrypt().getPassword(cryptedPassword, salt);
        modelPassword.setPassword(password);
        modelPassword.setSalt(salt);
    }

    public void newPassword(String email){
        modelPassword.newPassword(email);
    }

    public void updatePasswordWithoutSHA256(String pass){ modelPassword.modifyPasswordWithoutSHA256(pass); }

}
