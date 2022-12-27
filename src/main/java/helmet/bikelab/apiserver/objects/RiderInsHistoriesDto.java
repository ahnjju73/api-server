package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RiderInsHistoriesDto {
    private String log;
    private String updatedAt;

    public void setUpdatedAt(LocalDateTime updatedAt){
        this.updatedAt = updatedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
