package helmet.bikelab.apiserver.services.fines;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.Fines;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.FineStatusTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.fine.AddFineRequest;
import helmet.bikelab.apiserver.objects.bikelabs.fine.FetchFineRequest;
import helmet.bikelab.apiserver.objects.bikelabs.fine.FetchFineResponse;
import helmet.bikelab.apiserver.objects.bikelabs.fine.UpdateFineRequest;
import helmet.bikelab.apiserver.repositories.BikesRepository;
import helmet.bikelab.apiserver.repositories.ClientsRepository;
import helmet.bikelab.apiserver.repositories.FinesRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class FinesService extends SessService {
    private final FinesRepository finesRepository;
    private final ClientsRepository clientsRepository;
    private final BikesRepository bikesRepository;

    public BikeSessionRequest fetchFine(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        FetchFineRequest fetchFineRequest = map(param, FetchFineRequest.class);
        Fines fines = finesRepository.findByFineNum(fetchFineRequest.getFineNum());
        FetchFineResponse fetchFineResponse = new FetchFineResponse();
        fetchFineResponse.setFineDate(fines.getFineDt());
        fetchFineResponse.setFee(fines.getFee());
        fetchFineResponse.setPaidFee(fines.getPaidFee());
        Bikes bike = fines.getBike();
        Leases lease = bike.getLease();
        Clients client = lease.getClients();
        fetchFineResponse.setBikeNum(bike.getCarNum());
        fetchFineResponse.setClientName(client.getClientInfo().getName());

        response.put("fine", fetchFineResponse);
        return request;
    }

    @Transactional
    public BikeSessionRequest addFine(BikeSessionRequest request){
        Map param = request.getParam();
        AddFineRequest addFineRequest = map(param, AddFineRequest.class);
        Fines fine = new Fines();
        fine.setFineDt(addFineRequest.getFineDate());
        fine.setFineNum(addFineRequest.getFineNum());
        fine.setFee(addFineRequest.getFee());
        Bikes bike = bikesRepository.findByBikeId(addFineRequest.getBikeId());
        fine.setBikeNo(bike.getBikeNo());
        finesRepository.save(fine);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateFine(BikeSessionRequest request){
        Map param = request.getParam();
        UpdateFineRequest updateFineRequest = map(param, UpdateFineRequest.class);
        Fines fine = finesRepository.findByFineNum(updateFineRequest.getFineNum());
        fine.setFineDt(updateFineRequest.getFineDate());
        Bikes bike = bikesRepository.findByBikeId(updateFineRequest.getBikeId());
        fine.setBikeNo(bike.getBikeNo());
        fine.setFee(updateFineRequest.getFee());
        finesRepository.save(fine);

        return request;
    }

}
