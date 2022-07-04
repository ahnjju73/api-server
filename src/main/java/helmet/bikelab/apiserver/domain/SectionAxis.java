package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.domain.types.converters.PartsImagesConverter;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "section_axis", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SectionAxis extends OriginObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "axis_no")
    private Integer axisNo;

    @ManyToOne
    @JoinColumn(name = "section_no", insertable = false, updatable = false)
    private Sections section;

    @Column(name = "section_no")
    private Integer sectionNo;

    @Column(name = "name")
    private String name;

    @Column(name = "axis", columnDefinition = "JSON")
    private String axis;
}
