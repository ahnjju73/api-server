package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.types.GroupSessionPK;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import helmet.bikelab.apiserver.utils.Crypt;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@IdClass(GroupSessionPK.class)
@Table(name = "group_sessions", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
public class GroupSessions {

    @Id
    @Column(name = "group_no")
    private Integer groupNo;

    @Id
    @Column(name = "session_type", columnDefinition = "ENUM")
    private UserSessionTypes sessionTypes;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "group_no", insertable = false, updatable = false)
    private ClientGroups group;

    @Column(name = "salt")
    private String salt;

    @Column(name = "session_key")
    private String sessionKey;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "notification_token", length = 512)
    private String notificationToken;

    @JsonIgnore
    public void makeSessionKey(ClientGroups group) throws UnsupportedEncodingException {
        this.salt = Crypt.newCrypt().getSalt(8);
        this.sessionKey = Jwts.builder()
                .setIssuer(SESSION.TOKEN_ISSURE)
                .setSubject(SESSION.TOKEN_NAME)
                .claim("user_id", group.getGroupId())
                .claim("sess_now", LocalDateTime.now().toString())
                .setIssuedAt(new Date())
                .signWith(
                        SignatureAlgorithm.HS256,
                        this.salt.getBytes("UTF-8")
                ).compact();
    }
}
