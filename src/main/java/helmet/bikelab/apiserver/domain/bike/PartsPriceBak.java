package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.SampleFiles;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.objects.requests.UploadPartsPrice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "parts_price_bak")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsPriceBak {

    public PartsPriceBak(){

    }

    public PartsPriceBak(Parts part, UploadPartsPrice partsPrice, String description, SampleFiles sampleFile, BikeUser user){
        this.partsNo = part.getPartNo();
        this.partsPrices = partsPrice.getPartsPrice();
        this.workingHours = partsPrice.getWorkingHour();
        this.bikeUserNo = user.getUserNo();
        this.description = description;
        this.sampleNo = sampleFile.getSampleNo();
    }

    public void initialized(Parts part, BikeUser user){
        this.partsNo = part.getPartNo();
        this.partsPrices = part.getPartsPrices();
        this.workingHours = part.getWorkingHours();
        this.bikeUserNo = user.getUserNo();
        this.description = "최초등록";
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bak_no")
    private Long bakNo;

    @Column(name = "parts_no")
    private Long partsNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parts_no", insertable = false, updatable = false)
    private Parts part;

    @Column(name = "parts_prices")
    private Integer partsPrices;

    @Column(name = "working_hours")
    private Double workingHours;

    @Column(name = "created_no", length = 21)
    private Integer bikeUserNo;

    @ManyToOne
    @JoinColumn(name = "created_no", referencedColumnName = "user_no", insertable = false, updatable = false)
    private BikeUser bikeUser;

    @Column(name = "created_at", columnDefinition = "default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "sample_no")
    private Integer sampleNo;

    @ManyToOne
    @JoinColumn(name = "sample_no", insertable = false, updatable = false)
    private SampleFiles sampleFiles;

}
