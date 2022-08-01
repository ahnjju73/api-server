package helmet.bikelab.apiserver.services;

import helmet.bikelab.apiserver.domain.Estimates;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.types.EstimateStatusTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.FetchEstimateParameter;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.requests.EstimateRequestListDto;
import helmet.bikelab.apiserver.objects.requests.FetchUnpaidEstimatesRequest;
import helmet.bikelab.apiserver.objects.requests.PageableRequest;
import helmet.bikelab.apiserver.objects.responses.EstimateByIdResponse;
import helmet.bikelab.apiserver.objects.responses.FetchUnpaidEstimateResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.ClientWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EstimateService extends SessService {
    private final EstimatesRepository estimatesRepository;
    private final ClientsRepository clientsRepository;
    private final CommonWorker commonWorker;
    private final EstimatePartsRepository estimatePartsRepository;
    private final LeaseRepository leaseRepository;
    private final BikeWorker bikeWorker;
    private final ClientWorker clientWorker;
    private final RiderRepository riderRepository;
    private final ShopsRepository shopsRepository;
    private final BikesRepository bikesRepository;

    public BikeSessionRequest excelDownloadEstimates(BikeSessionRequest request){
        Map param = request.getParam();
        EstimateRequestListDto estimateRequestListDto = map(param, EstimateRequestListDto.class);
        FetchEstimateParameter fetchEstimateParameter = setRequestParamOfFetchingEstimate(estimateRequestListDto);
        fetchEstimateParameter.setLimited(null);
        Map data = map(fetchEstimateParameter, HashMap.class);
        List list = getList("estimate.estimates.fetchEstimateList", data);
        request.setResponse(list);
        return request;
    }

    public BikeSessionRequest fetchEstimates(BikeSessionRequest request){
        Map param = request.getParam();
        EstimateRequestListDto estimateRequestListDto = map(param, EstimateRequestListDto.class);
        FetchEstimateParameter fetchEstimateParameter = setRequestParamOfFetchingEstimate(estimateRequestListDto);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(fetchEstimateParameter, "estimate.estimates.fetchEstimateList", "estimate.estimates.countEstimateList", "rownum");
        request.setResponse(responseListDto);
        return request;
    }

    private FetchEstimateParameter setRequestParamOfFetchingEstimate(EstimateRequestListDto estimateRequestListDto) {
        FetchEstimateParameter fetchEstimateParameter = new FetchEstimateParameter(estimateRequestListDto);
        if(bePresent(estimateRequestListDto.getGroupId())){
            List<Clients> clientListByGroupId = clientWorker.getClientListByGroupId(estimateRequestListDto.getGroupId());
            fetchEstimateParameter.setSearchClientNo(clientListByGroupId);
        }
        if(bePresent(estimateRequestListDto.getClientId())){
            Clients byClientId = clientsRepository.findByClientId(estimateRequestListDto.getClientId());
            fetchEstimateParameter.setSearchClientNo(byClientId);
        }
        if(bePresent(estimateRequestListDto.getRiderId())){
            Riders byRiderId = riderRepository.findByRiderId(estimateRequestListDto.getRiderId());
            fetchEstimateParameter.setSearchRiderNo(byRiderId);
        }
        if(bePresent(estimateRequestListDto.getShopId())){
            Shops byShopId = shopsRepository.findByShopId(estimateRequestListDto.getShopId());
            fetchEstimateParameter.setSearchShopNo(byShopId);
        }
        if(bePresent(estimateRequestListDto.getBikeNumber())){
            Bikes byCarNum = bikesRepository.findByCarNum(estimateRequestListDto.getBikeNumber());
            fetchEstimateParameter.setSearchBikeNo(byCarNum);
        }
        if(bePresent(estimateRequestListDto.getLimited()))
            fetchEstimateParameter.setLimited(estimateRequestListDto.getLimited());
        else
            fetchEstimateParameter.setLimited("limited");
        fetchEstimateParameter.setIsDeleted(estimateRequestListDto.getIsDeleted());
        fetchEstimateParameter.setAccident(estimateRequestListDto.getAccident());
        return fetchEstimateParameter;
    }

    public BikeSessionRequest fetchUnpaidEstimates(BikeSessionRequest request){
        Map param = request.getParam();
        String clientId = (String) param.get("client_id");
        FetchUnpaidEstimatesRequest fetchUnpaidEstimatesRequest = map(param, FetchUnpaidEstimatesRequest.class);
        Clients client = clientsRepository.findByClientId(clientId);
        if(bePresent(client)){
            fetchUnpaidEstimatesRequest.setClientNo(client.getClientNo());
        }

        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(fetchUnpaidEstimatesRequest, "estimate.estimates.fetchUnpaidEstimateList", "estimate.estimates.countUnpaidEstimateList", "rownum");
        FetchUnpaidEstimateResponse toReturn = new FetchUnpaidEstimateResponse();
        toReturn.setTotal(responseListDto.getTotal());
        toReturn.setItems(responseListDto.getItems());
        toReturn.setNextToken(responseListDto.getNextToken());
        if(bePresent(client)){
            List<Estimates> allByClient_clientId = estimatesRepository.getUnpaidEstimatesByClientNo(client.getClientNo(), EstimateStatusTypes.COMPLETED, EstimateStatusTypes.PAID);
            Integer total = 0;
            Integer paid = 0;
            for(Estimates e : allByClient_clientId){
                if(e.getTotalPrice().equals(e.getPaidFee())) continue;
                total += e.getTotalPrice();
                paid += e.getPaidFee();
            }
            toReturn.setTotalPrice(total);
            toReturn.setPaidFee(paid);
        }

        request.setResponse(toReturn);
        return request;
    }

    @Transactional
    public BikeSessionRequest payUnpaidEstimatesByClients(BikeSessionRequest request){
        Map param = request.getParam();
        String clientId = (String) param.get("client_id");
        Integer payingFee = (Integer) param.get("paying_fee");
        List<Estimates> allByClient_clientId = estimatesRepository.getUnpaidEstimates(clientsRepository.findByClientId(clientId).getClientNo());
        for(int i = 0; i < allByClient_clientId.size(); i++){
            Estimates estimates = allByClient_clientId.get(i);
            int unpaidFee= estimates.getTotalPrice() - estimates.getPaidFee();
            if(unpaidFee == 0)
                continue;
            if(unpaidFee <= payingFee) {
                payingFee -= unpaidFee;
                estimates.setPaidFee(estimates.getTotalPrice());
                estimates.setPaidAt(LocalDateTime.now());
            }else{
                estimates.setPaidFee(estimates.getPaidFee() + payingFee);
                payingFee = 0;
            }
            estimatesRepository.save(estimates);
            if(payingFee == 0)
                break;
        }
        return request;
    }

    public BikeSessionRequest fetchClientsWithUnpaidEstimate(BikeSessionRequest request){
        List<Integer> clientsList = new ArrayList<>();
        List<Map> items = getList("estimate.estimates.fetchUnpaidEstimates", request.getParam());
        for(Map map : items){
            Integer totalPrice = (Integer) map.get("total_price");
            Integer paidFee = (Integer) map.get("paid_fee");
            if(totalPrice <= paidFee)
                continue;
            Integer clientNo = (Integer) map.get("client_no");
            clientsList.add(clientNo);
        }
        Set<Integer> set = new HashSet<>(clientsList);
        List<Integer> toReturn = new ArrayList<>(set);
        List<ClientDto> unpaidClients = new ArrayList<>();
        for(Integer clientNo : toReturn){
            Clients clients = clientsRepository.findById(clientNo).get();
            ClientDto clientDto = new ClientDto();
            clientDto.setClientId(clients.getClientId());
            clientDto.setClientName(clients.getClientInfo().getName());
            unpaidClients.add(clientDto);
        }
        request.setResponse(unpaidClients);
        return request;
    }

    public BikeSessionRequest fetchEstimateDetail(BikeSessionRequest request){
        Map param = request.getParam();
        String estimateId = (String)param.get("estimate_id");
        Estimates byEstimateId = estimatesRepository.findByEstimateId(estimateId);
        param.put("estimate_no", byEstimateId.getEstimateNo());
        param.put("client_no", byEstimateId.getClientNo());
        List parts = getList("estimate.estimates.fetchEstimatePartsById", param);
        List attachments = getList("estimate.estimates.fetchEstimateAttachmentsById", param);
        EstimateByIdResponse estimateByIdResponse = new EstimateByIdResponse();
        estimateByIdResponse.setEstimate(byEstimateId);
        estimateByIdResponse.setParts(parts);
        estimateByIdResponse.setAttachments(attachments);
        estimateByIdResponse.setLease(leaseRepository.findByBike_BikeId(byEstimateId.getBike().getBikeId()));
        if(estimatePartsRepository.findAllByEstimate_EstimateId(estimateId) == null||estimatePartsRepository.findAllByEstimate_EstimateId(estimateId).size() == 0)
            estimateByIdResponse.setWorkingPrice(bikeWorker.getWorkingPrice(byEstimateId.getBike().getCarModel()));
        else
            estimateByIdResponse.setWorkingPrice(estimatePartsRepository.findAllByEstimate_EstimateId(estimateId).get(0).getWorkingPrice());
        request.setResponse(estimateByIdResponse);
        return request;
    }

    public BikeSessionRequest fetchReviewsByShop(BikeSessionRequest request){
        PageableRequest pageableRequest = map(request.getParam(), PageableRequest.class);
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        String shopId = (String) request.getParam().get("shop_id");
        Page<Estimates> reviewList;
        if(bePresent(shopId)){
            reviewList = estimatesRepository.findByShop_ShopIdAndReviewNotNullAndEstimateStatusType(shopId, EstimateStatusTypes.COMPLETED, pageable);
        } else{
            reviewList = estimatesRepository.findAllByReviewNotNullAndEstimateStatusType(EstimateStatusTypes.COMPLETED, pageable);
        }
        request.setResponse(reviewList);
        return request;
    }



}
