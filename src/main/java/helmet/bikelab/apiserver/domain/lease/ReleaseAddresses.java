package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "release_addresses")
public class ReleaseAddresses {
    @Id
    @Column(name = "release_no", nullable = false)
    private Integer releaseNo;

    @OneToOne(optional = false)
    @JoinColumn(name = "release_no", insertable = false, updatable = false)
    private Releases release;

    @Embedded
    private ModelAddress address = new ModelAddress();
}
