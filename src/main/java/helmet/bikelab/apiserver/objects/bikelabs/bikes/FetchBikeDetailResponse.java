package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.types.BikeStatusTypes;
import helmet.bikelab.apiserver.objects.CarModel;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.InsuranceDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeasesDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchBikeDetailResponse extends OriginObject {
    private ClientDto client;
    private LeasesDto lease;
    private String bikeId;
    private String vimNum;
    private String carNum;
    private CarModel model;
    private String color;
    private Integer year;
    private Double volume;
    private LocalDateTime receiveDt;
    private LocalDateTime registerDt;
    private InsuranceDto insuranceInfo;
    private String description;
    private ModelTransaction transaction = new ModelTransaction();
    private Boolean isBikemaster;
    private Boolean isMt;
    private String payerTypeCode;
    private Integer odometerByAdmin = 0;
    private BikeStatusTypes bikeStatusType;
    private String bikeStatusTypeCode;
    private String warehouse;

    private Integer attachmentCompletion = 0;
    private Integer attachmentRevocation = 0;
    private Integer attachmentProduction = 0;


    public void setBikeStatusType(BikeStatusTypes bikeStatusType) {
        this.bikeStatusType = bikeStatusType;
        this.bikeStatusTypeCode = bikeStatusType.getType();
    }

    public void setAttachmentCount(Integer completion, Integer revocation, Integer production){
        this.attachmentCompletion = completion;
        this.attachmentRevocation = revocation;
        this.attachmentProduction = production;
    }
}
