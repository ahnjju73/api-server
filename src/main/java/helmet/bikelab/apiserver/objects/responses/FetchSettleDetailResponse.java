package helmet.bikelab.apiserver.objects.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.Estimates;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.embeds.ModelBankAccount;
import helmet.bikelab.apiserver.domain.shops.Shops;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchSettleDetailResponse {
    private String settleId;
    private Shops shop;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private BikeUser confirmedUser;
    private ModelBankAccount bankAccount;
    private String settleStatus;
    private Integer deductible;
    private List<Estimates> estimates;
}
