package helmet.bikelab.apiserver.objects;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public void copyObjectToOrigin(){
        AmazonS3 amazonS3 = AmazonUtils.amazonS3();
        CopyObjectRequest objectRequest = new CopyObjectRequest(getBucket(), getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, getFileKey());
        amazonS3.copyObject(objectRequest);
    }

}
