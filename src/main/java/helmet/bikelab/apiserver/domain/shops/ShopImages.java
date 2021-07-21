package helmet.bikelab.apiserver.domain.shops;

import helmet.bikelab.apiserver.domain.types.ModelMedia;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "shop_images")
public class ShopImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_no")
    private Long imageNo;

    @Column(name = "shop_no")
    private Integer shopNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    @Embedded
    private ModelMedia media = new ModelMedia();

    @Column(name = "order_no")
    private Integer orderNo;

}
