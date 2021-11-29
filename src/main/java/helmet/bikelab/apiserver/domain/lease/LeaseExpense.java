package helmet.bikelab.apiserver.domain.lease;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.types.ExpenseOptionTypes;
import helmet.bikelab.apiserver.domain.types.ExpenseTypes;
import helmet.bikelab.apiserver.domain.types.converters.ExpenseOptionTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.ExpenseTypesConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lease_expense")
public class LeaseExpense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_no")
    private Long expenseNo;

    @JsonIgnore
    @Column(name = "lease_no")
    private Integer leaseNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lease_no", updatable = false, insertable = false)
    private Leases lease;

    @Column(name = "expense_type", columnDefinition = "ENUM")
    @Convert(converter = ExpenseTypesConverter.class)
    private ExpenseTypes expenseTypes;

    @Column(name = "expense_type", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String expenseTypeCode;

    @Embedded
    private ModelTransaction transaction = new ModelTransaction();

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "expense_option", columnDefinition = "ENUM")
    @Convert(converter = ExpenseOptionTypesConverter.class)
    private ExpenseOptionTypes expenseOptionTypes;

    @Column(name = "expense_option", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String expenseOptionTypeCode;

}
