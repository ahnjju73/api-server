package helmet.bikelab.apiserver.services;

import helmet.bikelab.apiserver.domain.bikelab.BikeUserTodo;
import helmet.bikelab.apiserver.domain.types.BikeUserTodoTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.todo.BikeUserTodoDto;
import helmet.bikelab.apiserver.repositories.BikeUserTodoRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BikeUserTodoService extends SessService {

    private final BikeUserTodoRepository bikeUserTodoRepository;

    public void addTodo(BikeUserTodoTypes bikeUserTodoTypes, Integer fromUserNo, Integer toUserNo, String referenceId){
        bikeUserTodoRepository.deleteAllByTodoTypesAndReferenceId(bikeUserTodoTypes, referenceId);
        BikeUserTodo bikeUserTodo = new BikeUserTodo();
        bikeUserTodo.setFromUserNo(fromUserNo);
        bikeUserTodo.setToUserNo(toUserNo);
        bikeUserTodo.setTodoTypes(bikeUserTodoTypes);
        bikeUserTodo.setReferenceId(referenceId);
        bikeUserTodoRepository.save(bikeUserTodo);
    }

    public BikeSessionRequest fetchTodoSummery(BikeSessionRequest request){
        Map param = request.getParam();
        BikeUserTodoDto bikeUserTodoDto = new BikeUserTodoDto();

        List<Map> todo = getList("bikelabs.todo.fetchTodoSummery", param);

        if(bePresent(todo)){
            Long refreshToken = (Long)todo.get(0).get("todo_no");
            bikeUserTodoDto.setTodo(todo);
            bikeUserTodoDto.setRefreshToken(refreshToken);
        }

        request.setResponse(bikeUserTodoDto);

        return request;
    }

    public BikeSessionRequest getReferenceIdFromNo(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        String referenceNo = (String)param.get("ref_id");
        BikeUserTodoTypes bikeUserTodoTypes = BikeUserTodoTypes.getBikeUserTodoTypes((String)param.get("todo_type"));
        if(BikeUserTodoTypes.LEASE_APPROVAL.equals(bikeUserTodoTypes)){
            response.put("id", getItem("bikelabs.todo.getLeaseIdFromLeaseNo", param));
        }
        request.setResponse(response);
        return request;
    }

}
