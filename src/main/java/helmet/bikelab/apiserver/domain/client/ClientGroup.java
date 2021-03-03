package helmet.bikelab.apiserver.domain.client;

import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "groups", catalog = SESSION.SCHEME_SERVICE)
public class ClientGroup {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_no")
    private Integer groupNo;



}
