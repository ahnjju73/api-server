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
public class LeasesRouters {

    private final DemandLeaseHandler demandLeaseHandler;
    private final LeaseExpenseHandler expenseHandler;

    @Bean
    public RouterFunction<ServerResponse> leasesExtensionRouter(LeasesHandler handler){
        return RouterFunctions
                .route(PUT("/contract/extension"), handler::checkIfExtension)
                .andRoute(POST("/contract/extension"), handler::extensionLeaseById)
                .andRoute(GET("/contract/extension"), handler::getLeaseExtensionList)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> leasesRouter(LeasesHandler handler){
        return RouterFunctions
                .route(GET("/leases"), handler::fetchLeases)
                .andRoute(GET("/leases/{lease_id}"), handler::fetchLease)
                .andRoute(POST("/leases"), handler::addLease)
                .andRoute(PUT("/leases/{lease_id}"), handler::updateLease)
                .andRoute(PUT("/leases/change_status/submit"), handler::submitApplication)
                .andRoute(PUT("/leases/change_status/confirm"), handler::confirmApplication)
                .andRoute(PUT("/leases/change_status/reject"), handler::rejectLease)
                .andRoute(PUT("/cancel-lease"), handler::cancelLease)
                .andRoute(DELETE("/leases"), handler::deleteLease)
                .andRoute(POST("/stop-leases/stop-fee"), handler::fetchStopLeaseFee)
                .andRoute(PUT("/stop_leases"), handler::stopLease)
                .andRoute(PUT("/stop_leases/update"), handler::updateStopLease)
                .andRoute(POST("/leases/{lease_id}/lease-attachments/generate-presigned"), handler::generatePresignedUrl)
                .andRoute(POST("/leases/{lease_id}/lease-attachments"), handler::addLeaseAttachment)
                .andRoute(GET("/commons/contracts/companies"), handler::fetchCompaniesByLease)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> leasesHistoryRouter(LeasesHandler handler){
        return RouterFunctions
                .route(GET("/leases/{lease_id}/histories"), handler::fetchBikeUserLogInLeaseContract);
    }

    @Bean
    public RouterFunction<ServerResponse> demandLeaseRouters(){
        return RouterFunctions
                .route(GET("/demand-leases/{demand_lease_id}"), demandLeaseHandler::fetchDemandLeaseById)
                .andRoute(PATCH("/demand-leases/{demand_lease_id}/complete"), demandLeaseHandler::completedDemandLeaseById)
                .andRoute(PATCH("/demand-leases/{demand_lease_id}/deny"), demandLeaseHandler::denyDemandLeaseById)
                .andRoute(GET("/demand-leases/{demand_lease_id}/leases"), demandLeaseHandler::fetchLeaseListByDemandLeaseNo)
                .andRoute(GET("/demand-leases/{demand_lease_id}/attachments"), demandLeaseHandler::fetchAttachmentsByDemandLeaseId)
                .andRoute(POST("/common/check-holiday"),demandLeaseHandler::checkHoliday)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> leaseExpenseRouters(){
        return RouterFunctions
                .route(GET("/lease-expense"), expenseHandler::fetchExpenses)
                .andRoute(POST("/lease-expense"), expenseHandler::addExpense)
                .andRoute(PUT("/lease-expense"), expenseHandler::updateExpense)
                .andRoute(DELETE("/lease-expense"), expenseHandler::deleteExpense)
                .andRoute(PUT("/lease-expense-option/{expense_no}"), expenseHandler::changeExpenseOption);
    }
}
