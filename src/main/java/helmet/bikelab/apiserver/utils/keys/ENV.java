package helmet.bikelab.apiserver.utils.keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ENV {

    public static String AWS_S3_QUEUE_DOMAIN;
    public static String AWS_S3_QUEUE_BUCKET;
    public static String AWS_S3_ORIGIN_DOMAIN;
    public static String AWS_S3_ORIGIN_BUCKET;
    public static String AMAZON_CREDENTIAL_ACCESS_KEY;
    public static String AMAZON_CREDENTIAL_SECRET_KEY;

    public static String ALIGO_DOMAIN_KAKAO;
    public static String ALIGO_DOMAIN_SMS;
    public static String ALIGO_API_KEY;

    public static String JANDI_URL;

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

    @Value("${aligo.domain.kakao}")
    public void setAligoDomainKakao(String aligoDomainKakao) {
        ALIGO_DOMAIN_KAKAO = aligoDomainKakao;
    }

    @Value("${aligo.domain.sms}")
    public void setAligoDomainSms(String aligoDomainSms) {
        ALIGO_DOMAIN_SMS = aligoDomainSms;
    }

    @Value("${aligo.header.aws-api-gateway_key}")
    public void setAligoApiKey(String aligoApiKey) {
        ALIGO_API_KEY = aligoApiKey;
    }

    @Value("${jandi.url}")
    public void setJandiUrl(String jandiUrl) {
        JANDI_URL = jandiUrl;
    }

}


