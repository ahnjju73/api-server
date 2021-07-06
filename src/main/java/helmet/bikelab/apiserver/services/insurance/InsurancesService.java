package helmet.bikelab.apiserver.services.insurance;

import helmet.bikelab.apiserver.domain.CommonCodeInsurances;
import helmet.bikelab.apiserver.domain.lease.Insurances;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.InsuranceOptionDto;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.DeleteInsuranceRequest;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.FetchInsuranceRequest;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.FetchInsuranceResponse;
import helmet.bikelab.apiserver.repositories.InsuranceOptionlRepository;
import helmet.bikelab.apiserver.repositories.InsurancesRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
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
    private final InsuranceOptionlRepository insuranceOptionlRepository;
    private final LeaseRepository leaseRepository;


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
        insurancesRepository.save(insurance);

        return request;
    }

    @Transactional
    public BikeSessionRequest deleteInsurance(BikeSessionRequest request){
        Map param = request.getParam();
        DeleteInsuranceRequest deleteInsuranceRequest = map(param, DeleteInsuranceRequest.class);
        Insurances insurances = insurancesRepository.findByInsuranceId(deleteInsuranceRequest.getInsuranceId());
        if(leaseRepository.existsAllByInsuranceNoEquals(insurances.getInsuranceNo())) withException("");
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

}
