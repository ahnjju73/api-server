package helmet.bikelab.apiserver.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class PushComponent extends OriginObject {

    private final ExecutorService executorService;

    @PostConstruct
    public void initialize() {
        try {
            InputStream inputStream = new ClassPathResource("bikelabs-b6f90-firebase-adminsdk-kx3wd-14cafb3ace.json").getInputStream();
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream)).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void pushNotification(String targetToken, String title, String body){
        executorService.submit(() -> {
            try {
                Message message = Message
                        .builder()
                        .setToken(targetToken)
                        .setNotification(
                                Notification.builder()
                                        .setTitle(title)
                                        .setBody(body)
                                        .build())
                        .build();
                FirebaseMessaging.getInstance().send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
