package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.riders.RiderVerified;
import helmet.bikelab.apiserver.domain.types.RiderVerifiedTypes;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RiderVerifiedResponse {

    private RiderVerifiedTypes verified;
    private List<RiderVerified> verifiedList;
    private List<RiderVerified> requestVerifiedList;
    private LocalDateTime verifiedAt;
    private LocalDateTime verifiedRequestAt;
    private String verifiedRejectMessage;

}
