package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.ExpenseTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ExpenseDto extends OriginObject {
    private Long expenseNo;
    private String expenseType;
    private String companyName;
    private String regNum;
    private Integer price;
    private Integer number;
    private String description;
    private String expenseOptionType;

    public void validationCheck(){
        if(ExpenseTypes.getType(expenseType) == null) withException("");
        if(!bePresent(price)) withException("");
        if(!bePresent(number)) withException("");
    }
}

