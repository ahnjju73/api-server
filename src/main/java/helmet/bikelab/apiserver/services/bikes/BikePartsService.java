package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.PartsBackUpDto;
import helmet.bikelab.apiserver.repositories.PartsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BikePartsService extends SessService {

    private final PartsRepository partsRepository;

    public BikeSessionRequest fetchParts(BikeSessionRequest request) {
        return request;
    }

    @Transactional
    public BikeSessionRequest fetchPartsCodes(BikeSessionRequest request){

        Parts byPartNo = partsRepository.findByPartNo(Long.parseLong("2"));

        List<PartsBackUpDto> backUpList = byPartNo.getBackUpList();

//        PartsBackUpDto partsBackUpDto = map(byPartNo, PartsBackUpDto.class);
//        List<PartsBackUpDto> backUpList = byPartNo.getBackUpList();
//
//        if(!bePresent(backUpList)) backUpList = new ArrayList<>();
//        backUpList.add(partsBackUpDto);
//        byPartNo.setBackUpList(backUpList);
//        partsRepository.save(byPartNo);
        request.setResponse(backUpList);
        return request;
    }
}
