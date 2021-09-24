package helmet.bikelab.apiserver.domain.demands;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.ComSpecialTerms;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "demand_lease_terms", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
public class DemandLeaseSpecialTerms extends OriginObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_no")
    private Long termNo;

    @Column(name = "demand_lease_no")
    private Long demandLeaseNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "demand_lease_no", insertable = false, updatable = false)
    private DemandLeases demandLeases;

    @Column(name = "sterm_no")
    private Integer stermNo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sterm_no", insertable = false, updatable = false)
    private ComSpecialTerms specialTerms;

}
