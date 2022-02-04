package helmet.bikelab.apiserver.domain.embeds;


import helmet.bikelab.apiserver.domain.Banks;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Embeddable
public class ModelBankAccount {

    @Column(name = "bank_cd", length = 4)
    private String bankCode;

    @ManyToOne
    @JoinColumn(name = "bank_cd", insertable = false, updatable = false)
    private Banks bank;

    @Column(name = "account")
    private String account;

    @Column(name = "depositor")
    private String depositor;
}
