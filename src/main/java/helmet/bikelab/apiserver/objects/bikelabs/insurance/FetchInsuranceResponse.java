package helmet.bikelab.apiserver.objects.bikelabs.insurance;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.InsuranceOptionDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchInsuranceResponse {
    private String upCode;
    private String upCodeName;
    private List<InsuranceOptionDto> list;


    @Override
    public boolean equals(Object o) {
        return upCode.equals(((FetchInsuranceResponse)o).getUpCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(upCode, upCodeName, list);
    }
}
