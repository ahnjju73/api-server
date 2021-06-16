package helmet.bikelab.apiserver.domain.client;

import helmet.bikelab.apiserver.domain.types.ClientSessionPK;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@IdClass(ClientSessionPK.class)
@Table(name = "client_sessions", catalog = SESSION.SCHEME_SERVICE)
public class ClientSessions implements Serializable {

    @Id
    @Column(name = "client_no")
    private Integer clientNo;

    @Id
    @Column(name = "session_type", columnDefinition = "ENUM")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionTypes;

    @ManyToOne
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @Column(name = "salt")
    private String salt;

    @Column(name = "session_key")
    private String sessionKey;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}
