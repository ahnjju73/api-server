package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.repositories.BikeModelsRepository;
import helmet.bikelab.apiserver.repositories.ManufacturersRepository;
import helmet.bikelab.apiserver.repositories.PartsCodesRepository;
import helmet.bikelab.apiserver.repositories.PartsRepository;
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

    public List<Manufacturers> getManufacturers(){
        return manufacturersRepository.findAllBy();
    }

    public Manufacturers getManufacturerById(Integer manufacturerNo){
        Manufacturers byManufacturerNo = manufacturersRepository.findByManufacturerNo(manufacturerNo);
        if(!bePresent(byManufacturerNo)) withException("501-002");
        return byManufacturerNo;
    }

    public CommonCodeBikes getCommonCodeBikesById(String code){
        CommonCodeBikes byCode = bikeModelsRepository.findByCode(code);
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

