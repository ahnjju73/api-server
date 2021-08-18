package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LeasePaymentsRequestListDto extends RequestListDto{

    private String startAt;
    private String endAt;
    private String searchRead;
    private String searchClientId;
    private Integer searchClientNo;
    private String searchBike;

    public void setStartAt(String startAt) {
        this.startAt = LocalDateTime.parse(startAt.replace("Z", "")).toLocalDate().toString();
    }

    public void setEndAt(String endAt) {
        this.endAt = LocalDateTime.parse(endAt.replace("Z", "")).toLocalDate().toString();
    }
}
