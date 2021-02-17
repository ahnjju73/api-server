package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class EmployeesRouters {

    @Bean
    public RouterFunction<ServerResponse> employeeRouter(EmployeesHandlers handler){
        return RouterFunctions
                .route(GET("/employees"), handler::fetchListOfEmployee)
//                .andRoute(PATCH("/employees"), handler::modifyEmployeeInfoByManager)
//                .andRoute(DELETE("/employees"), handler::fireEmployee)
//                .andRoute(GET("/employees/{user_no}"), handler::fetchEmployeeInfoByUserNo)
                ;
    }

//    @Bean
//    public RouterFunction<ServerResponse> myInfoRouter(EmployeesHandlers handler){
//        return RouterFunctions
//                .route(GET("/me"), handler::fetchMyInfo)
//                .andRoute(PUT("/me"), handler::updateMyInfo)
//                ;
//    }

}
