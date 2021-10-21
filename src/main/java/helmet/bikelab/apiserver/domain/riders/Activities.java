package helmet.bikelab.apiserver.domain.riders;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.types.ActivityTypes;
import helmet.bikelab.apiserver.domain.types.converters.ActivityTypesConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "activities")
public class Activities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_no")
    private Long activityNo;

    @Column(name = "activity_type", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = ActivityTypesConverter.class)
    private ActivityTypes activityType;

    @Column(name = "activity_type", columnDefinition = "ENUM", nullable = false, insertable = false, updatable = false)
    private String activityTypeCode;

    @Column(name = "rider_no")
    private Integer riderNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @Column(name = "shop_no")
    private Integer shopNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    @Column(name = "client_no")
    private Integer clientNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @Column(name = "bike_no")
    private Integer bikeNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "estimate_no")
    private Long estimateNo;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}
