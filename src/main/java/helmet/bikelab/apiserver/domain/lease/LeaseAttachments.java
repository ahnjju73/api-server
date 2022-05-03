package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.embeds.ModelLeaseAttachment;
import helmet.bikelab.apiserver.domain.types.converters.ModelReviewImageConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lease_attachments")
public class LeaseAttachments {

    @Id
    @Column(name = "lease_no")
    private Integer leaseNo;

    @OneToOne
    @JoinColumn(name = "lease_no")
    private Leases lease;

    @Column(name = "attachments", columnDefinition = "JSON")
    @Convert(converter = ModelReviewImageConverter.class)
    private List<ModelLeaseAttachment> attachmentsList;

    @Column(name = "attachments", columnDefinition = "JSON", updatable = false, insertable = false)
    private String attachments;

}
