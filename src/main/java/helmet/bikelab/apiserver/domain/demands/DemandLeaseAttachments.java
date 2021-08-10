package helmet.bikelab.apiserver.domain.demands;

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
@Table(name = "demand_lease_attachments")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DemandLeaseAttachments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_no")
    private Long demandLeaseFileNo;

    @JsonIgnore
    @Column(name = "demand_lease_no")
    private Long demandLeaseNo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "demand_lease_no", insertable = false, updatable = false)
    private DemandLeases demandLeases;

    @Column(name = "domain", columnDefinition = "MEDIUMTEXT")
    private String domain;

    @Column(name = "file_name", columnDefinition = "MEDIUMTEXT")
    private String fileName;

    @Column(name = "file_key", columnDefinition = "MEDIUMTEXT")
    private String fileKey;
}
