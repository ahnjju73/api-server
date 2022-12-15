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
public class BikesRouters {

    private final BikesModelHandlers bikesModelHandlers;
    private final BikeModelByImageHandlers bikeModelByImageHandlers;
    private final BikePartsHandlers partsHandler;
    private final DiagramHandlers diagramHandlers;

    @Bean
    public RouterFunction<ServerResponse> bikeRouter(BikesHandlers handler){
        return RouterFunctions
                .route(GET("/bikes"), handler::fetchListOfBikes)
                .andRoute(GET("/bikes/{bike_id}"), handler::fetchBikeDetail)
                .andRoute(GET("/bikes/client/{client_id}"),handler::fetchBikesByClient)
                .andRoute(POST("/bikes-upload"), handler::uploadExcelToAddBike)
                .andRoute(POST("/bikes"), handler::addBike)
                .andRoute(PUT("/bikes-transaction"), handler::updateBikeTransactionInfo)
                .andRoute(GET("/bikes-transaction"), handler::getBikeTransactionInfo)
                .andRoute(PUT("/bikes"), handler::updateBike)
                .andRoute(DELETE("/bikes"), handler::deleteBike)
                .andRoute(GET("/bikes_volume"), handler::fetchBikeVolumes)
                .andRoute(GET("/bikes_model/codes/{code}"), bikesModelHandlers::fetchModelManufacturerByCode)
                .andRoute(GET("/bikes_model/volumes/{volume}"), handler::fetchBikeModelsByVolume)
                .andRoute(GET("/bikes_model"), handler::fetchBikeModels)
                .andRoute(POST("/bikes_model"), handler::addBikeModel)
                .andRoute(PUT("/bikes_model"), handler::updateBikeModel)
                .andRoute(DELETE("/bikes_model"), handler::deleteBikeModel)
                .andRoute(GET("/bikes_no_lease"), handler::fetchBikesWithoutLease)
                .andRoute(GET("/bikes_ridable"), handler::fetchBikesRidable)
                .andRoute(GET("/bikes/{bike_id}/histories"), handler::fetchHistoriesByBikeId)

                .andRoute(POST("/sections"), bikeModelByImageHandlers::doSaveSection)
                .andRoute(PUT("/sections"), bikeModelByImageHandlers::doUpdateSection)
                .andRoute(POST("/sections/generate/presigned"), bikeModelByImageHandlers::generatePreSignedUrl)
                .andRoute(POST("/sections/axis"), bikeModelByImageHandlers::doSaveSectionAxis)
                .andRoute(PUT("/sections/axis"), bikeModelByImageHandlers::doUpdateSectionAxis)
                .andRoute(POST("/sections/axis/parts"), bikeModelByImageHandlers::handleSectionAxisParts)
                .andRoute(GET("/sections"), bikeModelByImageHandlers::fetchSectionsByModel)
                .andRoute(GET("/sections/{section_no}"), bikeModelByImageHandlers::fetchSectionsDetail)
                .andRoute(GET("/sections-parts"), bikeModelByImageHandlers::fetchPartsByAxis)
                .andRoute(DELETE("/sections/axis"), bikeModelByImageHandlers::deleteAxis)
                .andRoute(DELETE("/sections"), bikeModelByImageHandlers::deleteSection)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> bikeFileRouter(BikesHandlers handler){
        return RouterFunctions
                .route(POST("/bike_file/{bike_id}/generate_pre_sign"), handler::generatePreSign)
                .andRoute(POST("/bike_file/{bike_id}/check_upload"), handler::checkUpload)
                .andRoute(PUT("/bike_file/{bike_id}/types"), handler::updateBikeAttachmentTypeById)
                .andRoute(GET("/bike-file/{bike_id}"), handler::fetchBikeFiles)
                .andRoute(DELETE("/bike-file/{bike_attachment_no}"), handler::deleteBikeFile);
    }

    @Bean
    public RouterFunction<ServerResponse> bikeModelRouters(){
        return RouterFunctions
                .route(GET("/car-models/by-manufacturers"), bikesModelHandlers::fetchCarModelByManufacturer)
                .andRoute(POST("/car-models/by-manufacturers"), bikesModelHandlers::doSaveCarModel)
                .andRoute(PUT("/car-models/by-manufacturers"), bikesModelHandlers::updateCarModel)
                .andRoute(GET("/manufacturers"), bikesModelHandlers::fetchModelManufacturer)
                .andRoute(POST("/manufacturers"), bikesModelHandlers::doSaveManuf)
                .andRoute(PUT("/manufacturers"), bikesModelHandlers::updateManuf)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> bikePartRouters(){
        return RouterFunctions
                .route(GET("/clients/commons/parts"), partsHandler::fetchParts)
                .andRoute(GET("/commons/bikes/parts/details"), partsHandler::fetchPartsByID)
                .andRoute(GET("/commons/bikes/parts-codes"), partsHandler::fetchPartsCodes)
                .andRoute(GET("/commons/bikes/parts-types"), partsHandler::fetchPartsTypes)
                .andRoute(POST("/commons/bikes/parts"), partsHandler::addPartsByModel)
                .andRoute(PUT("/commons/bikes/parts"), partsHandler::updatePartsByIdAndCarModel)
                .andRoute(POST("/commons/bikes/images/pre-signed-url"), partsHandler::generatePresignedUrl)
                .andRoute(POST("/commons/bikes/images"), partsHandler::addNewPartsImage)
                .andRoute(DELETE("/commons/bikes/images"), partsHandler::deletePartsImage)
                .andRoute(GET("/commons/bikes/images"), partsHandler::fetchPartImageByPartsId)
                .andRoute(GET("/commons/parts-types"), partsHandler::fetchPartsTypeList)
                .andRoute(POST("/commons/parts-types"), partsHandler::doSavePartType)
                .andRoute(PUT("/commons/parts-types"), partsHandler::updatePartType)
                .andRoute(DELETE("/commons/parts-types"), partsHandler::deletePartsType)
                .andRoute(POST("/commons/parts-codes/move"), partsHandler::moveParsCodeToAnotherType)
                .andRoute(GET("/commons/parts-codes"), partsHandler::fetchPartsCodeList)
                .andRoute(POST("/commons/parts-codes"), partsHandler::doSavePartsCode)
                .andRoute(PUT("/commons/parts-codes"), partsHandler::updatePartsCode)
                .andRoute(DELETE("/commons/parts-codes"), partsHandler::deletePartsCode)
                .andRoute(GET("/commons/working-price"), partsHandler::fetchCommonWorkingPriceList)
                .andRoute(GET("/commons/codes/parts_codes"), partsHandler::fetchParsCodeListByCondition)
                .andRoute(POST("/upload/bikes/parts"), partsHandler::uploadNewParts)
                .andRoute(POST("/upload/model/parts"), partsHandler::uploadModelParts)
                .andRoute(POST("/upload/model/parts-price"), partsHandler::uploadModelPartsPrice)
                .andRoute(GET("/upload/model/parts-price"), partsHandler::getPartsPriceHistory)
                ;
    }


    @Bean
    public RouterFunction<ServerResponse> bikeRiderFileUploadRouter(BikesHandlers handler){
        return RouterFunctions
                .route(PUT("/upload-bike-riders"), handler::uploadFile);
    }

    @Bean
    public RouterFunction<ServerResponse> bikeModelWorkingRouter(BikesModelHandlers handler){
        return RouterFunctions
                .route(GET("/bike-working"), handler::fetchWorkingPriceByModel)
                .andRoute(PUT("/bike-working"), handler::updateWorkingPriceByModel)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> diagramRouter(){
        return RouterFunctions
                .route(POST("/diagrams"), diagramHandlers::addNewDiagram)
                .andRoute(PUT("/diagrams"), diagramHandlers::updateDiagram)
                .andRoute(DELETE("/diagrams"), diagramHandlers::deleteDiagram)
                .andRoute(GET("/diagrams"), diagramHandlers::fetchAllDiagramList)
                .andRoute(GET("/diagrams/details"), diagramHandlers::fetchDiagramDetailsById)
                .andRoute(PUT("/diagrams/images/signed"), diagramHandlers::generatePreSigned)
                .andRoute(POST("/diagrams/images"), diagramHandlers::updateImageByDiagramId)
                .andRoute(DELETE("/diagrams/images"), diagramHandlers::deleteImageByDiagramId)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> diagramPartsRouter(){
        return RouterFunctions
                .route(POST("/diagrams/parts"), diagramHandlers::addPartsByDiagramId)
                .andRoute(DELETE("/diagrams/parts"), diagramHandlers::removePartsByDiagramId)
                .andRoute(GET("/diagrams/parts"), diagramHandlers::fetchPartListByDiagramId)
                .andRoute(GET("/diagrams/parts/by-all"), diagramHandlers::fetchAllPartListOfDiagramId)
                .andRoute(PUT("/diagrams/part-ordering"), diagramHandlers::reorderPartListOfDiagram)
                ;
    }

}
