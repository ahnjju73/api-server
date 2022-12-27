package helmet.bikelab.apiserver.domain.shops;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.riders.RiderInsurances;
import helmet.bikelab.apiserver.domain.types.converters.HistoryConverter;
import helmet.bikelab.apiserver.objects.RiderInsHistoriesDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "regular_inspection_histories")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RegularInspectionHistories {

    @Id
    @Column(name = "inspect_no")
    private Integer inspectNo;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "inspect_no", insertable = false, updatable = false)
    private RegularInspections regularInspections;

    @Column(name = "histories", columnDefinition = "json")
    @Convert(converter = HistoryConverter.class)
    private List<RiderInsHistoriesDto> histories = new ArrayList<>();

}