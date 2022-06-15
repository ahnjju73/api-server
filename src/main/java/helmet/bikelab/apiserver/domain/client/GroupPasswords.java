package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelPassword;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.Crypt;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "group_passwords", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GroupPasswords extends OriginObject {

    @Id
    @Column(name = "group_no")
    private Integer groupNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_no", insertable = false, updatable = false)
    private ClientGroups group;

    @Embedded
    private ModelPassword modelPassword = new ModelPassword();

    public void makePassword(){
        ClientGroups group = this.getGroup();
        String cryptedPassword = Crypt.newCrypt().SHA256(group.getCeoEmail());
        String salt = Crypt.newCrypt().getSalt(128);
        String password = Crypt.newCrypt().getPassword(cryptedPassword, salt);
        modelPassword.setPassword(password);
        modelPassword.setSalt(salt);
    }

    public void newPassword(String email){
        modelPassword.newPassword(email);
    }

    public void updatePassword(String pass){ modelPassword.modifyPassword(pass); }

    public void checkLogin(String fromPassword){
        String requestedPassword = Crypt.newCrypt().getPassword(fromPassword, modelPassword.getSalt());
        String crypedPassword = modelPassword.getPassword();
        if(!bePresent(requestedPassword) || !bePresent(crypedPassword) || !requestedPassword.equals(crypedPassword)) withException("1001-106");
    }

    public void updatePasswordWithoutSHA256(String pass){ modelPassword.modifyPasswordWithoutSHA256(pass); }

}
