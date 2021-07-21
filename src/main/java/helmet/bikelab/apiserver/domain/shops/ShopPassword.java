package helmet.bikelab.apiserver.domain.shops;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.embeds.ModelPassword;
import helmet.bikelab.apiserver.utils.Crypt;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "shop_passwords", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ShopPassword {

    @Id
    @Column(name = "shop_no")
    private Integer shopNo;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    @Embedded
    private ModelPassword modelPassword = new ModelPassword();

    public void makePassword(String _password){
        String cryptedPassword = Crypt.newCrypt().SHA256(_password);
        String salt = Crypt.newCrypt().getSalt(128);
        String password = Crypt.newCrypt().getPassword(cryptedPassword, salt);
        modelPassword.setPassword(password);
        modelPassword.setSalt(salt);
    }


    public void newPassword(String email){
        modelPassword.newPassword(email);
    }

    public void updatePassword(String pass){ modelPassword.modifyPassword(pass); }

}
