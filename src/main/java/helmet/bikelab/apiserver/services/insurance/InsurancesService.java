package helmet.bikelab.apiserver.services.insurance;

import helmet.bikelab.apiserver.domain.CommonCodeInsurances;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.lease.Insurances;
import helmet.bikelab.apiserver.domain.riders.RiderInsurances;
import helmet.bikelab.apiserver.domain.riders.RiderInsurancesDtl;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.RiderInsuranceStatus;
import helmet.bikelab.apiserver.objects.BankInfoDto;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.InsuranceOptionDto;
import helmet.bikelab.apiserver.objects.RiderInfoDto;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.DeleteInsuranceRequest;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.FetchInsuranceRequest;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.FetchInsuranceResponse;
import helmet.bikelab.apiserver.objects.requests.AddUpdateRiderInsuranceRequest;
import helmet.bikelab.apiserver.objects.requests.FetchRiderInsuranceRequest;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.RiderWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InsurancesService extends SessService {

    private final InsurancesRepository insurancesRepository;
    private final AutoKey autoKey;
    private final RiderWorker riderWorker;
    private final BikeWorker bikeWorker;
    private final InsuranceOptionlRepository insuranceOptionlRepository;
    private final LeaseRepository leaseRepository;
    private final CommonWorker commonWorker;
    private final RiderInsuranceRepository riderInsuranceRepository;
    private final RiderInsuranceDtlRepository riderInsuranceDtlRepository;


    public BikeSessionRequest fetchInsurances(BikeSessionRequest request){
        Map response = new HashMap();
        List<Insurances> insurancesList = insurancesRepository.findAll();
        response.put("insurances", insurancesList);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addInsurance(BikeSessionRequest request){
        Map param = request.getParam();
        Insurances insurance = map(param, Insurances.class);
        insurance.checkValidation();
        String insuranceId = autoKey.makeGetKey("insurance");
        insurance.setInsuranceId(insuranceId);
        insurancesRepository.save(insurance);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateInsurance(BikeSessionRequest request){
        Map param = request.getParam();
        Insurances newInsurance = map(param, Insurances.class);
        newInsurance.checkValidation();
        Insurances insurance = insurancesRepository.findByInsuranceId(newInsurance.getInsuranceId());
        insurance.setInsuranceTypeCode(newInsurance.getInsuranceTypeCode());
        insurance.setCompanyName(newInsurance.getCompanyName());
        insurance.setAge(newInsurance.getAge());
        insurance.setBmCare(newInsurance.getBmCare());
        insurance.setLiabilityCar(newInsurance.getLiabilityCar());
        insurance.setLiabilityMan(newInsurance.getLiabilityMan());
        insurance.setSelfCoverCar(newInsurance.getSelfCoverCar());
        insurance.setSelfCoverMan(newInsurance.getSelfCoverMan());
        insurance.setLiabilityMan2(newInsurance.getLiabilityMan2());
        insurance.setNoInsuranceCover(newInsurance.getNoInsuranceCover());
        insurance.setType(newInsurance.getType());
        insurance.setInsuranceName(newInsurance.getInsuranceName());
        insurancesRepository.save(insurance);

        return request;
    }

    @Transactional
    public BikeSessionRequest deleteInsurance(BikeSessionRequest request){
        Map param = request.getParam();
        DeleteInsuranceRequest deleteInsuranceRequest = map(param, DeleteInsuranceRequest.class);
        Insurances insurances = insurancesRepository.findByInsuranceId(deleteInsuranceRequest.getInsuranceId());
        if(leaseRepository.existsAllByInsuranceNoEquals(insurances.getInsuranceNo()))
            writeMessage("사용중인 보험입니다 삭제할 수 없습니다.");
        insurancesRepository.delete(insurances);
        return request;
    }

    public BikeSessionRequest fetchInsuranceOption(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        List<CommonCodeInsurances> insuranceOptions = insuranceOptionlRepository.findAll();
        List<FetchInsuranceResponse> fetchInsuranceResponses = new ArrayList<>();
        for(CommonCodeInsurances insurance : insuranceOptions){
            if(insurance.getUpperCode()==null){
                FetchInsuranceResponse fetchInsuranceResponse = new FetchInsuranceResponse();
                fetchInsuranceResponse.setUpCode(insurance.getCode());
                fetchInsuranceResponse.setUpCodeName(insurance.getName());
                fetchInsuranceResponse.setList(new ArrayList<>());
                fetchInsuranceResponses.add(fetchInsuranceResponse);
            }else{
                for(int i=0; i<fetchInsuranceResponses.size();i++){
                    if(fetchInsuranceResponses.get(i).getUpCode().equals(insurance.getUpperCode())){
                        InsuranceOptionDto optionDto = new InsuranceOptionDto();
                        optionDto.setValue(insurance.getName());
                        optionDto.setComCode(insurance.getCode());
                        fetchInsuranceResponses.get(i).getList().add(optionDto);
                    }
                }
            }
        }
        response.put("insurances", fetchInsuranceResponses);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addRiderInsurance(BikeSessionRequest request){
        AddUpdateRiderInsuranceRequest addUpdateRiderInsuranceRequest = map(request.getParam(), AddUpdateRiderInsuranceRequest.class);
        String riderInsId = autoKey.makeGetKey("rider_ins");
        RiderInsurances riderInsurances = new RiderInsurances();
        riderInsurances.setRiderInsId(riderInsId);
        Riders rider = null;
        if(bePresent(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderId())) {
            rider = riderWorker.getRiderById(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderId());
            riderInsurances.setRiderNo(rider.getRiderNo());
        }
        Bikes bike = bikeWorker.getBikeById(addUpdateRiderInsuranceRequest.getBikeId());
        riderInsurances.setBikeNum(bike.getCarNum());
        riderInsurances.setVimNum(bike.getVimNum());
        riderInsuranceRepository.save(riderInsurances);

        RiderInsurancesDtl insurancesDtl = new RiderInsurancesDtl();
        insurancesDtl.setRiderInsNo(riderInsurances.getRiderInsNo());
        RiderInfoDto riderInfoDto = new RiderInfoDto();
        if(bePresent(rider)) {
            riderInfoDto.setRiderId(rider.getRiderId());
            riderInfoDto.setRiderStatus(rider.getStatus().getRiderStatusType());
            riderInfoDto.setRiderEmail(rider.getEmail());
            riderInfoDto.setRiderPhone(rider.getPhone());
            riderInfoDto.setRiderName(rider.getRiderInfo().getName());
        }else{
            riderInfoDto.setRiderEmail(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderEmail());
            riderInfoDto.setRiderPhone(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderPhone());
            riderInfoDto.setRiderName(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderName());
        }
        insurancesDtl.setRiderInfoDto(riderInfoDto);
        insurancesDtl.setRiderInsuranceStatus(RiderInsuranceStatus.PENEDING);
        if(bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto()))
            insurancesDtl.setBankInfo(addUpdateRiderInsuranceRequest.getBankInfoDto());
        insurancesDtl.setUsage(addUpdateRiderInsuranceRequest.getUsage());
        insurancesDtl.setAdditionalStandard(addUpdateRiderInsuranceRequest.getAdditionalStandard());
        riderInsuranceDtlRepository.save(insurancesDtl);
        return request;
    }

    public BikeSessionRequest fetchRiderInsurances(BikeSessionRequest request){
        FetchRiderInsuranceRequest fetchRiderInsuranceRequest = map(request.getParam(), FetchRiderInsuranceRequest.class);
        Pageable pageable = PageRequest.of(fetchRiderInsuranceRequest.getPage(), fetchRiderInsuranceRequest.getSize());
        if(bePresent(fetchRiderInsuranceRequest.getRiderName())){
            Page<RiderInsurances> allByRiderInsurancesDtl_riderInfoDto_riderNameContaining = riderInsuranceRepository.findAllByRiderInsurancesDtl_RiderInfoDto_RiderNameContaining(fetchRiderInsuranceRequest.getRiderName(), pageable);
            request.setResponse(allByRiderInsurancesDtl_riderInfoDto_riderNameContaining);
        }else{
            Page<RiderInsurances> allOrderByRiderInsNoDesc = riderInsuranceRepository.findAllOrderByRiderInsNoDesc(pageable);
            request.setResponse(allOrderByRiderInsNoDesc);
        }
        return request;
    }

}
