package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.types.LeaseFinePK;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "lease_fine")
@NoArgsConstructor
@IdClass(value = LeaseFinePK.class)
public class LeaseFines {

    @Id
    @Column(name = "lease_no")
    private Integer leaseNo;

    @Id
    @Column(name = "fine_no")
    private Integer fineNo;

}
