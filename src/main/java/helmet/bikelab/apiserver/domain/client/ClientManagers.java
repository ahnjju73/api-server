package helmet.bikelab.apiserver.domain.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "client_managers")
public class ClientManagers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manager_no")
    private Long managerNo;

    @Column(name = "client_no")
    private Integer clientNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @Column(name = "name", length = 45)
    private String name;

    @Column(name = "phone", length = 45)
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "department")
    private String department;
}
