package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.types.PartsTypeDiscountClientPK;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@IdClass(PartsTypeDiscountClientPK.class)
@Table(name = "parts_type_discount_client")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsTypeDiscountClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parts_type_no", nullable = false)
    private Integer partsTypeNo;

    @ManyToOne
    @JoinColumn(name = "parts_type_no", insertable = false, updatable = false)
    private PartsTypes partsType;

    @Id
    @Column(name = "client_no")
    private Integer clientNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @Column(name = "discount_rate")
    private Double discountRate = 1.0;

}
