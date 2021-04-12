package helmet.bikelab.apiserver.services.fines;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.Fines;
import helmet.bikelab.apiserver.domain.lease.LeaseFine;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.fine.*;
import helmet.bikelab.apiserver.repositories.BikesRepository;
import helmet.bikelab.apiserver.repositories.ClientsRepository;
import helmet.bikelab.apiserver.repositories.FinesRepository;
import helmet.bikelab.apiserver.repositories.LeaseFineRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class FinesService extends SessService {
    private final FinesRepository finesRepository;
    private final LeaseFineRepository leaseFineRepository;
    private final BikesRepository bikesRepository;
//    private final Bike
    private final AutoKey autoKey;

    public BikeSessionRequest fetchFineList(BikeSessionRequest request){
        Map response = new HashMap();
        List<Fines> fines = finesRepository.findAll();
        List<FetchFinesResponse> fetchFineResponseList = new ArrayList<>();
        for(Fines fine : fines){
            FetchFinesResponse fetchFinesResponse = new FetchFinesResponse();
            fetchFinesResponse.setFineDate(fine.getFineDt());
            fetchFinesResponse.setFee(fine.getFee());
            fetchFinesResponse.setPaidFee(fine.getPaidFee());
            fetchFinesResponse.setFineNum(fine.getFineNum());
            fetchFinesResponse.setFineId(fine.getFineId());
            fetchFineResponseList.add(fetchFinesResponse);
        }
        response.put("fines", fetchFineResponseList);
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchFine(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        FetchFineRequest fetchFineRequest = map(param, FetchFineRequest.class);
        Fines fines = finesRepository.findByFineId(fetchFineRequest.getFineId());
        LeaseFine leaseFine = leaseFineRepository.findByFine(fines);
        FetchFineResponse fetchFineResponse = new FetchFineResponse();
        if(leaseFine!=null) {
            Clients clients = leaseFine.getLease().getClients();
            Bikes bikes = leaseFine.getLease().getBike();
            fetchFineResponse.setClientName(clients.getClientInfo().getName());
            fetchFineResponse.setClientId(clients.getClientId());
            fetchFineResponse.setBikeNum(bikes.getCarNum());
            fetchFineResponse.setBikeId(bikes.getBikeId());
        }
        fetchFineResponse.setFineId(fines.getFineId());
        fetchFineResponse.setFineDate(fines.getFineDt());
        fetchFineResponse.setFee(fines.getFee());
        fetchFineResponse.setPaidFee(fines.getPaidFee());
        fetchFineResponse.setFineId(fines.getFineNum());
        response.put("fine", fetchFineResponse);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addFine(BikeSessionRequest request){
        Map param = request.getParam();
        AddFineRequest addFineRequest = map(param, AddFineRequest.class);
        addFineRequest.checkValidation();
        Fines fine = new Fines();
        String fineId = autoKey.makeGetKey("fine");

        fine.setFineId(fineId);
        fine.setFineDt(addFineRequest.getFineDate());
        fine.setFineNum(addFineRequest.getFineNum());
        fine.setFee(addFineRequest.getFee());
        finesRepository.save(fine);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateFine(BikeSessionRequest request){
        Map param = request.getParam();
        UpdateFineRequest updateFineRequest = map(param, UpdateFineRequest.class);
        Fines fine = finesRepository.findByFineId(updateFineRequest.getFineId());
        fine.setFineDt(updateFineRequest.getFineDate());
        fine.setExpireDt(updateFineRequest.getExpireDate());
        fine.setFee(updateFineRequest.getFee());
        fine.setFineNum(updateFineRequest.getFinNum());
        fine.setPaidFee(updateFineRequest.getPaidFee());

        finesRepository.save(fine);

        return request;
    }

    @Transactional
    public BikeSessionRequest deleteFine(BikeSessionRequest request){
        Map param = request.getParam();
        DeleteFineRequest deleteFineRequest = map(param, DeleteFineRequest.class);
        deleteFineRequest.checkValidation();
        Fines fine = finesRepository.findByFineId(deleteFineRequest.getFineId());
        finesRepository.delete(fine);
        return request;
    }

}
