package helmet.bikelab.apiserver.controllers;


import helmet.bikelab.apiserver.domain.ins_companies.InsuranceCompanies;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.insurance.InsuranceCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class InsuranceCompanyHandler {
    private final InsuranceCompanyService insuranceCompanyService;

    public Mono<ServerResponse> fetchInsuranceCompanies(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> insuranceCompanyService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(insuranceCompanyService::checkBikeSession)
                        .map(insuranceCompanyService::fetchInsCompanies)
                        .map(insuranceCompanyService::returnData), Page.class);
    }

    public Mono<ServerResponse> fetchInsCompanyDetail(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> insuranceCompanyService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> insuranceCompanyService.getPathVariable(req, "company_id"))
                        .map(insuranceCompanyService::checkBikeSession)
                        .map(insuranceCompanyService::fetchInsCompanyDetail)
                        .map(insuranceCompanyService::returnData), InsuranceCompanies.class);
    }

    public Mono<ServerResponse> addInsuranceCompany(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> insuranceCompanyService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(insuranceCompanyService::checkBikeSession)
                        .map(insuranceCompanyService::addCompany)
                        .map(insuranceCompanyService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateInsCompany(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> insuranceCompanyService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(req -> insuranceCompanyService.getPathVariable(req, "company_id"))
                        .map(insuranceCompanyService::checkBikeSession)
                        .map(insuranceCompanyService::updateCompany)
                        .map(insuranceCompanyService::returnData), Map.class);
    }

    public Mono<ServerResponse> resetPassword(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> insuranceCompanyService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(req -> insuranceCompanyService.getPathVariable(req, "company_id"))
                        .map(insuranceCompanyService::checkBikeSession)
                        .map(insuranceCompanyService::resetPassword)
                        .map(insuranceCompanyService::returnData), Map.class);
    }

}
