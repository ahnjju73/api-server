package helmet.bikelab.apiserver.domain.riders;


import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.types.RiderSessionPK;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;
import helmet.bikelab.apiserver.utils.Crypt;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@IdClass(RiderSessionPK.class)
@Table(name = "rider_sessions", catalog = SESSION.SCHEME_SERVICE)
public class RiderSessions  {

    @Id
    @Column(name = "rider_no")
    private Integer riderNo;

    @Id
    @Column(name = "session_type", columnDefinition = "ENUM")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionTypes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @Column(name = "salt")
    private String salt;

    @Column(name = "session_key")
    private String sessionKey;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonIgnore
    public void makeSessionKey(Riders client) throws UnsupportedEncodingException {
        this.salt = Crypt.newCrypt().getSalt(8);
        this.sessionKey = Jwts.builder()
                .setIssuer(SESSION.TOKEN_ISSURE)
                .setSubject(SESSION.TOKEN_NAME)
                .claim("user_id", client.getRiderId())
                .claim("sess_now", LocalDateTime.now().toString())
                .setIssuedAt(new Date())
                .signWith(
                        SignatureAlgorithm.HS256,
                        this.salt.getBytes("UTF-8")
                ).compact();
    }

}
