package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.domain.types.converters.YesNoTypeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "releases")
public class Releases {

    // todo db에서 ai안되어있음
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "release_no", nullable = false)
    private Integer releaseNo;

    @Column(name = "release_name", length = 45)
    private String releaseName;

    @Column(name = "use_yn", columnDefinition = "ENUM")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes yesNoTypes;

    @Column(name = "created_at", length = 45)
    private String createdAt;

    @OneToOne(mappedBy = "release")
    private ReleaseAddresses address;
}
