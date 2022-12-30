package helmet.bikelab.apiserver.controllers.endusers;

import helmet.bikelab.apiserver.domain.riders.InquiryRiderInsurances;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.requests.RiderVerifiedResponse;
import helmet.bikelab.apiserver.objects.responses.FetchRiderDetailResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.endusers.RiderDemandLeaseService;
import helmet.bikelab.apiserver.services.endusers.RiderService;
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
public class RiderHandlers {

    private final RiderService riderService;
    private final RiderDemandLeaseService riderDemandLeaseService;

    public Mono<ServerResponse> fetchRiderListByBike(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> riderService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(riderService::checkBikeSession)
                        .map(riderService::fetchRiderListByBike)
                        .map(riderService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchRidersLeaseRequested(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> riderService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(riderService::checkBikeSession)
                        .map(riderService::fetchRidersLeaseRequested)
                        .map(riderService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchRidersVerified(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> riderService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(riderService::checkBikeSession)
                        .map(riderService::fetchRidersVerified)
                        .map(riderService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchRiders(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> riderService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(riderService::checkBikeSession)
                        .map(riderService::fetchRiders)
                        .map(riderService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchRiderBikeRequests(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> riderService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(riderService::checkBikeSession)
                        .map(riderService::fetchRiderBikeRequests)
                        .map(riderService::returnData), List.class);
    }

    public Mono<ServerResponse> doApproveRider(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(riderService::checkBikeSession)
                        .map(riderService::doApproveRider)
                        .map(riderService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchRiderVerified(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> riderService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(riderService::checkBikeSession)
                        .map(riderService::fetchRiderVerified)
                        .map(riderService::returnData), RiderVerifiedResponse.class);
    }


    public Mono<ServerResponse> doRejectRiderVerified(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(riderService::checkBikeSession)
                        .map(riderService::doRejectRiderVerified)
                        .map(riderService::returnData), Map.class);
    }

    public Mono<ServerResponse> doApproveRiderVerified(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(riderService::checkBikeSession)
                        .map(riderService::doApproveRiderVerified)
                        .map(riderService::returnData), Map.class);
    }

    public Mono<ServerResponse> assignRiderToBike(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(riderService::checkBikeSession)
                        .map(riderService::assignRiderToBike)
                        .map(riderService::returnData), Map.class);
    }

    public Mono<ServerResponse> doDeclineRider(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(riderService::checkBikeSession)
                        .map(riderService::doDeclineRider)
                        .map(riderService::returnData), Map.class);
    }

    public Mono<ServerResponse> addNewRiders(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(riderService::checkBikeSession)
                        .map(riderService::addNewRider)
                        .map(riderService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchRiderDetail(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> riderService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.getPathVariable(row, "rider_id"))
                        .map(riderService::checkBikeSession)
                        .map(riderService::fetchRiderDetail)
                        .map(riderService::returnData), FetchRiderDetailResponse.class);
    }

    public Mono<ServerResponse> updateRider(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> riderService.getPathVariable(row, "rider_id"))
                        .map(riderService::checkBikeSession)
                        .map(riderService::updateRider)
                        .map(riderService::returnData), Map.class);
    }

    public Mono<ServerResponse> stopRider(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(riderService::checkBikeSession)
                        .map(riderService::changeRiderStatus)
                        .map(riderService::returnData), FetchRiderDetailResponse.class);
    }

    public Mono<ServerResponse> resetPassword(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(riderService::checkBikeSession)
                        .map(riderService::resetPassword)
                        .map(riderService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchRiderBikeHistories(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> riderService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.getPathVariable(row, "rider_id"))
                        .map(riderService::checkBikeSession)
                        .map(riderService::fetchRiderBikesHistory)
                        .map(riderService::returnData), List.class);
    }

    public Mono<ServerResponse> approveRiderDemandLease(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderDemandLeaseService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(riderDemandLeaseService::checkBikeSession)
                        .map(riderDemandLeaseService::approveDemandLease)
                        .map(riderDemandLeaseService::returnData), Map.class);
    }

    public Mono<ServerResponse> rejectRiderDemandLease(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderDemandLeaseService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(riderDemandLeaseService::checkBikeSession)
                        .map(riderDemandLeaseService::rejectDemandLease)
                        .map(riderDemandLeaseService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchInsuranceInquiries(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> riderService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(riderService::checkBikeSession)
                        .map(riderService::fetchRiderInsuranceDemandList)
                        .map(riderService::returnData), Page.class);
    }

    public Mono<ServerResponse> fetchInsuranceInquiryDetail(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> riderService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.getPathVariable(row, "inquiry_id"))
                        .map(riderService::checkBikeSession)
                        .map(riderService::fetchRiderInsuranceDemandDetail)
                        .map(riderService::returnData), InquiryRiderInsurances.class);
    }

    public Mono<ServerResponse> updateConsultingDescriptions(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderDemandLeaseService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> riderService.getPathVariable(row, "inquiry_id"))
                        .map(riderService::checkBikeSession)
                        .map(riderService::updateDescription)
                        .map(riderService::returnData), Map.class);
    }

    public Mono<ServerResponse> insuranceInqChangeStatus(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> riderService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> riderService.getPathVariable(row, "inquiry_id"))
                        .map(riderService::checkBikeSession)
                        .map(riderService::changeInsInqStatus)
                        .map(riderService::returnData), Map.class);
    }

}
