package helmet.bikelab.apiserver.domain.shops;

import helmet.bikelab.apiserver.domain.types.ShopSessionPK;
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
@IdClass(ShopSessionPK.class)
@Table(name = "shop_sessions", catalog = SESSION.SCHEME_SERVICE)
public class ShopSessions implements Serializable {

    @Id
    @Column(name = "shop_no")
    private Integer shopNo;

    @Id
    @Column(name = "session_type", columnDefinition = "ENUM")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionTypes;

    @ManyToOne
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    @Column(name = "salt")
    private String salt;

    @Column(name = "session_key")
    private String sessionKey;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}
