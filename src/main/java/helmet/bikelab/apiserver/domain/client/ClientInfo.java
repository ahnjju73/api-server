package helmet.bikelab.apiserver.domain.client;

import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "client_info", catalog = SESSION.SCHEME_SERVICE)
public class ClientInfo {
    @Id
    @Column(name = "client_no")
    private Integer clientNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Client client;


}
