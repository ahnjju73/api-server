package helmet.bikelab.apiserver.domain.embeds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Embeddable
public class ModelAttachment {
    private String uuid;
    private String uri;
    private String domain;
    private String fileName;

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ModelAttachment)){
            return false;
        }
        return uuid.equals(((ModelAttachment) obj).uuid);
    }
}