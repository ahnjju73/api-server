package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommonWorker extends SessService {

    public PresignedURLVo generatePreSignedUrl(String filename, String extension){
        PresignedURLVo presignedURLVo = new PresignedURLVo();
        presignedURLVo.setBucket(ENV.AWS_S3_QUEUE_BUCKET);
        if(bePresent(extension)){
            presignedURLVo.setFileKey(LocalDate.now() + "/" + filename + "." + extension);
        }else {
            presignedURLVo.setFileKey(LocalDate.now() + "/" + filename);
        }

        presignedURLVo.setFilename(filename + "." + extension);
        presignedURLVo.setUrl(AmazonUtils.AWSGeneratePresignedURL(presignedURLVo));
        return presignedURLVo;
    }

    public <T extends RequestListDto> ResponseListDto fetchItemListByNextToken(T requestListDto, String listPath, String countPath, String id){
        ResponseListDto responseListDto = new ResponseListDto();
        Map param = map(requestListDto, HashMap.class);
        param.put("now", LocalDate.now());
        if(bePresent(requestListDto) && ENV.LIST_COUNT_DONE.equals(requestListDto.getNextToken())) {
            responseListDto.setNextToken(ENV.LIST_COUNT_DONE);
        }else {
            List<Map> items = getList(listPath, param);
            if(!bePresent(items)){
                responseListDto.setNextToken(ENV.LIST_COUNT_DONE);
            }else {
                String nextToken = String.valueOf(items.get(items.size() - 1).get(id));
                responseListDto.setNextToken(nextToken);
                responseListDto.setItems(items);
            }
        }
        Integer countAll = (Integer)getItem(countPath, param);
        responseListDto.setTotal(countAll);
        return responseListDto;
    }
}
