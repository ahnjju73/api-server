package helmet.bikelab.apiserver.services;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserTodo;
import helmet.bikelab.apiserver.domain.types.BikeUserTodoTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.todo.BikeUserTodoDto;
import helmet.bikelab.apiserver.repositories.BikeUserTodoRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BikeUserTodoService extends SessService {

    private final BikeUserTodoRepository bikeUserTodoRepository;

    public void addTodo(BikeUserTodoTypes bikeUserTodoTypes, Integer fromUserNo, Integer toUserNo, String referenceId, String referenceUuid){
        bikeUserTodoRepository.deleteAllByReferenceId(referenceId);
        BikeUserTodo bikeUserTodo = new BikeUserTodo();
        bikeUserTodo.setFromUserNo(fromUserNo);
        bikeUserTodo.setToUserNo(toUserNo);
        bikeUserTodo.setTodoTypes(bikeUserTodoTypes);
        bikeUserTodo.setReferenceId(referenceId);
        bikeUserTodo.setReferenceUuid(referenceUuid);
        bikeUserTodoRepository.save(bikeUserTodo);
    }

    public BikeSessionRequest fetchTodoSummery(BikeSessionRequest request){
        Map param = request.getParam();
        BikeUserTodoDto bikeUserTodoDto = new BikeUserTodoDto();
        Integer countAll = (Integer)getItem("bikelabs.todo.countMyTodo", param);
        if(!bePresent(countAll)) countAll = 0;
        List<Map> todo = getList("bikelabs.todo.fetchTodoSummery", param);
        if(bePresent(todo)){
            Long refreshToken = (Long)todo.get(0).get("todo_no");
            bikeUserTodoDto.setTodo(todo);
            bikeUserTodoDto.setRefreshToken(refreshToken);
        }
        bikeUserTodoDto.setCount(countAll);
        request.setResponse(bikeUserTodoDto);

        return request;
    }

    public BikeSessionRequest getReferenceIdFromNo(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        String referenceNo = (String)param.get("ref_id");
        BikeUserTodoTypes bikeUserTodoTypes = BikeUserTodoTypes.getBikeUserTodoTypes((String)param.get("todo_type"));
        if(BikeUserTodoTypes.LEASE_APPROVAL.equals(bikeUserTodoTypes) || BikeUserTodoTypes.LEASE_REJECT.equals(bikeUserTodoTypes)){
            response.put("id", getItem("bikelabs.todo.getLeaseIdFromLeaseNo", param));
        }
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteMyTodo(BikeSessionRequest request){
        Map param = request.getParam();
        BikeUser session = request.getSessionUser();
        String _todoNo = (String)param.get("todo_no");
        if(bePresent(_todoNo)){
            Long todoNo = Long.parseLong(_todoNo);
            bikeUserTodoRepository.deleteAllByTodoNoAndToUserNo(todoNo, session.getUserNo());
        }
        return request;
    }

}
