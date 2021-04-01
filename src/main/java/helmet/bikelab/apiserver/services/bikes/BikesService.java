package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.CommonCode;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.ClientInfo;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.CarModel;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.*;
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
    private final ClientInfoRepository clientInfoRepository;
    private final AutoKey autoKey;

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
            fetchBikesResponse.setVimNum(bike.getVimNum());
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
        Clients clients = leases==null?null : clientsRepository.findByClientNo(leases.getClientNo());//leases.getClient()
        FetchBikeDetailResponse fetchBikeDetailResponse = new FetchBikeDetailResponse();
        CommonCode carModel = bike.getCarModel();
        CarModel model = new CarModel();
        model.setCarModelCode(carModel.getCode());
        model.setCarModelName(carModel.getCodeName());
        fetchBikeDetailResponse.setModel(model);
        fetchBikeDetailResponse.setColor(bike.getColor());
        fetchBikeDetailResponse.setVimNum(bike.getVimNum());
        fetchBikeDetailResponse.setCarNum(bike.getCarNum());
        fetchBikeDetailResponse.setReceiveDt(bike.getReceiveDate());
        fetchBikeDetailResponse.setClientName(clients==null?"":clients.getClientInfo().getName());

        response.put("bike", fetchBikeDetailResponse);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addBike(BikeSessionRequest request){
        Map param = request.getParam();
        AddBikeRequest addBikeRequest = map(param, AddBikeRequest.class);
        String bikeId = autoKey.makeGetKey("bike");
        Bikes bike = new Bikes();
        bike.setBikeId(bikeId);
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

        return request;
    }


}
