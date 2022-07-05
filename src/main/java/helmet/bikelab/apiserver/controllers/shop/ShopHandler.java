package helmet.bikelab.apiserver.controllers.shop;

import helmet.bikelab.apiserver.domain.Settles;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.SessionResponseDto;
import helmet.bikelab.apiserver.objects.responses.FetchSettleDetailResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.SignService;
import helmet.bikelab.apiserver.services.shops.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ShopHandler {

    private final ShopService shopService;
//
    public Mono<ServerResponse> registerNewShop(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .map(row -> shopService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::registerNewShop)
                        .map(shopService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateShopInfo(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .map(row -> shopService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::updateShopInfo)
                        .map(shopService::returnData), Map.class);
    }

    public Mono<ServerResponse> updatePasswordByShopId(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .map(row -> shopService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::updatePasswordByShopId)
                        .map(shopService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchHistoryOfShop(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> shopService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::fetchHistoryOfShop)
                        .map(shopService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchShopDetailsByShopId(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> shopService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::fetchShopDetailsByShopId)
                        .map(shopService::returnData), Shops.class);
    }

    public Mono<ServerResponse> fetchAllShops(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> shopService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::fetchAllShops)
                        .map(shopService::returnData), Page.class);
    }
    public Mono<ServerResponse> fetchAllShopsWithoutPage(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> shopService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::fetchAllShopsWithoutPage)
                        .map(shopService::returnData), List.class);
    }

    public Mono<ServerResponse> fetchBanks(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> shopService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::fetchBanks)
                        .map(shopService::returnData), List.class);
    }

    public Mono<ServerResponse> fetchAllSettles(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> shopService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::fetchSettles)
                        .map(shopService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchSettleDetail(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> shopService.makeSessionRequest(request, BikeSessionRequest.class))
                        .map(row -> shopService.getPathVariable(row, "settle_id"))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::fetchSettleDetail)
                        .map(shopService::returnData), FetchSettleDetailResponse.class);
    }

    public Mono<ServerResponse> completeSettle(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .map(row -> shopService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::completeSettle)
                        .map(shopService::returnData), Map.class);
    }

    public Mono<ServerResponse> generatePresignedUrl(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> shopService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::generatePresignedUrl)
                        .map(shopService::returnData), PresignedURLVo.class);
    }

    public Mono<ServerResponse> fetchAttachments(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> shopService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(shopService::checkBikeSession)
                        .map(shopService::fetchAttachments)
                        .map(shopService::returnData), List.class);
    }


    public Mono<ServerResponse> addFineAttachment(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> shopService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(shopService::checkBikeSession)
                        .map(shopService::addAttachments)
                        .map(shopService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteFineAttachment(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> shopService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(shopService::checkBikeSession)
                        .map(shopService::deleteAttachment)
                        .map(shopService::returnData), Map.class);
    }


}