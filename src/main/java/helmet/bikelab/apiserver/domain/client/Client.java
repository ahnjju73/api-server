package helmet.bikelab.apiserver.domain.client;

import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.domain.types.converters.YesNoTypeConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Table(name = "clients", catalog = SESSION.SCHEME_SERVICE)
public class Client {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_no")
    private  Integer clientNo;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "group_no")
    private Integer groupId;

    @ManyToOne
    @JoinColumn(name = "group_no")
    ClientGroup clientGroup;

    @Column(name = "direct_yn")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes directType;

    @OneToOne(mappedBy = "client")
    private ClientInfo clientInfo;


}
