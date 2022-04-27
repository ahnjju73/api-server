package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.domain.bike.PartsTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.requests.PageableRequest;
import helmet.bikelab.apiserver.objects.requests.PartsCodeListRequest;
import helmet.bikelab.apiserver.repositories.PartsCodesRepository;
import helmet.bikelab.apiserver.repositories.PartsTypesRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PartsService extends SessService {

    private final PartsTypesRepository partsTypesRepository;
    private final PartsCodesRepository partsCodesRepository;

    public BikeSessionRequest fetchParsCodeListByCondition(BikeSessionRequest request){
        PartsCodeListRequest pageableRequest = map(request.getParam(), PartsCodeListRequest.class);
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        Page<PartsCodes> partsCodesList = partsCodesRepository.findAllByPartsNameContainingAndPartsType_PartsTypeContaining(pageableRequest.getPartsName(), pageableRequest.getPartsType(), pageable);
        request.setResponse(partsCodesList);
        return request;
    }

    @Transactional
    public BikeSessionRequest doSavePartsCode(BikeSessionRequest request){
        Map param = request.getParam();
        Map partsType = (Map)param.get("parts_type_no");
        Integer partsTypeNo = (Integer)partsType.get("parts_type_no");
        String partsName = (String)param.get("parts_name");
        Boolean usable = (Boolean)param.get("usable");
        PartsTypes byPartsTypeNo = partsTypesRepository.findByPartsTypeNo(partsTypeNo);
        if(bePresent(byPartsTypeNo)){
            PartsCodes partsCodes = new PartsCodes();
            partsCodes.setPartsTypeNo(byPartsTypeNo.getPartsTypeNo());
            partsCodes.setPartsName(partsName);
            partsCodes.setUsable(usable);
            partsCodesRepository.save(partsCodes);
        }else withException("");
        return request;
    }

    @Transactional
    public BikeSessionRequest updatePartsCode(BikeSessionRequest request){
        Map param = request.getParam();
        Integer partsCodeNo = (Integer)param.get("parts_code_no");
        Map partsType = (Map)param.get("parts_type_no");
        Integer partsTypeNo = (Integer)partsType.get("parts_type_no");
        String partsName = (String)param.get("parts_name");
        Boolean usable = (Boolean)param.get("usable");
        PartsTypes byPartsTypeNo = partsTypesRepository.findByPartsTypeNo(partsTypeNo);
        if(bePresent(byPartsTypeNo)){
            PartsCodes byPartsCodeNo = partsCodesRepository.findByPartsCodeNo(partsCodeNo);
            if(bePresent(byPartsCodeNo)){
                byPartsCodeNo.setPartsTypeNo(byPartsTypeNo.getPartsTypeNo());
                byPartsCodeNo.setPartsName(partsName);
                byPartsCodeNo.setUsable(usable);
                partsCodesRepository.save(byPartsCodeNo);
            }
        }else withException("");
        return request;
    }

    @Transactional
    public BikeSessionRequest updatePartType(BikeSessionRequest request){
        Map param = request.getParam();
        Integer partsTypeNo = (Integer)param.get("parts_type_no");
        String partsType = (String)param.get("parts_type");
        Boolean usable = (Boolean)param.get("usable");
        YesNoTypes isFreeSupport = YesNoTypes.getYesNo((String)param.get("is_free_support_code"));
        PartsTypes byPartsTypeNo = partsTypesRepository.findByPartsTypeNo(partsTypeNo);
        if(bePresent(byPartsTypeNo)){
            byPartsTypeNo.setPartsType(partsType);
            byPartsTypeNo.setUsable(usable);
            byPartsTypeNo.setIsFreeSupport(isFreeSupport);
            partsTypesRepository.save(byPartsTypeNo);
            Map response = new HashMap();
            response.put("parts_type_no", byPartsTypeNo.getPartsTypeNo());
            request.setResponse(response);
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest doSavePartType(BikeSessionRequest request){
        Map param = request.getParam();
        String partsType = (String)param.get("parts_type");
        Boolean usable = (Boolean)param.get("usable");
        YesNoTypes isFreeSupport = YesNoTypes.getYesNo((String)param.get("is_free_support_code"));
        if(!bePresent(isFreeSupport)){
            isFreeSupport = YesNoTypes.YES;
        }
        if(!bePresent(usable)){
            usable = true;
        }

        PartsTypes top1ByOrderByOrderNoDesc = partsTypesRepository.findTop1ByOrderByOrderNoDesc();
        Integer orderNo = top1ByOrderByOrderNoDesc == null ? 0 : top1ByOrderByOrderNoDesc.getOrderNo() + 1;
        PartsTypes partsTypes = new PartsTypes();
        partsTypes.setOrderNo(orderNo);
        partsTypes.setPartsType(partsType);
        partsTypes.setUsable(usable);
        partsTypes.setIsFreeSupport(isFreeSupport);
        partsTypesRepository.save(partsTypes);
        Map response = new HashMap();
        response.put("parts_type_no", partsTypes.getPartsTypeNo());
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchPartsTypeList(BikeSessionRequest request){
        List<PartsTypes> allBy = partsTypesRepository.findAllBy();
        request.setResponse(allBy);
        return request;
    }

    public BikeSessionRequest fetchPartsCodeList(BikeSessionRequest request){
        Map param = request.getParam();
        String _partsTypeNo = (String)param.get("parts_type_no");
        Integer partsTypeNo = Integer.parseInt(_partsTypeNo);
        List<PartsCodes> byPartsTypeNo = partsCodesRepository.findByPartsTypeNo(partsTypeNo);
        request.setResponse(byPartsTypeNo);
        return request;
    }

}
