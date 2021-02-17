package helmet.bikelab.apiserver.objects.bikelabs.systems;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MenuDto {

    private String upMenuNm;
    private List<ProgramDto> menu;
}
