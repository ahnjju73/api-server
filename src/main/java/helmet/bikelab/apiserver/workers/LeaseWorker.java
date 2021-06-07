package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.keys.ENV;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LeaseWorker extends SessService {

    public ResponseListDto fetchLeases(RequestListDto requestListDto){
        ResponseListDto responseListDto = new ResponseListDto();
        if(bePresent(requestListDto) && ENV.LIST_COUNT_DONE.equals(requestListDto.getNextToken())) {
            responseListDto.setNextToken(ENV.LIST_COUNT_DONE);
        }else {

            List<Map> items = getList("leases.leases-manager.fetchLeases", requestListDto);
            if(!bePresent(items)){
                responseListDto.setNextToken(ENV.LIST_COUNT_DONE);
            }else {

                String nextToken = (String)items.get(items.size() - 1).get("lease_id");
                responseListDto.setNextToken(nextToken);
                responseListDto.setItems(items);
            }
        }
        Integer countAll = (Integer)getItem("leases.leases-manager.countAllLeases", null);
        responseListDto.setTotal(countAll);
        return responseListDto;
    }
}
