package helmet.bikelab.apiserver.domain.embeds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Embeddable
public class ModelReview {

    @Column(name = "rate", columnDefinition = "TINYINT(3)")
    private Integer rate;

    @Column(name = "review", columnDefinition = "MEDIUMTEXT")
    private String review;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "review_tag", length = 21)
    private String reviewTag;

}