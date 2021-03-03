package helmet.bikelab.apiserver.domain.client;

import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "groups", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
public class ClientGroups {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_no")
    private Integer groupNo;

    @Column(name = "group_id", unique = true)
    private String groupId;

    @Column(name = "group_name")
    private String groupName;

}
