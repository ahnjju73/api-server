package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "client_info", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClientInfo {

    @Id
    @JsonIgnore
    @Column(name = "client_no")
    private Integer clientNo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    // 고객명
    @Column(name = "name",  length =  100)
    private String name;

    @Column(name = "phone", length = 45)
    private String phone;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

}