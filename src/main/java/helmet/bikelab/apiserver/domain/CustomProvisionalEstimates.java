package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.CustomProvisionalEstimatePK;
import helmet.bikelab.apiserver.domain.types.ProvisionalPartsDto;
import helmet.bikelab.apiserver.domain.types.converters.PartsConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "custom_provisional_estimates", catalog = SESSION.SCHEME_SERVICE)
@IdClass(value = CustomProvisionalEstimatePK.class)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CustomProvisionalEstimates {

    @Id
    @Column(name = "custom_estimate_no")
    private Long customEstimateNo;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "custom_estimate_no", insertable = false, updatable = false)
    private CustomEstimates customEstimates;

    @Id
    @Column(name = "order_no")
    private Integer order;

    @Column(name = "parts", columnDefinition = "json")
    @Convert(converter = PartsConverter.class)
    private List<ProvisionalPartsDto> partsList;

    @Column(name = "total_price")
    private Integer totalPrice = 0;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;
}
