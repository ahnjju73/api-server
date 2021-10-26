package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "rider_demand_lease_attachments")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RiderDemandLeaseAttachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_no")
    private Long demandLeaseFileNo;

    @JsonIgnore
    @Column(name = "rider_no")
    private Integer riderNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @Column(name = "domain", columnDefinition = "MEDIUMTEXT")
    private String domain;

    @Column(name = "file_name", columnDefinition = "MEDIUMTEXT")
    private String fileName;

    @Column(name = "file_key", columnDefinition = "MEDIUMTEXT")
    private String fileKey;
}
