package helmet.bikelab.apiserver.domain.embeds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.util.UUID;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Embeddable
public class ModelInsuranceImage {
    private String id = UUID.randomUUID().toString().replaceAll("-", "");
    private String uri;
    private String domain;
    private String fileName;

}
//[{"id": "16e67b5377d54efaa345a93a04d19f8c", "uri": "/estimates/reviews/EST2022032400008/R000000244/thumbnail/3dfe68d1-2371-457c-846b-a9a04e00ddb2.jpg",
// "domain": "https://bikelabs-test-storage.s3.ap-northeast-2.amazonaws.com", "mediaType": "IMAGE", "mediaTypeCode": "IMAGE"}]