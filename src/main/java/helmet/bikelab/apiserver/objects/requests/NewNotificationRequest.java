package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NewNotificationRequest extends OriginObject {

    private String title;
    private String content;
    private List<PresignedURLVo> imageList;
    private List<PresignedURLVo> attachmentList;
    private String startAt;
    private String endAt;

    public void checkValidation(){
        if(!bePresent(title))
            withException("");
        if(!bePresent(content))
            withException("");
        if(!bePresent(endAt))
            withException("");

    }

}
