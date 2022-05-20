package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeaseBikeUserLogs;
import helmet.bikelab.apiserver.objects.responses.LeaseExtensionCheckedResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.employees.BikeUserLogService;
import helmet.bikelab.apiserver.services.leases.LeaseExtensionService;
import helmet.bikelab.apiserver.services.leases.LeasesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LeasesHandler {

    private final LeasesService leasesService;
    private final BikeUserLogService bikeUserLogService;
    private final LeaseExtensionService leaseExtensionService;

    public Mono<ServerResponse> fetchCompaniesByLease(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::fetchCompaniesByLease)
                        .map(leasesService::returnData), List.class);
    }

    public Mono<ServerResponse> getLeaseExtensionList(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leaseExtensionService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leaseExtensionService::checkBikeSession)
                        .map(leaseExtensionService::getLeaseExtensionList)
                        .map(leaseExtensionService::returnData), List.class);
    }

    public Mono<ServerResponse> checkIfExtension(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leaseExtensionService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(leaseExtensionService::checkBikeSession)
                        .map(leaseExtensionService::checkIfExtension)
                        .map(leaseExtensionService::returnData), LeaseExtensionCheckedResponse.class);
    }

    public Mono<ServerResponse> extensionLeaseById(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leaseExtensionService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(leaseExtensionService::checkBikeSession)
                        .map(leaseExtensionService::extensionLeaseById)
                        .map(leaseExtensionService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchLeases(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::fetchLeases)
                        .map(leasesService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchLease(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> leasesService.getPathVariable(req, "lease_id"))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::fetchDetailLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> addLease(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leasesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::addLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateLease(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leasesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> leasesService.getPathVariable(row, "lease_id"))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::updateLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> confirmApplication(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::confirmLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> submitApplication(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::pendingLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> rejectLease (ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::rejectLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> cancelLease(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::cancelLease)
                        .map(leasesService::returnData), Map.class);
    }


    public Mono<ServerResponse> deleteLease(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::deleteLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchBikeUserLogInLeaseContract(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeUserLogService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikeUserLogService::checkBikeSession)
                        .map(row -> bikeUserLogService.getPathVariable(row, "lease_id"))
                        .map(bikeUserLogService::fetchBikeUserLogInLeaseContract)
                        .map(bikeUserLogService::returnData), LeaseBikeUserLogs.class);
    }

    public Mono<ServerResponse> fetchStopLeaseFee(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leasesService.makeSessionRequest(request, row , BikeSessionRequest.class))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::fetchStopLeaseFee)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> stopLease(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leasesService.makeSessionRequest(request, row , BikeSessionRequest.class))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::stopLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateStopLease(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leasesService.makeSessionRequest(request, row , BikeSessionRequest.class))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::updateStopLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> generatePresignedUrl(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leasesService.makeSessionRequest(request, row , BikeSessionRequest.class))
                        .map(req -> leasesService.getPathVariable(req, "lease_id"))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::generatePreSignedURLToUploadLeaseFile)
                        .map(leasesService::returnData), PresignedURLVo.class);
    }

    public Mono<ServerResponse> addLeaseAttachment(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> leasesService.getPathVariable(req, "lease_id"))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::addAttachments)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteAttachment(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leasesService.makeSessionRequest(request, row , BikeSessionRequest.class))
                        .map(req -> leasesService.getPathVariable(req, "lease_id"))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::deleteAttachments)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> getAttachments(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> leasesService.getPathVariable(req, "lease_id"))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::getLeaseAttachments)
                        .map(leasesService::returnData), List.class);
    }
}
