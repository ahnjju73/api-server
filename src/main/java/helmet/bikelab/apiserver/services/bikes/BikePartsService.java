package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.domain.types.UnitTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.BikePartsDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.BikeModelsRepository;
import helmet.bikelab.apiserver.repositories.PartsCodesRepository;
import helmet.bikelab.apiserver.repositories.PartsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class BikePartsService extends SessService {
    private final CommonWorker commonWorker;
    private final BikeModelsRepository modelsRepository;
    private final PartsCodesRepository partsCodesRepository;
    private final PartsRepository partsRepository;

    public BikeSessionRequest fetchParts(BikeSessionRequest request) {
        Map param = request.getParam();
        BikePartsDto bikePartsDto = map(param, BikePartsDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(bikePartsDto, "comm.parts.fetchAllParts", "comm.parts.countAllParts", "parts_no");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest addPartsByModel(BikeSessionRequest request) {
        Map param = request.getParam();
        BikePartsDto bikePartsDto = map(param, BikePartsDto.class);
        Parts parts = new Parts();
        CommonCodeBikes model = modelsRepository.findByCode(bikePartsDto.getCarModel());
        PartsCodes partsCodes = partsCodesRepository.findById(bikePartsDto.getPartsCodeNo()).get();
        if(!bePresent(partsCodes)) withException("");
        if(!bePresent(model)) withException("");
        parts.setBikeModelCode(model.getCode());
        parts.setPartsCodeNo(bikePartsDto.getPartsCodeNo());
        parts.setPartsPrices(bikePartsDto.getPartsPrice());
        parts.setWorkingHours(bikePartsDto.getWorkingHours());
        parts.setWorkingPrices(bikePartsDto.getWorkingPrices());
        parts.setUnits(UnitTypes.getUnitTypes(bikePartsDto.getUnits()));
        partsRepository.save(parts);
        return request;
    }
}
