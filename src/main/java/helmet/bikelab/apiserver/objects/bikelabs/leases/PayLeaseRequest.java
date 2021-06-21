package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PayLeaseRequest implements Comparable {
    private String leaseId;
    private Integer paidFee;
    private String clientId;
    private String clientNum;
    private String bikeNum;

    @Override
    public int compareTo(Object o) {
        return clientNum.compareTo(((PayLeaseRequest)o).getClientNum());
    }

    @Override
    public boolean equals(Object o) {
        return clientNum.equals(((PayLeaseRequest)o).getClientNum());
    }


}
