package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.CommonWorking;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.domain.bike.BikeInsurances;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.objects.responses.BikeInsuranceListResponse;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BikeWorker extends Workspace {

    private final ManufacturersRepository manufacturersRepository;
    private final BikeModelsRepository bikeModelsRepository;
    private final PartsCodesRepository partsCodesRepository;
    private final PartsRepository partsRepository;
    private final BikesRepository bikesRepository;
    private final CommonWorkingRepository commonWorkingRepository;
    private final BikeInsurancesRepository bikeInsurancesRepository;

    public List<Manufacturers> getManufacturers(){
        return manufacturersRepository.findAllBy();
    }

    public BikeInsuranceListResponse getBikeInsuranceListByBikeId(Bikes bikeById) {
        Map param = new HashMap();
        param.put("bike_id", bikeById.getBikeId());
        List list = getList("bikelabs.insurance.getBikeInsuranceListByBikeId", param);
        BikeInsuranceListResponse bikeInsuranceListResponse = new BikeInsuranceListResponse(list, bikeById.getBikeInsuranceNo());
        if(bePresent(bikeInsuranceListResponse.getBikeInsuranceNo())){
            BikeInsurances bikeInsurance = bikeById.getBikeInsurance();
            bikeInsuranceListResponse.setInsuranceId(bikeInsurance.getInsuranceId());
        }
        return bikeInsuranceListResponse;
    }

    public Bikes getBikeByNo(Integer bikeNo){
        Bikes byBikeNo = bikesRepository.findByBikeNo(bikeNo);
        if(!bePresent(byBikeNo)) withException("512-001");
        return byBikeNo;
    }

    public Bikes getBikeById(String bikeId){
        Bikes bike = bikesRepository.findByBikeId(bikeId);
        if(!bePresent(bike)) withException("512-001");
        return bike;
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

    public Parts getPartsByPartsIdAndCarModel(String partsId, String carModelCode){
        Parts byPartNoAndBikeModelCode = partsRepository.findByPartsIdAndBikeModelCode(partsId, carModelCode);
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

    public Integer getWorkingPrice(CommonBikes carModel){
        Integer workingPrice = 0;
        List<CommonWorking> byBikeTypeOrderByVolumeAsc = commonWorkingRepository.findByBikeTypeOrderByVolumeAsc(carModel.getBikeType());
        for(int i = 0; i < byBikeTypeOrderByVolumeAsc.size(); i++){
            CommonWorking commonWorking = byBikeTypeOrderByVolumeAsc.get(i);
            if(carModel.getVolume() >= commonWorking.getVolume()){
                workingPrice = commonWorking.getWorkingPrice();
            }else{
                break;
            }
        }
        return workingPrice;
    }

    public BikeInsurances getBikeInsurancesByNo(Integer insuranceNo){
        BikeInsurances byInsuranceNo = bikeInsurancesRepository.findByInsuranceNo(insuranceNo);
        if(!bePresent(byInsuranceNo)) writeMessage("존재하지않습니다.");
        return byInsuranceNo;
    }

}

