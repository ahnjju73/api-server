package helmet.bikelab.apiserver.controllers.shop;

import helmet.bikelab.apiserver.controllers.DemandLeaseHandler;
import helmet.bikelab.apiserver.controllers.LeaseExpenseHandler;
import helmet.bikelab.apiserver.controllers.LeasesHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
@RequiredArgsConstructor
public class ShopRouters {

    private final ShopHandler shopHandler;

    @Bean
    public RouterFunction<ServerResponse> shopRouter(){
        return RouterFunctions
                .route(POST("/repairshop"), shopHandler::registerNewShop)
                .andRoute(PUT("/repairshop"), shopHandler::updateShopInfo)
                .andRoute(GET("/repairshop/details"), shopHandler::fetchShopDetailsByShopId)
                .andRoute(GET("/repairshop"), shopHandler::fetchAllShops)
                .andRoute(GET("/repairshop/no-page"), shopHandler::fetchAllShopsWithoutPage)
                .andRoute(PUT("/repairshop/password"), shopHandler::updatePasswordByShopId)
                .andRoute(GET("/repairshop/histories"), shopHandler::fetchHistoryOfShop)
                .andRoute(POST("/repairshop/generate-presigned"), shopHandler::generatePresignedUrl)
                .andRoute(GET("/repairshop/attachments"), shopHandler::fetchAttachments)
                .andRoute(POST("/repairshop/attachments"), shopHandler::addFineAttachment)
                .andRoute(DELETE("/repairshop/attachments"), shopHandler::deleteFineAttachment)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> bankRouter(){
        return RouterFunctions
                .route(GET("/banks"), shopHandler::fetchBanks)
                .andRoute(GET("/settles"), shopHandler::fetchAllSettles)
                .andRoute(GET("/settles/{settle_id}"), shopHandler::fetchSettleDetail)
                .andRoute(PATCH("/settles"), shopHandler::completeSettle)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> inspectRouter() {
        return RouterFunctions
                .route(POST("/repairshop/regular-inspect"), shopHandler::addRegularInspection)
                .andRoute(GET("/repairshop/regular-inspect"), shopHandler::fetchInspectionList)
                .andRoute(GET("/repairshop/regular-inspect-by-group"), shopHandler::fetchInspectionListByGroups)
                .andRoute(PUT("/repairshop/regular-inspect"), shopHandler::updateRegularInspect)
                .andRoute(GET("/repairshop/regular-inspect/{inspect_id}"), shopHandler::fetchInspectionDetail)
                .andRoute(PATCH("/repairshop/regular-inspect/change-date"), shopHandler::changeIncludeDate)
                .andRoute(POST("/repairshop/regular-inspect/generate-presignedurl"), shopHandler::generatePresignedUrl)
                .andRoute(DELETE("/repairshop/regular-inspect"), shopHandler::deleteInspect)
                ;
    }
}
