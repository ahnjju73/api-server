package helmet.bikelab.apiserver.utils.keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ENV {
    public static String LIST_COUNT_DONE = "D";
    public static String AWS_S3_QUEUE_DOMAIN;
    public static String AWS_S3_QUEUE_BUCKET;
    public static String AWS_S3_ORIGIN_DOMAIN;
    public static String AWS_S3_ORIGIN_BUCKET;
    public static String AMAZON_CREDENTIAL_ACCESS_KEY;
    public static String AMAZON_CREDENTIAL_SECRET_KEY;

    public static String ALIGO_USERID;
    public static String ALIGO_DOMAIN;
    public static String ALIGO_ACCESS_KEY;
    public static String ALIGO_SENDER;
    public static Boolean IS_RELEASE;

    @Value("${system.is-release}")
    public void setIsRelease(Boolean isRelease) {
        IS_RELEASE = isRelease;
    }

    @Value("${aligo.sender}")
    public void setAligoSender(String aligoSender) {
        ALIGO_SENDER = aligoSender;
    }

    @Value("${aligo.user-id}")
    public void setAligoUserid(String aligoUserid) {
        ALIGO_USERID = aligoUserid;
    }

    @Value("${aligo.domain}")
    public void setAligoDomain(String aligoDomain) {
        ALIGO_DOMAIN = aligoDomain;
    }

    @Value("${aligo.access-key}")
    public void setAligoAccessKey(String aligoAccessKey) {
        ALIGO_ACCESS_KEY = aligoAccessKey;
    }

    @Value("${amazon.s3.queue.domain}")
    public void setAwsS3QueueDomain(String awsS3QueueDomain) {
        AWS_S3_QUEUE_DOMAIN = awsS3QueueDomain;
    }

    @Value("${amazon.s3.queue.bucket}")
    public void setAwsS3QueueBucket(String awsS3QueueBucket) {
        AWS_S3_QUEUE_BUCKET = awsS3QueueBucket;
    }

    @Value("${amazon.s3.origin.bucket}")
    public void setAwsS3OriginBucket(String awsS3OriginBucket) {
        AWS_S3_ORIGIN_BUCKET = awsS3OriginBucket;
    }

    @Value("${amazon.s3.origin.domain}")
    public void setAwsS3OriginDomain(String awsS3OriginDomain) {
        AWS_S3_ORIGIN_DOMAIN = awsS3OriginDomain;
    }

    @Value("${amazon.credential.accessKey}")
    public void setAmazonCredentialAccessKey(String amazonCredentialAccessKey) {
        AMAZON_CREDENTIAL_ACCESS_KEY = amazonCredentialAccessKey;
    }

    @Value("${amazon.credential.secretKey}")
    public void setAmazonCredentialSecretKey(String amazonCredentialSecretKey) {
        AMAZON_CREDENTIAL_SECRET_KEY = amazonCredentialSecretKey;
    }

}


