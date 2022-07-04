package helmet.bikelab.apiserver.domain.types;

import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import java.io.Serializable;

public class SectionAxisPartsPK implements Serializable {

    @Column(name = "axis_no")
    private Integer axisNo;

    @Column(name = "parts_no")
    private Long partsNo;

}
