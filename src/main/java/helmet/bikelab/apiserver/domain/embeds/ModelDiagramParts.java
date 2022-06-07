package helmet.bikelab.apiserver.domain.embeds;

import helmet.bikelab.apiserver.domain.bike.Diagrams;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Embeddable
public class ModelDiagramParts {

    @Column(name = "diagram_no")
    private Integer diagramNo;

    @ManyToOne
    @JoinColumn(name = "diagram_no", insertable = false, updatable = false)
    private Diagrams diagram;

    @Column(name = "diagram_name")
    private String diagramName;

    @Column(name = "diagram_order")
    private Integer diagramOrderNo;

}
