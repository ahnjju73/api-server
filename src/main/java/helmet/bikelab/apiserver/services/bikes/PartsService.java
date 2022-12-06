package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.domain.bike.PartsTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.requests.*;
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
        String partsNameEng = (String)param.get("parts_name_eng");
        Boolean usable = (Boolean)param.get("usable");
        PartsTypes byPartsTypeNo = partsTypesRepository.findByPartsTypeNo(partsTypeNo);
        if(bePresent(byPartsTypeNo)){
            PartsCodes partsCodes = new PartsCodes();
            partsCodes.setPartsTypeNo(byPartsTypeNo.getPartsTypeNo());
            partsCodes.setPartsName(partsName);
            partsCodes.setUsable(usable);
            partsCodes.setPartsNameEng(partsNameEng);
            partsCodesRepository.save(partsCodes);
        }else withException("");
        return request;
    }

    @Transactional
    public BikeSessionRequest deletePartsCode(BikeSessionRequest request){
        PartsCodeByIdRequest partsCodeByIdRequest = map(request.getParam(), PartsCodeByIdRequest.class);
        PartsCodes partsCode = partsCodesRepository.findByPartsCodeNo(partsCodeByIdRequest.getPartsCodeNo());
        if(!bePresent(partsCode)) writeMessage("존재하지않는 부품입니다.");
        try{
            partsCodesRepository.deleteByPartsCodeNo(partsCode.getPartsCodeNo());
        }catch (Exception e){
            writeMessage("이미 사용중인 부품이라 삭제가 불가능 합니다. 삭제대신 사요여부를 '미사용'으로 해주세요.");
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest updatePartsCode(BikeSessionRequest request){
        Map param = request.getParam();
        Integer partsCodeNo = (Integer)param.get("parts_code_no");
        Map partsType = (Map)param.get("parts_type_no");
        Integer partsTypeNo = (Integer)partsType.get("parts_type_no");
        String partsName = (String)param.get("parts_name");
        String partsNameEng = (String)param.get("parts_name_eng");
        Boolean usable = (Boolean)param.get("usable");
        PartsTypes byPartsTypeNo = partsTypesRepository.findByPartsTypeNo(partsTypeNo);
        if(bePresent(byPartsTypeNo)){
            PartsCodes byPartsCodeNo = partsCodesRepository.findByPartsCodeNo(partsCodeNo);
            if(bePresent(byPartsCodeNo)){
                byPartsCodeNo.setPartsTypeNo(byPartsTypeNo.getPartsTypeNo());
                byPartsCodeNo.setPartsName(partsName);
                byPartsCodeNo.setPartsNameEng(partsNameEng);
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

    @Transactional
    public BikeSessionRequest moveParsCodeToAnotherType(BikeSessionRequest request){
        MovePartsRequest movePartsRequest = map(request.getParam(), MovePartsRequest.class);
        PartsTypes byPartsTypeNo = partsTypesRepository.findByPartsTypeNo(movePartsRequest.getPartsTypeNo());
        if(bePresent(byPartsTypeNo) && bePresent(movePartsRequest.getCodeList())){
            movePartsRequest.getCodeList().forEach(elm -> {
                partsCodesRepository.moveParsCodeToAnotherType(elm, byPartsTypeNo.getPartsTypeNo());
            });
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest deletePartsType(BikeSessionRequest request){
        PartsTypeByIdRequest partsTypeByIdRequest = map(request.getParam(), PartsTypeByIdRequest.class);
        PartsTypes byPartsTypeNo = partsTypesRepository.findByPartsTypeNo(partsTypeByIdRequest.getPartsTypeNo());
        if(!bePresent(byPartsTypeNo)) writeMessage("존재하지 않는 계통정보입니다.");
        List<PartsCodes> partsCodes = partsCodesRepository.findByPartsTypeNo(byPartsTypeNo.getPartsTypeNo());
        if(bePresent(partsCodes)) writeMessage("등록된 계통별 부품이 존재합니다. 부품들을 먼저 이동시켜주세요.");
        partsTypesRepository.deleteByPartsTypeNo(byPartsTypeNo.getPartsTypeNo());
        return request;
    }

}
