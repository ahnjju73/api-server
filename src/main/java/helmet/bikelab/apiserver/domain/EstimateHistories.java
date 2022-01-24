package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.types.EstimateHistoryTypes;
import helmet.bikelab.apiserver.domain.types.converters.EstimateHistoryTypesConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "estimate_histories")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EstimateHistories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_no")
    private Long historyNo;

    @Column(name = "estimate_no")
    private Long estimateNo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "estimate_no", insertable = false, updatable = false)
    private Estimates estimate;

    @Column(name = "history_type", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = EstimateHistoryTypesConverter.class)
    private EstimateHistoryTypes historyType;

    @Column(name = "history_type", columnDefinition = "ENUM", nullable = false, insertable = false, updatable = false)
    private String historyTypeCode;

    @Column(name = "client_no")
    private Integer clientNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;


    @Column(name = "shop_no")
    private Integer shopNo;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    @Column(name = "rider_no")
    private Integer riderNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @Column(name = "bike_no", nullable = false)
    private Integer bikeNo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "created_at", columnDefinition = "default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}
