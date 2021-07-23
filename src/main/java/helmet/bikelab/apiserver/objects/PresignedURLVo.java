package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PresignedURLVo extends OriginObject {

    private String bucket;
    private String fileKey;
    private String url;
    private String filename;
    public void checkValidation(){
        if (!bePresent(this.bucket)) withException("500-005");
        if (!bePresent(this.fileKey)) withException("500-005");
        if (!bePresent(this.url)) withException("500-005");

    }
}
