package helmet.bikelab.apiserver.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import helmet.bikelab.apiserver.services.internal.Workspace;
import helmet.bikelab.apiserver.utils.keys.ENV;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Senders extends Workspace {

    private final ExecutorService executorService;
    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000); // 읽기시간초과, ms
        factory.setConnectTimeout(3000); // 연결시간초과, ms
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100) // connection pool 적용
                .setMaxConnPerRoute(5) // connection pool 적용
                .build();
        factory.setHttpClient(httpClient); // 동기실행에 사용될 HttpClient 세팅
        restTemplate = new RestTemplate(factory);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Charset utf8 = Charset.forName("UTF-8");
        MediaType mediaType = new MediaType("application", "json", utf8);
        headers.setContentType(mediaType);
    }

    public void toSlack(String message, String url) {
        executorService.submit(() -> {
            Map<String, Object> params = new HashMap<>();
            params.put("themeColor", "BCF7DA");
            params.put("title", "test");
            params.put("text", message);

            String body = null;
            try {
                body = mapper.writeValueAsString(params);
                if(body != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
                    HttpEntity entity = new HttpEntity(body, headers);
                    ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, entity, String.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }
        });
    }

    public void withPhoneMessage(String message, String ...phoneNumber){
        if(phoneNumber.length == 0) return;
        if(ENV.IS_RELEASE){
            executorService.submit(() -> {
                MultiValueMap<String, String> params = new LinkedMultiValueMap();
                params.add("key", ENV.ALIGO_ACCESS_KEY);
                params.add("user_id", ENV.ALIGO_USERID);
                params.add("sender", ENV.ALIGO_SENDER);
                params.add("msg", message);

                String collect = Arrays.stream(phoneNumber).collect(Collectors.joining("<"));
                params.add("receiver", collect);

                try {
                    if(params != null) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                        HttpEntity entity = new HttpEntity(params, headers);
                        restTemplate.postForEntity(ENV.ALIGO_DOMAIN + "/send/", entity, String.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
