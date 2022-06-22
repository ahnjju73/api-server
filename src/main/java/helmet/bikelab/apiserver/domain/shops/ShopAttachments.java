package helmet.bikelab.apiserver.domain.shops;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.types.converters.ModelAttachmentConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "shop_attachments", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ShopAttachments {

    @Id
    @Column(name = "shop_no")
    private Integer shopNo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    @JsonIgnore
    @Column(name = "attachments", columnDefinition = "JSON")
    @Convert(converter = ModelAttachmentConverter.class)
    private List<ModelAttachment> attachmentsList;

    @Column(name = "attachments", columnDefinition = "JSON", updatable = false, insertable = false)
    private String attachments;

}
