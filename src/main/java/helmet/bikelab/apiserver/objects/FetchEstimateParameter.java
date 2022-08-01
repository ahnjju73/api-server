package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.objects.requests.EstimateRequestListDto;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchEstimateParameter extends RequestListDto {

    public FetchEstimateParameter(EstimateRequestListDto estimateRequest) {
        this.searchEstimateId = estimateRequest.getEstimateId();
        this.searchStartAt = estimateRequest.getStartAt();
        this.searchEndAt = estimateRequest.getEndAt();
        if(bePresent(estimateRequest.getStatus())){
            this.searchStatusCode = estimateRequest.getStatus().getStatus();
        }
        this.setNextToken(estimateRequest.getNextToken());
    }

    @JsonIgnore
    private List<Clients> searchClientList = new ArrayList<>();
    private String searchEstimateId;
    private String searchStatusCode;
    private String isDeleted;
    private String searchClientNo;
    private String searchRiderNo;
    private String searchShopNo;
    private String searchBikeNo;
    private String searchStartAt;
    private String searchEndAt;

    private String accident;

    private String limited = "limited";

    public void setSearchShopNo(Shops shop) {
        if(bePresent(shop)) this.searchShopNo = shop.getShopNo().toString();
    }

    public void setSearchRiderNo(Riders rider) {
        if(bePresent(rider)) this.searchRiderNo = rider.getRiderNo().toString();
    }

    public void setSearchClientNo(Clients client) {
        if(bePresent(client)) {
            this.searchClientList.add(client);
        }
        this.searchClientNo = getClientIn();
    }

    public void setSearchClientNo(List<Clients> clientList) {
        if(bePresent(clientList)){
            this.searchClientList.addAll(clientList);
        }
        this.searchClientNo = getClientIn();
    }

    public void setSearchBikeNo(Bikes bike) {
        this.searchBikeNo = bike.getBikeNo().toString();
    }

    private String getClientIn(){
        String collect = this.searchClientList.stream().map(elm -> elm.getClientNo().toString()).collect(Collectors.joining(", "));
        return bePresent(collect) ? collect : "0";
    }

}
