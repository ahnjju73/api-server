package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "client_addresses")
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClientAddresses {
    @Id
    @Column(name = "client_no")
    private Integer clientNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients clients;

    @Embedded
    private ModelAddress modelAddress;
}
