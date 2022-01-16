package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.CommonWorking;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.BikeWorkingDto;
import helmet.bikelab.apiserver.objects.UpdateBikeWorkingRequest;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.BikeModelByIdRequest;
import helmet.bikelab.apiserver.objects.requests.NewCarModelRequest;
import helmet.bikelab.apiserver.repositories.BikeModelsRepository;
import helmet.bikelab.apiserver.repositories.CommonWorkingRepository;
import helmet.bikelab.apiserver.repositories.ManufacturersRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.workers.BikeWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class BikeModelService extends SessService {

    private final BikeWorker bikeWorker;
    private final ManufacturersRepository manufacturersRepository;
    private final BikeModelsRepository bikeModelsRepository;
    private final AutoKey autoKey;
    private final CommonWorkingRepository commonWorkingRepository;

    @Transactional
    public BikeSessionRequest updateManuf(BikeSessionRequest request){
        Map param = request.getParam();
        Integer manufacturerNo = (Integer)param.get("manufacturer_no");
        String manufacturerName = (String)param.get("manufacturer");
        Manufacturers manufacturerById = bikeWorker.getManufacturerById(manufacturerNo);
        manufacturerById.setManufacturer(manufacturerName);
        manufacturersRepository.save(manufacturerById);
        request.setResponse(manufacturerById);
        return request;
    }

    @Transactional
    public BikeSessionRequest doSaveManuf(BikeSessionRequest request){
        Map param = request.getParam();
        String manufacturerName = (String)param.get("manufacturer");
        if(!bePresent(manufacturerName)) withException("504-001");
        Manufacturers manufacturers = new Manufacturers();
        manufacturers.setManufacturer(manufacturerName);
        manufacturersRepository.save(manufacturers);
        request.setResponse(manufacturers);
        return request;
    }

    @Transactional
    public BikeSessionRequest doSaveCarModel(BikeSessionRequest request){
        NewCarModelRequest carModelRequest = map(request.getParam(), NewCarModelRequest.class);
        Manufacturers manufacturerById = bikeWorker.getManufacturerById(carModelRequest.getManufacturerNo());
        String code = autoKey.makeGetKey("car_model");
        CommonBikes commonBikes = new CommonBikes();
        commonBikes.setCode(code);
        commonBikes.setManufacturerNo(manufacturerById.getManufacturerNo());
        commonBikes.setModel(carModelRequest.getModel());
        commonBikes.setVolume(carModelRequest.getVolume());
        commonBikes.setBikeType(carModelRequest.getBikeType());
        commonBikes.setDiscontinue(carModelRequest.getDiscontinue());
        bikeModelsRepository.save(commonBikes);
        request.setResponse(commonBikes);
        return request;
    }


    @Transactional
    public BikeSessionRequest updateCarModel(BikeSessionRequest request){
        Map param = request.getParam();
        String code = (String)param.get("code");
        NewCarModelRequest carModelRequest = map(request.getParam(), NewCarModelRequest.class);
        CommonBikes commonBikes = bikeWorker.getCommonCodeBikesById(code);
        commonBikes.setModel(carModelRequest.getModel());
        commonBikes.setVolume(carModelRequest.getVolume());
        commonBikes.setBikeType(carModelRequest.getBikeType());
        commonBikes.setDiscontinue(carModelRequest.getDiscontinue());
        bikeModelsRepository.save(commonBikes);
        request.setResponse(commonBikes);
        return request;
    }

    public BikeSessionRequest fetchManufacturerCodes(BikeSessionRequest request){
        List<Manufacturers> allBy = manufacturersRepository.findAllBy();
        request.setResponse(allBy);
        return request;
    }

    public BikeSessionRequest fetchCarModelByManufacturer(BikeSessionRequest request){
        Map param = request.getParam();
        Integer manufacturerNo = Integer.parseInt((String)param.get("manufacturer_no"));
        List<CommonBikes> byManufacturerNo = bikeModelsRepository.findByManufacturerNo(manufacturerNo);
        request.setResponse(byManufacturerNo);
        return request;
    }

    @Deprecated
    public BikeSessionRequest fetchModelManufacturer(BikeSessionRequest request){
        List<Manufacturers> response = bikeWorker.getManufacturers();
        request.setResponse(response);
        return request;
    }

    @Deprecated
    public BikeSessionRequest fetchModelManufacturerByCode(BikeSessionRequest request){
        BikeModelByIdRequest bikeModelByIdRequest = map(request.getParam(), BikeModelByIdRequest.class);
        CommonBikes commonCodeBikesById = bikeWorker.getCommonCodeBikesById(bikeModelByIdRequest.getCode());
        request.setResponse(commonCodeBikesById);
        return request;
    }

    public BikeSessionRequest fetchWorkingByModel(BikeSessionRequest request){
        Map response = new HashMap();
        response.put("gas", commonWorkingRepository.findByBikeTypeOrderByVolumeAsc(BikeTypes.GAS));
        response.put("electronic", commonWorkingRepository.findByBikeTypeOrderByVolumeAsc(BikeTypes.ELECTRONIC));
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateWorkingByModel(BikeSessionRequest request) {
        commonWorkingRepository.deleteAll();
        Map param = request.getParam();
        UpdateBikeWorkingRequest updateBikeWorkingRequest = map(param, UpdateBikeWorkingRequest.class);
        List<BikeWorkingDto> gas = updateBikeWorkingRequest.getGas();
        List<BikeWorkingDto> electronic = updateBikeWorkingRequest.getElectronic();
        for(BikeWorkingDto bw : gas){
            long count = gas.stream().filter(o -> o.getVolume().equals(bw.getVolume())).count();
            if(count > 1){
                writeMessage(bw.getVolume() + " CC가 중복됩니다. 확인 후 다시 시도해 주세요.");

            }
            CommonWorking commonWorking = new CommonWorking();
            commonWorking.setWorkingPrice(bw.getWorkingPrice());
            commonWorking.setVolume(bw.getVolume());
            commonWorking.setBikeType(BikeTypes.GAS);
            commonWorking.setBikeTypeCode(BikeTypes.GAS.getType());
            commonWorkingRepository.save(commonWorking);
        }
        for(BikeWorkingDto bw : electronic){
            long count = Stream.of(electronic)
                    .filter(e -> e.equals(bw))
                    .count();
            if(count > 1){
                writeMessage(bw.getVolume() + " KW가 중복됩니다. 확인 후 다시 시도해 주세요.");

            }
            CommonWorking commonWorking = new CommonWorking();
            commonWorking.setWorkingPrice(bw.getWorkingPrice());
            commonWorking.setVolume(bw.getVolume());
            commonWorking.setBikeType(BikeTypes.ELECTRONIC);
            commonWorking.setBikeTypeCode(BikeTypes.ELECTRONIC.getType());
            commonWorkingRepository.save(commonWorking);
        }
        return request;
    }
}
