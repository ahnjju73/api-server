package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.embeds.ModelBankAccount;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.types.SettleStatusTypes;
import helmet.bikelab.apiserver.domain.types.converters.SettleStatusTypesConverter;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "sample_files", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SampleFiles extends OriginObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sample_no")
    private Integer sampleNo;

    @Column(name = "links")
    private String links;

}
