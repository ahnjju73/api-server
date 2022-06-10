package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.types.DiagramPartsPK;
import helmet.bikelab.apiserver.domain.types.converters.PartsImagesConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "diagram_parts")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@IdClass(DiagramPartsPK.class)
public class DiagramParts {

    public DiagramParts(Diagrams diagram, Parts part, Integer orderNo){
        this.diagramNo = diagram.getDiagramNo();
        this.partNo = part.getPartNo();
        this.orderNo = orderNo;
    }

    @Id
    @Column(name = "diagram_no")
    private Integer diagramNo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "diagram_no", insertable = false, updatable = false)
    private Diagrams diagram;

    @Id
    @Column(name = "parts_no")
    private Long partNo;

    @ManyToOne
    @JoinColumn(name = "parts_no", insertable = false, updatable = false)
    private Parts part;

    @Column(name = "order_no")
    private Integer orderNo;

}
