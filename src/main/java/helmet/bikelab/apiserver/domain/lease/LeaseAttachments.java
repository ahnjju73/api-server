package helmet.bikelab.apiserver.domain.lease;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.types.converters.ModelAttachmentConverter;
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

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "lease_no")
    private Leases lease;

    @Column(name = "attachments", columnDefinition = "JSON")
    @Convert(converter = ModelAttachmentConverter.class)
    private List<ModelAttachment> attachmentsList;

    @Column(name = "attachments", columnDefinition = "JSON", updatable = false, insertable = false)
    private String attachments;

}
