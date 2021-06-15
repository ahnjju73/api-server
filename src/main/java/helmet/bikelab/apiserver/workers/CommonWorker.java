package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.keys.ENV;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommonWorker extends SessService {

    public <T extends RequestListDto> ResponseListDto fetchItemListByNextToken(T requestListDto, String listPath, String countPath, String id){
        ResponseListDto responseListDto = new ResponseListDto();
        Map param = map(requestListDto, HashMap.class);
        if(bePresent(requestListDto) && ENV.LIST_COUNT_DONE.equals(requestListDto.getNextToken())) {
            responseListDto.setNextToken(ENV.LIST_COUNT_DONE);
        }else {
            List<Map> items = getList(listPath, param);
            if(!bePresent(items)){
                responseListDto.setNextToken(ENV.LIST_COUNT_DONE);
            }else {

                String nextToken = (String)items.get(items.size() - 1).get(id);
                responseListDto.setNextToken(nextToken);
                responseListDto.setItems(items);
            }
        }
        Integer countAll = (Integer)getItem(countPath, param);
        responseListDto.setTotal(countAll);
        return responseListDto;
    }
}