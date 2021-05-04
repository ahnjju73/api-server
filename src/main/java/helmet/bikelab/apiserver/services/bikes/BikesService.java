package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.CommonCode;
import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.CarModel;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.*;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeasesDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BikesService extends SessService {

    private final BikesRepository bikesRepository;
    private final LeaseRepository leaseRepository;
    private final ClientsRepository clientsRepository;
    private final AutoKey autoKey;
    private final BikeModelsRepository bikeModelsRepository;

    public BikeSessionRequest fetchBikes(BikeSessionRequest request){
        List<Bikes> bikes = bikesRepository.findAll();
        List<FetchBikesResponse> fetchBikesResponses = new ArrayList<>();
        Map response = new HashMap();
        for(Bikes bike : bikes){
            FetchBikesResponse fetchBikesResponse = new FetchBikesResponse();
            CommonCode carModel = bike.getCarModel();
            fetchBikesResponse.setColor(bike.getColor());
            fetchBikesResponse.setNumber(bike.getCarNum());
            CarModel model = new CarModel();
            model.setCarModelCode(carModel.getCode());
            model.setCarModelName(carModel.getCodeName());
            fetchBikesResponse.setModel(model);
            fetchBikesResponse.setYears(bike.getYears());
            fetchBikesResponse.setVimNum(bike.getVimNum());
            fetchBikesResponse.setBikeId(bike.getBikeId());
            fetchBikesResponses.add(fetchBikesResponse);
        }
        response.put("bikes", fetchBikesResponses);
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchBikeDetail(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        FetchBikeDetailRequest fetchBikeDetailRequest = map(param, FetchBikeDetailRequest.class);
        fetchBikeDetailRequest.checkValidation();
        Bikes bike = bikesRepository.findByBikeId(fetchBikeDetailRequest.getBikeId());
        Leases leases = leaseRepository.findByBikeNo(bike.getBikeNo());
        Clients clients = leases == null ? null : leases.getClients();
        FetchBikeDetailResponse fetchBikeDetailResponse = new FetchBikeDetailResponse();
        CommonCode carModel = bike.getCarModel();
        CarModel model = new CarModel();
        model.setCarModelCode(carModel.getCode());
        model.setCarModelName(carModel.getCodeName());
        fetchBikeDetailResponse.setYears(bike.getYears());
        fetchBikeDetailResponse.setModel(model);
        fetchBikeDetailResponse.setBikeId(bike.getBikeId());
        fetchBikeDetailResponse.setColor(bike.getColor());
        fetchBikeDetailResponse.setVimNum(bike.getVimNum() == null ? "" : bike.getVimNum());
        fetchBikeDetailResponse.setCarNum(bike.getCarNum());
        fetchBikeDetailResponse.setReceiveDt(bike.getReceiveDate());
        fetchBikeDetailResponse.setRegisterDt(bike.getRegisterDate());
        if(leases != null) {
            ClientDto client = new ClientDto();
            client.setClientName(clients.getClientInfo().getName());
            client.setClientId(clients.getClientId());
            LeasesDto lease = new LeasesDto();
            lease.setLeaseId(leases.getLeaseId());
            fetchBikeDetailResponse.setClient(client);
            fetchBikeDetailResponse.setLease(lease);
        }
        response.put("bike", fetchBikeDetailResponse);
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchBikesWithoutLease(BikeSessionRequest request){
        request = fetchBikes(request);
        Map response = (HashMap)request.getResponse();
        List<FetchBikesResponse> bikes = (List)response.get("bikes");
        for(int i = bikes.size()-1; i >= 0; i--){
            Bikes bike = bikesRepository.findByBikeId(bikes.get(i).getBikeId());
            if(bike.getLease()!= null)
                bikes.remove(i);
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest addBike(BikeSessionRequest request){
        Map param = request.getParam();
        AddBikeRequest addBikeRequest = map(param, AddBikeRequest.class);
        addBikeRequest.checkValidation();
        String bikeId = autoKey.makeGetKey("bike");
        Bikes bike = new Bikes();
        bike.setBikeId(bikeId);
        bike.setYears(addBikeRequest.getYears());
        bike.setVimNum(addBikeRequest.getVimNumber());
        bike.setCarNum(addBikeRequest.getNumber());
        bike.setCarModelCode(addBikeRequest.getCarModel());
        bike.setColor(addBikeRequest.getColor());
        bike.setReceiveDate(addBikeRequest.getReceiveDt());
        bikesRepository.save(bike);

        return request;
    }

    @Transactional
    public BikeSessionRequest updateBike(BikeSessionRequest request){
        Map param = request.getParam();
        UpdateBikeRequest updateBikeRequest = map(param, UpdateBikeRequest.class);
        Bikes bike = bikesRepository.findByBikeId(updateBikeRequest.getBikeId());
        bike.setYears(updateBikeRequest.getYears());
        bike.setVimNum(updateBikeRequest.getVimNumber());
        bike.setCarNum(updateBikeRequest.getNumber());
        bike.setCarModelCode(updateBikeRequest.getCarModel());
        bike.setColor(updateBikeRequest.getColor());
        bike.setReceiveDate(updateBikeRequest.getReceiveDt());
        bike.setRegisterDate(updateBikeRequest.getRegisterDt());
        bikesRepository.save(bike);

        return request;
    }

    @Transactional
    public BikeSessionRequest deleteBike(BikeSessionRequest request){
        Map param = request.getParam();
        DeleteBikeRequest deleteBikeRequest  = map(param, DeleteBikeRequest.class);
        deleteBikeRequest.checkValidation();
        Bikes bikes = bikesRepository.findByBikeId(deleteBikeRequest.getBikeId());
        if(bikes == null) withException("");
        if(bikes.getLease() != null) writeMessage("리스번호 " + bikes.getLease().getLeaseId() + "가 이미 연결되어 있습니다.");
        else{
            bikesRepository.delete(bikes);
        }
        return request;
    }

    public BikeSessionRequest fetchBikeModels(BikeSessionRequest request){
        Map response = new HashMap();
        List<CommonCodeBikes> commonCodeBikes = bikeModelsRepository.findAll();
        List<FetchBikeModelsResponse> fetchBikeModelsResponses = new ArrayList<>();
        for(CommonCodeBikes model: commonCodeBikes){
              FetchBikeModelsResponse fetchBikeModelsResponse = new FetchBikeModelsResponse();
              fetchBikeModelsResponse.setModel(model.getModel());
              fetchBikeModelsResponse.setCode(model.getCode());
              fetchBikeModelsResponses.add(fetchBikeModelsResponse);
        }
        response.put("model", fetchBikeModelsResponses);
        request.setResponse(response);
        return request;
    }


}
