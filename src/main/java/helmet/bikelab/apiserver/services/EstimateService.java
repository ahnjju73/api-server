package helmet.bikelab.apiserver.services;

import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.EstimateParts;
import helmet.bikelab.apiserver.domain.Estimates;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.EstimateDto;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.requests.EstimateRequestListDto;
import helmet.bikelab.apiserver.objects.requests.FetchUnpaidEstimatesRequest;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.responses.EstimateByIdResponse;
import helmet.bikelab.apiserver.objects.responses.FetchUnpaidEstimateResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EstimateService extends SessService {
    private final EstimatesRepository estimatesRepository;
    private final EstimateAttachmentRepository estimateAttachmentRepository;
    private final ClientsRepository clientsRepository;
    private final CommonWorker commonWorker;
    private final EstimatePartsRepository estimatePartsRepository;
    private final BikeWorker bikeWorker;

    public BikeSessionRequest fetchEstimates(BikeSessionRequest request){
        Map param = request.getParam();
        EstimateRequestListDto requestListDto = map(param, EstimateRequestListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "estimate.estimates.fetchEstimateList", "estimate.estimates.countEstimateList", "estimate_id");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchUnpaidEstimates(BikeSessionRequest request){
        Map param = request.getParam();
        String clientId = (String) param.get("client_id");
        FetchUnpaidEstimatesRequest fetchUnpaidEstimatesRequest = map(param, FetchUnpaidEstimatesRequest.class);
        if(clientId != null)
            fetchUnpaidEstimatesRequest.setClientNo(clientsRepository.findByClientId(clientId).getClientNo());
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(fetchUnpaidEstimatesRequest, "estimate.estimates.fetchUnpaidEstimateList", "estimate.estimates.countUnpaidEstimateList", "rownum");
        FetchUnpaidEstimateResponse toReturn = new FetchUnpaidEstimateResponse();
        toReturn.setTotal(responseListDto.getTotal());
        toReturn.setItems(responseListDto.getItems());
        toReturn.setNextToken(responseListDto.getNextToken());
        List<Estimates> allByClient_clientId = estimatesRepository.getUnpaidEstimates(clientsRepository.findByClientId(clientId).getClientNo());
        Integer total = 0;
        Integer paid = 0;
        for(Estimates e : allByClient_clientId){
            if(e.getTotalPrice() ==  e.getPaidFee())
                continue;
            total += e.getTotalPrice();
            paid += e.getPaidFee();
        }
        toReturn.setTotalPrice(total);
        toReturn.setPaidFee(paid);
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
        if(estimatePartsRepository.findAllByEstimate_EstimateId(estimateId) == null||estimatePartsRepository.findAllByEstimate_EstimateId(estimateId).size() == 0)
            estimateByIdResponse.setWorkingPrice(bikeWorker.getWorkingPrice(byEstimateId.getBike().getCarModel()));
        else
            estimateByIdResponse.setWorkingPrice(estimatePartsRepository.findAllByEstimate_EstimateId(estimateId).get(0).getWorkingPrice());
        request.setResponse(estimateByIdResponse);
        return request;
    }




}
