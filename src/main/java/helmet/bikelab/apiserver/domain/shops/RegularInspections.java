package helmet.bikelab.apiserver.domain.shops;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.client.ClientGroups;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.types.TimeTypes;
import helmet.bikelab.apiserver.domain.types.converters.ModelAttachmentConverter;
import helmet.bikelab.apiserver.domain.types.converters.TimeTypeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "regular_inspections")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RegularInspections {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspect_no")
    private Integer inspectNo;

    @Column(name = "inspect_id")
    private String inspectId;

    @Column(name = "times", columnDefinition = "ENUM")
    @Convert(converter = TimeTypeConverter.class)
    private TimeTypes times;

    @Column(name = "times", columnDefinition = "ENUM", updatable = false, insertable = false)
    private String timeCode;

    @Column(name = "client_no")
    private Integer clientNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @Column(name = "shop_no")
    private Integer shopNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;


    @Column(name = "group_no")
    private Integer groupNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_no", insertable = false, updatable = false)
    private ClientGroups group;

    @JsonIgnore
    @Column(name = "attachments", columnDefinition = "JSON")
    @Convert(converter = ModelAttachmentConverter.class)
    private List<ModelAttachment> attachmentsList = new ArrayList<>();

    @Column(name = "attachments", columnDefinition = "JSON", updatable = false, insertable = false)
    private String attachments;

    @OneToOne(mappedBy = "regularInspections", fetch = FetchType.EAGER)
    private RegularInspectionHistories regularInspectionHistories;

    @Column(name = "include_dt")
    private String includeDt;

    @Column(name = "inspect_date")
    private LocalDateTime inspectDt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
