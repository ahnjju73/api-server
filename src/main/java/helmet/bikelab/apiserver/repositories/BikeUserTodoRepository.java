package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeUserTodo;
import helmet.bikelab.apiserver.domain.types.BikeUserTodoTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BikeUserTodoRepository extends JpaRepository<BikeUserTodo, Integer> {

    BikeUserTodo findByTodoTypesAndToUserNoAndReferenceId(BikeUserTodoTypes bikeUserTodoTypes, Integer toUserNo, String referenceId);

    void deleteAllByTodoTypesAndToUserNoAndReferenceId(BikeUserTodoTypes bikeUserTodoTypes, Integer toUserNo, String referenceId);
    void deleteAllByTodoTypesAndReferenceId(BikeUserTodoTypes bikeUserTodoTypes, String referenceId);
    void deleteAllByReferenceId(String referenceId);
    void deleteAllByTodoNoAndToUserNo(Long todoNo, Integer toUserNo);

}

