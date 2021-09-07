package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ExpenseDto {
    private Long expenseNo;
    private String expenseType;
    private String companyName;
    private String regNum;
    private Integer price;
    private Integer number;
    private String description;
}

