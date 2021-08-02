package helmet.bikelab.apiserver.domain.bike;

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
@Table(name = "bike_attachments")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeAttachments {
    @JsonIgnore
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

    @Column(name = "url", columnDefinition = "MEDIUMTEXT")
    private String url;
}
