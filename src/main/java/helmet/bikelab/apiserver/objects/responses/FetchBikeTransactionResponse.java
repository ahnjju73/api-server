package helmet.bikelab.apiserver.objects.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.Sections;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.embeds.ModelBikeTransaction;
import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchBikeTransactionResponse extends ModelBikeTransaction {
    public FetchBikeTransactionResponse(Bikes bike){
        ModelBikeTransaction transaction = bike.getTransaction();
        this.receiveDate = bike.getReceiveDate();
        setRegNum(transaction.getRegNum());
        setCompanyName(transaction.getCompanyName());
        setPrice(transaction.getPrice());
        setConsignmentPrice(transaction.getConsignmentPrice());
        setDiscount(transaction.getDiscount());

        setSellRegNum(transaction.getSellRegNum());
        setSellCompanyName(transaction.getSellCompanyName());
        setSellConsignmentPrice(transaction.getSellConsignmentPrice());
        setSellPrice(transaction.getSellPrice());
        setSellDiscount(transaction.getSellDiscount());
        setSoldDate(transaction.getSoldDate());
    }
    private LocalDateTime receiveDate;
}
