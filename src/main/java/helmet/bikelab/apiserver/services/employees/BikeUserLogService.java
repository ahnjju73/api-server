package helmet.bikelab.apiserver.services.employees;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeaseBikeUserLogs;
import helmet.bikelab.apiserver.services.internal.SessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BikeUserLogService extends SessService {

    public BikeSessionRequest fetchBikeUserLogInLeaseContract(BikeSessionRequest request){
        Map param = request.getParam();
        LeaseBikeUserLogs bikeUserLogs = new LeaseBikeUserLogs();
        String nextToken = "";
        if("D".equals(nextToken)) {
            bikeUserLogs.setNextToken("D");
        }else {
            List<Map> histories = getList("bikelabs.bike_user_log.getBikeUserLogInLeases", param);
            if(bePresent(histories)){
                nextToken = ((Long)histories.get(histories.size() - 1).get("log_no")).toString();
            }else {
                nextToken = "D";
            }
            bikeUserLogs.setNextToken(nextToken);
            bikeUserLogs.setHistories(histories);
        }
        request.setResponse(bikeUserLogs);
        return request;
    }
}
