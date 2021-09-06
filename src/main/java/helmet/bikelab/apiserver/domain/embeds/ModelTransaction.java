package helmet.bikelab.apiserver.domain.embeds;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class ModelTransaction {
    @Column(name = "reg_num")
    private String regNum;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "price")
    private Integer price;

}
