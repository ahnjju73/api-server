package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.types.ClientSessionPK;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;
import helmet.bikelab.apiserver.utils.Crypt;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "client_sessions", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@IdClass(ClientSessionPK.class)
@NoArgsConstructor
public class ClientSessions {

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

    @JsonIgnore
    public void makeSessionKey(Clients client) throws UnsupportedEncodingException {
        this.salt = Crypt.newCrypt().getSalt(8);
        this.sessionKey = Jwts.builder()
                .setIssuer(SESSION.TOKEN_ISSURE)
                .setSubject(SESSION.TOKEN_NAME)
                .claim("user_id", client.getClientId())
                .claim("sess_now", LocalDateTime.now().toString())
                .setIssuedAt(new Date())
                .signWith(
                        SignatureAlgorithm.HS256,
                        this.salt.getBytes("UTF-8")
                ).compact();
    }

}
