package helmet.bikelab.apiserver.domain.ins_companies;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.embeds.ModelPassword;
import helmet.bikelab.apiserver.utils.Crypt;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "insurance_company_passwords")
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InsuranceCompanyPasswords {

    @Id
    @Column(name = "company_no")
    private Integer companyNo;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "company_no", insertable = false, updatable = false)
    private InsuranceCompanies company;

    @Embedded
    private ModelPassword modelPassword = new ModelPassword();

    public void makePassword(){
        InsuranceCompanies insuranceCompanies = this.getCompany();
        String cryptedPassword = Crypt.newCrypt().SHA256(insuranceCompanies.getEmail());
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
