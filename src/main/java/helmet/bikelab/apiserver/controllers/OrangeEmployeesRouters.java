package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

public class OrangeEmployeesRouters {
    @Bean
    public RouterFunction<ServerResponse> employeeRouter(OrangeEmployeesHandler handler){
        return RouterFunctions
                .route(GET("/orange/bike-labs/read-all"), handler::fetchListOfEmployee)
                .andRoute(PUT("/orange/bike-labs/new-user?session_type={session}"), handler::addEmployee)
//                .andRoute(PUT("/orange/bike-labs/update-user"), handler::fireEmployee)
//                .andRoute(DELETE("/orange/bike-labs/dummy-user"), handler::fetchEmployeeInfoByUserNo)
                ;
    }
}
