package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.ComSpecialTerms;
import helmet.bikelab.apiserver.domain.types.RiderDemandLeaseSpecialTermsPK;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "rider_demand_lease_terms", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@IdClass(RiderDemandLeaseSpecialTermsPK.class)
public class RiderDemandLeaseSpecialTerms extends OriginObject {

    @Id
    @Column(name = "rider_no")
    private Integer riderNo;

    @Id
    @Column(name = "sterm_no")
    private Integer stermNo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sterm_no", insertable = false, updatable = false)
    private ComSpecialTerms specialTerms;

}
