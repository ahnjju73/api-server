package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.*;
import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.domain.types.UnitTypes;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.BikeModelsRepository;
import helmet.bikelab.apiserver.repositories.PartsCodesRepository;
import helmet.bikelab.apiserver.repositories.PartsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class BikePartsService extends SessService {
    private final CommonWorker commonWorker;
    private final PartsRepository partsRepository;

    private final BikeWorker bikeWorker;

    public BikeSessionRequest fetchPartsByID(BikeSessionRequest request){
        PartsByIdRequest partsByIdRequest = map(request.getParam(), PartsByIdRequest.class);
        Parts partsById = bikeWorker.getPartsById(partsByIdRequest.getPartsNo());
        request.setResponse(partsById);
        return request;
    }

    public BikeSessionRequest fetchParts(BikeSessionRequest request) {
        Map param = request.getParam();
        BikePartsDto bikePartsDto = map(param, BikePartsDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(bikePartsDto, "comm.parts.fetchAllParts", "comm.parts.countAllParts", "parts_no");
        request.setResponse(responseListDto);
        return request;
    }

    @Transactional
    public BikeSessionRequest deletePartsByIdAndCarModel(BikeSessionRequest request){
        return request;
    }

    @Transactional
    public BikeSessionRequest updatePartsByIdAndCarModel(BikeSessionRequest request){
        PartsUpdatedRequest partsUpdatedRequest = map(request.getParam(), PartsUpdatedRequest.class);
        partsUpdatedRequest.checkValidation();
        Parts partsByIdAndCarModel = bikeWorker.getPartsById(partsUpdatedRequest.getPartsNo());
        Boolean changed = false;
        PartsBackUpDto partsBackUpDto = map(partsByIdAndCarModel, PartsBackUpDto.class);

        if(!partsUpdatedRequest.getPartsPrices().equals(partsByIdAndCarModel.getPartsPrices())){
            partsByIdAndCarModel.setPartsPrices(partsUpdatedRequest.getPartsPrices());
            changed = true;
        }
        if(!partsUpdatedRequest.getWorkingPrices().equals(partsByIdAndCarModel.getWorkingPrices())){
            partsByIdAndCarModel.setWorkingPrices(partsUpdatedRequest.getWorkingPrices());
            changed = true;
        }
        if(!partsUpdatedRequest.getWorkingHours().equals(partsByIdAndCarModel.getWorkingHours())){
            partsByIdAndCarModel.setWorkingHours(partsUpdatedRequest.getWorkingHours());
            changed = true;
        }
        if(!partsUpdatedRequest.getUnits().equals(partsByIdAndCarModel.getUnits())){
            partsByIdAndCarModel.setUnits(partsUpdatedRequest.getUnits());
            changed = true;
        }
        if(changed){
            List<PartsBackUpDto> backUpList = partsByIdAndCarModel.getBackUpList();
            if(!bePresent(backUpList)) backUpList = new ArrayList<>();
            backUpList.add(partsBackUpDto);
            partsByIdAndCarModel.setBackUpList(backUpList);
            partsRepository.save(partsByIdAndCarModel);
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest addPartsByModel(BikeSessionRequest request) {
        Map param = request.getParam();
        PartsNewRequest bikePartsDto = map(param, PartsNewRequest.class);
        bikePartsDto.checkValidation();
        Parts parts = new Parts();
        CommonCodeBikes model = bikeWorker.getCommonCodeBikesById(bikePartsDto.getCarModel());
        PartsCodes partsCodes = bikeWorker.getPartsCodeById(bikePartsDto.getPartsCodeNo());
        parts.setBikeModelCode(model.getCode());
        parts.setPartsCodeNo(partsCodes.getPartsCodeNo());
        parts.setPartsPrices(bikePartsDto.getPartsPrices());
        parts.setWorkingHours(bikePartsDto.getWorkingHours());
        parts.setWorkingPrices(bikePartsDto.getWorkingPrices());
        parts.setUnits(bikePartsDto.getUnits());
        partsRepository.save(parts);
        return request;
    }

    @Transactional
    public BikeSessionRequest fetchPartsCodes(BikeSessionRequest request){
        List response = getList("comm.parts.fetchPartsCodes", request.getParam());
        request.setResponse(response);
        return request;
    }
}
