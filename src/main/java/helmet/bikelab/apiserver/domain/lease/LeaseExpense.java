package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.types.ExpenseTypes;
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

    @Column(name = "lease_no")
    private Integer leaseNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lease_no", updatable = false, insertable = false)
    private Leases lease;

    @Column(name = "expense_type", columnDefinition = "ENUM")
    @Convert(converter = ExpenseTypesConverter.class)
    private ExpenseTypes expenseTypes;

    @Embedded
    private ModelTransaction transaction = new ModelTransaction();

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

}
