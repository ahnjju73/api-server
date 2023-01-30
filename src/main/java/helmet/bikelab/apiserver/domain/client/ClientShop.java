package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.types.AccountStatusTypes;
import helmet.bikelab.apiserver.domain.types.BusinessTypes;
import helmet.bikelab.apiserver.domain.types.ClientShopPK;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.domain.types.converters.AccountStatusConverter;
import helmet.bikelab.apiserver.domain.types.converters.BusinessTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.YesNoTypeConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "client_shop", catalog = SESSION.SCHEME_SERVICE)
@IdClass(ClientShopPK.class)
@NoArgsConstructor
public class ClientShop {

    @Id
    @Column(name = "client_no")
    private Integer clientNo;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @Id
    @Column(name = "shop_no")
    private Integer shopNo;

    @OneToOne
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

}
