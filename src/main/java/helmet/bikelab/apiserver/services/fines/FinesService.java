package helmet.bikelab.apiserver.services.fines;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.Fines;
import helmet.bikelab.apiserver.domain.lease.LeaseFine;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.fine.*;
import helmet.bikelab.apiserver.repositories.*;
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
            fetchFinesResponse.setFineExpireDate(fine.getExpireDt());
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
        LeaseFine leaseFine = leaseFineRepository.findByFine_FineId(fines.getFineId());
        FetchFineResponse fetchFineResponse = new FetchFineResponse();
        if(leaseFine!=null) {
            Clients clients = leaseFine.getLease().getClients();
            Bikes bikes = leaseFine.getLease().getBike();
            fetchFineResponse.setClientName(clients.getClientInfo().getName());
            fetchFineResponse.setClientId(clients.getClientId());
            fetchFineResponse.setBikeNum(bikes.getCarNum());
            fetchFineResponse.setBikeId(bikes.getBikeId());
        }
        fetchFineResponse.setFineExpireDate(fines.getExpireDt());
        fetchFineResponse.setFineNum(fines.getFineNum());
        fetchFineResponse.setFineId(fines.getFineId());
        fetchFineResponse.setFineDate(fines.getFineDt());
        fetchFineResponse.setFee(fines.getFee());
        fetchFineResponse.setPaidFee(fines.getPaidFee());
        response.put("fine", fetchFineResponse);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addFine(BikeSessionRequest request){
        Map param = request.getParam();
        AddFineRequest addFineRequest = map(param, AddFineRequest.class);
        addFineRequest.checkValidation();
        if(finesRepository.countFinesByFineNum(addFineRequest.getFineNum())>0) withException("700-009");
        Fines fine = new Fines();
        LeaseFine leaseFine = new LeaseFine();
        Bikes bike = bikesRepository.findByBikeId(addFineRequest.getBikeId());
        if(!bePresent(bike)) withException("700-011");
        String fineId = autoKey.makeGetKey("fine");
        fine.setPaidFee(addFineRequest.getPaidFee());
        fine.setFineId(fineId);
        fine.setFineDt(addFineRequest.getFineDate());
        fine.setFineNum(addFineRequest.getFineNum());
        fine.setFee(addFineRequest.getFee());
        fine.setExpireDt(addFineRequest.getExpireDate());
        finesRepository.save(fine);
        leaseFine.setFineNo(fine.getFineNo());
        leaseFine.setLeaseNo(bike.getLease().getLeaseNo());
        leaseFineRepository.save(leaseFine);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateFine(BikeSessionRequest request){
        Map param = request.getParam();
        UpdateFineRequest updateFineRequest = map(param, UpdateFineRequest.class);
        Fines fine = finesRepository.findByFineId(updateFineRequest.getFineId());
        if(!fine.getFineNum().equals(updateFineRequest.getFineNum())){
            if(finesRepository.countFinesByFineNum(updateFineRequest.getFineNum())>0) withException("700-010");
        }
        Bikes bike = bikesRepository.findByBikeId(updateFineRequest.getBikeId());
        fine.setFineDt(updateFineRequest.getFineDate());
        fine.setExpireDt(updateFineRequest.getExpireDate());
        fine.setFee(updateFineRequest.getFee());
        fine.setFineNum(updateFineRequest.getFineNum());
        fine.setPaidFee(updateFineRequest.getPaidFee());
        finesRepository.save(fine);
        LeaseFine leaseFine = leaseFineRepository.findByFine_FineId(fine.getFineId());
        leaseFine.setLeaseNo(bike.getLease().getLeaseNo());

        return request;
    }

    @Transactional
    public BikeSessionRequest deleteFine(BikeSessionRequest request){
        Map param = request.getParam();
        DeleteFineRequest deleteFineRequest = map(param, DeleteFineRequest.class);
        deleteFineRequest.checkValidation();
        Fines fine = finesRepository.findByFineId(deleteFineRequest.getFineId());
        finesRepository.delete(fine);
        LeaseFine leaseFine = leaseFineRepository.findByFine_FineId(fine.getFineId());
        if(bePresent(leaseFine)){
            leaseFineRepository.delete(leaseFine);
        }
        return request;
    }

    public BikeSessionRequest fetchFinesByBike(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        FetchFineRequest fine = map(param, FetchFineRequest.class);
        Fines fines = finesRepository.findByFineId(fine.getFineId());
        if(fines==null) withException("");
        LeaseFine leaseFine = leaseFineRepository.findByFine_FineId(fines.getFineId());
        Leases lease = leaseFine.getLease();
        List<LeaseFine> leaseFineList = leaseFineRepository.findAllByLease_LeaseId(lease.getLeaseId());
        List<Fines> finesList = new ArrayList<>();
        for(LeaseFine lf : leaseFineList)
            finesList.add(lf.getFine());
        response.put("fines", finesList);
        request.setResponse(response);
        return request;
    }




}
