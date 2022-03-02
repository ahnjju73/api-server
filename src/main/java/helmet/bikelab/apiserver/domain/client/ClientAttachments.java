package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "client_attachments")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClientAttachments {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_attachment_no")
    private Integer attachNo;

    @JsonIgnore
    @Column(name = "client_no")
    private Integer clientNo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @Column(name = "domain", columnDefinition = "MEDIUMTEXT")
    private String domain;

    @Column(name = "file_name", columnDefinition = "MEDIUMTEXT")
    private String fileName;

    @Column(name = "file_key", columnDefinition = "MEDIUMTEXT")
    private String fileKey;
}
