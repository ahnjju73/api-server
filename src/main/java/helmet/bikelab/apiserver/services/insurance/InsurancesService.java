package helmet.bikelab.apiserver.services.insurance;

import helmet.bikelab.apiserver.domain.lease.Insurances;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.DeleteInsuranceRequest;
import helmet.bikelab.apiserver.repositories.InsurancesRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InsurancesService extends SessService {

    private final InsurancesRepository insurancesRepository;
    private final AutoKey autoKey;

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
        String insuranceId = autoKey.makeGetKey("insurance");
        insurance.setInsuranceId(insuranceId);
        insurancesRepository.save(insurance);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateInsurance(BikeSessionRequest request){
        Map param = request.getParam();
        Insurances newInsurance = map(param, Insurances.class);
        Insurances insurance = insurancesRepository.findByInsuranceId(newInsurance.getInsuranceId());

        insurance.setCompanyName(newInsurance.getCompanyName());
        insurance.setInsuranceFee(newInsurance.getInsuranceFee());
        insurance.setAge(newInsurance.getAge());
        insurance.setBmCare(newInsurance.getBmCare());
        insurance.setLiabilityCar(newInsurance.getLiabilityCar());
        insurance.setLiabilityMan(newInsurance.getLiabilityMan());
        insurance.setSelfCoverCar(newInsurance.getSelfCoverCar());
        insurance.setSelfCoverMan(newInsurance.getSelfCoverMan());
        insurancesRepository.save(insurance);

        return request;
    }

    @Transactional
    public BikeSessionRequest deleteInsurance(BikeSessionRequest request){
        Map param = request.getParam();
        DeleteInsuranceRequest deleteInsuranceRequest = map(param, DeleteInsuranceRequest.class);
        Insurances insurances = insurancesRepository.findByInsuranceId(deleteInsuranceRequest.getInsuranceId());
        // todo 만약 lease에서 해당 insurance를 들고있는 경우 withException("")
        insurancesRepository.delete(insurances);

        return request;
    }

}
