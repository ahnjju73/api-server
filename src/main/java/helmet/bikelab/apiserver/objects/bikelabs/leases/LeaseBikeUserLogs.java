package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LeaseBikeUserLogs extends SessService {

    String nextToken;
    Integer count = 0;
    List<Map> histories = new ArrayList<>();
}
