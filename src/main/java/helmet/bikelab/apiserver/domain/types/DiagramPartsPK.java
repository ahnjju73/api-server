package helmet.bikelab.apiserver.domain.types;

import helmet.bikelab.apiserver.domain.bike.Diagrams;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

public class DiagramPartsPK implements Serializable {
    @Column(name = "diagram_no")
    private Integer diagramNo;

    @Column(name = "parts_no")
    private Long partNo;
}
