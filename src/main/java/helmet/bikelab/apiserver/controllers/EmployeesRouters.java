package helmet.bikelab.apiserver.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
@RequiredArgsConstructor
public class EmployeesRouters {

    private final SignHandlers signHandlers;

    @Bean
    public RouterFunction<ServerResponse> employeeRouter(EmployeesHandlers handler){
        return RouterFunctions
                .route(GET("/employees"), handler::fetchListOfEmployee)
                .andRoute(POST("/employees"), signHandlers::addBikeUser)
                .andRoute(GET("/employees/{user_no}"), handler::fetchEmployeeInfoByUserNo)
                .andRoute(PATCH("/employees/{user_no}"), handler::modifyEmployeeInfoByManager)
//                .andRoute(PATCH("/employees"), handler::modifyEmployeeInfoByManager)
//                .andRoute(DELETE("/employees"), handler::fireEmployee)
//                .andRoute(GET("/employees/{user_no}"), handler::x)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> todoRouter(EmployeesHandlers handler){
        return RouterFunctions
                .route(GET("/me/todo"), handler::fetchTodoSummery)
                .andRoute(GET("/me/todo/reference-id"), handler::getReferenceIdFromNo)
                ;
    }

}
