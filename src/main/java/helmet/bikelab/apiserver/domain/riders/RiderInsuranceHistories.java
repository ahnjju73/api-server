package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.domain.types.converters.HistoryConverter;
import helmet.bikelab.apiserver.domain.types.converters.ImageVoConverter;
import helmet.bikelab.apiserver.objects.RiderInsHistoriesDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "rider_insurance_histories")
public class RiderInsuranceHistories {

    @Id
    @Column(name = "rider_ins_no")
    private Integer riderInsNo;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "rider_ins_no", insertable = false, updatable = false)
    private RiderInsurances riderInsurance;

    @Column(name = "histories", columnDefinition = "json")
    @Convert(converter = HistoryConverter.class)
    private List<RiderInsHistoriesDto> histories = new ArrayList<>();

}
