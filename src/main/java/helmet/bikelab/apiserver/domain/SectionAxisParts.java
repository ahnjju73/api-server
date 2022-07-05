package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.types.SectionAxisPartsPK;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name = "section_axis_parts", catalog = SESSION.SCHEME_SERVICE)
@IdClass(SectionAxisPartsPK.class)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SectionAxisParts extends OriginObject {

    @Id
    @Column(name = "axis_no")
    private Integer axisNo;

    @Id
    @Column(name = "parts_no")
    private Long partsNo;

    @ManyToOne
    @JoinColumn(name = "parts_no", insertable = false, updatable = false)
    private Parts part;

    @ManyToOne
    @JoinColumn(name = "section_no", insertable = false, updatable = false)
    private Sections section;

    @Column(name = "section_no")
    private Integer sectionNo;

}
