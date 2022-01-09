package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BikeWorker extends Workspace {

    private final ManufacturersRepository manufacturersRepository;
    private final BikeModelsRepository bikeModelsRepository;
    private final PartsCodesRepository partsCodesRepository;
    private final PartsRepository partsRepository;
    private final BikesRepository bikesRepository;

    public List<Manufacturers> getManufacturers(){
        return manufacturersRepository.findAllBy();
    }

    public Bikes getEmptyBikes(){
        String emptyBikeId = (String)getItem("comm.common.getEmptyCar", null);
        Bikes byBikeId = bikesRepository.findByBikeId(emptyBikeId);
        return byBikeId;
    }

    public Manufacturers getManufacturerById(Integer manufacturerNo){
        Manufacturers byManufacturerNo = manufacturersRepository.findByManufacturerNo(manufacturerNo);
        if(!bePresent(byManufacturerNo)) withException("501-002");
        return byManufacturerNo;
    }

    public CommonBikes getCommonCodeBikesById(String code){
        CommonBikes byCode = bikeModelsRepository.findByCode(code);
        if(!bePresent(byCode)) withException("501-001");
        return byCode;
    }

    public PartsCodes getPartsCodeById(Integer partsCodeNo){
        PartsCodes byPartsCodeNo = partsCodesRepository.findByPartsCodeNo(partsCodeNo);
        if(!bePresent(byPartsCodeNo)) withException("503-001");
        return byPartsCodeNo;
    }

    public Parts getPartsByIdAndCarModel(Long partsNo, String carModelCode){
        Parts byPartNoAndBikeModelCode = partsRepository.findByPartNoAndBikeModelCode(partsNo, carModelCode);
        if(!bePresent(byPartNoAndBikeModelCode)) withException("503-007");
        return byPartNoAndBikeModelCode;
    }

    public Parts getPartsById(Long partsNo){
        Parts byPartNoAndBikeModelCode = partsRepository.findByPartNo(partsNo);
        if(!bePresent(byPartNoAndBikeModelCode)) withException("503-007");
        return byPartNoAndBikeModelCode;
    }

    public Parts getPartsByPartsId(String partsId){
        Parts byPartNoAndBikeModelCode = partsRepository.findByPartsId(partsId);
        if(!bePresent(byPartNoAndBikeModelCode)) withException("503-007");
        return byPartNoAndBikeModelCode;
    }

}

