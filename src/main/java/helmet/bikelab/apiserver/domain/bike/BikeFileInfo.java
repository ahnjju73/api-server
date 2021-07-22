package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

//@Entity
//@Getter
//@Setter
//@Table(name = "bike_file_info")
//@NoArgsConstructor
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeFileInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bike_file_info_no")
    private Integer bikeFileInfoNo;

    @Column(name = "bike_no")
    private Integer bikeNo;

    @ManyToOne
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "bucket")
    private String bucket;

    @Column(name = "file_key")
    private String fileKey;

    @Column(name = "url")
    private String url;
}
