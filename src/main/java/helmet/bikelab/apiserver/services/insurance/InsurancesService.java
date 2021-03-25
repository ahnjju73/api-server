package helmet.bikelab.apiserver.services.insurance;

import helmet.bikelab.apiserver.domain.lease.Insurances;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.FetchInsurancesResponse;
import helmet.bikelab.apiserver.repositories.InsurancesRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InsurancesService extends SessService {
    private final InsurancesRepository insurancesRepository;

//    public BikeSessionRequest fetchInsurances(BikeSessionRequest request){
//        Map response = new HashMap();
//        List<Insurances> insurancesList = insurancesRepository.findAll();
//        List<FetchInsurancesResponse> fetchInsurancesResponses = new ArrayList<>();
//        for(Insurances insurances : insurancesList){
//            FetchInsurancesResponse fetchInsurancesResponse = new FetchInsurancesResponse();
//            fetchInsurancesResponse.setInsuranceFee(insurances.getInsuranceFee());
//            fetchInsurancesResponse.setInsuranceAge(insurances.getAge());
//            fetchInsurancesResponse.setCompanyName(insurances.getCompanyName());
//            fetchInsurancesResponse.setBmCare(insurances.getBmCare());
//            fetchInsurancesResponse.setSecurity(insurances.getSecurityType());
//            fetchInsurancesResponses.add(fetchInsurancesResponse);
//        }
//        response.put("insurances", fetchInsurancesResponses);
//        request.setResponse(response);
//
//        return request;
//    }

}
