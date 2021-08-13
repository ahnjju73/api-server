package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.repositories.BikeModelsRepository;
import helmet.bikelab.apiserver.repositories.ManufacturersRepository;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BikeWorker extends Workspace {

    private final ManufacturersRepository manufacturersRepository;
    private final BikeModelsRepository bikeModelsRepository;

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



}

