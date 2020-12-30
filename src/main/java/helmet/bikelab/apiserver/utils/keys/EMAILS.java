package helmet.bikelab.apiserver.utils.keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EMAILS {
    public static String ID_FROM_EMAIL;
    public static String PWD_FROM_EMAIL;
    public static String EMAIL_VALIDATION_LINK;

    @Value("${emails.id_from_email}")
    public void setIdFromEmail(String idFromEmail) {
        ID_FROM_EMAIL = idFromEmail;
    }

    @Value("${emails.pwd_from_email}")
    public void setPwdFromEmail(String pwdFromEmail) {
        PWD_FROM_EMAIL = pwdFromEmail;
    }

}


