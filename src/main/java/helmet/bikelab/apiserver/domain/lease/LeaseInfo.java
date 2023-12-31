package helmet.bikelab.apiserver.domain.lease;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lease_info")
public class LeaseInfo {
    @Id
    @Column(name = "lease_no", nullable = false)
    private Integer leaseNo;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "lease_no")
    private Leases lease;

    @Column(name = "period")
    private Integer period;

    @Column(name = "start_dt")
    private LocalDate start;

    @Column(name = "end_dt")
    private LocalDate endDate;

    @Column(name = "contract_dt")
    private LocalDate contractDate;

    @Column(name = "note", columnDefinition = "MEDIUMTEXT")
    private String note;

}
