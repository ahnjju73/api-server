package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeAttachmentTypes;
import helmet.bikelab.apiserver.domain.types.PayerTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeAttachmentTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.PayerTypesConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "bike_attachments")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeAttachments {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bike_attachment_no")
    private Integer bikeFileInfoNo;

    @JsonIgnore
    @Column(name = "bike_no")
    private Integer bikeNo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "domain", columnDefinition = "MEDIUMTEXT")
    private String domain;

    @Column(name = "file_name", columnDefinition = "MEDIUMTEXT")
    private String fileName;

    @Column(name = "file_key", columnDefinition = "MEDIUMTEXT")
    private String fileKey;

    @Column(name = "attachment_type", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = BikeAttachmentTypesConverter.class)
    private BikeAttachmentTypes attachmentType;

    @Column(name = "attachment_type", columnDefinition = "ENUM", nullable = false, insertable = false, updatable = false)
    private String attachmentTypeCode;


}
