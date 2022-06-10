package helmet.bikelab.apiserver.domain.lease;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.converters.ModelAttachmentConverter;
import helmet.bikelab.apiserver.domain.types.converters.ModelReviewImageConverter;
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
@Table(name = "fines", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
public class Fines {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fine_no")
    private Integer fineNo;

    @Column(name = "fine_id")
    private String fineId;

    @Column(name = "fee")
    private Integer fee;

    @Column(name = "paid_fee")
    private Integer paidFee;

    @Column(name = "fine_num")
    private String fineNum;

    @Column(name = "fine_type")
    private String fineType;

    @Column(name = "fine_date")
    private LocalDateTime fineDate;

    @Column(name = "fine_expire_date")
    private LocalDateTime fineExpireDate;

    @Column(name = "rider_no")
    private Integer riderNo;

    @JsonIgnore
    @JoinColumn(name = "rider_no", updatable = false, insertable = false)
    @ManyToOne
    private Riders rider;

    @Column(name = "client_no")
    private Integer clientNo;

    @JsonIgnore
    @JoinColumn(name = "client_no", updatable = false, insertable = false)
    @ManyToOne
    private Clients client;

    @Column(name = "bike_no")
    private Integer bikeNo;

    @JsonIgnore
    @JoinColumn(name = "bike_no", updatable = false, insertable = false)
    @ManyToOne
    private Bikes bike;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JsonIgnore
    @Column(name = "attachments", columnDefinition = "JSON")
    @Convert(converter = ModelAttachmentConverter.class)
    private List<ModelAttachment> attachmentsList;

    @Column(name = "attachments", columnDefinition = "JSON", updatable = false, insertable = false)
    private String attachments;

}
