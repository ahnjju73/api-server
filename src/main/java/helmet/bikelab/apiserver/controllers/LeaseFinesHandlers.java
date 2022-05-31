package helmet.bikelab.apiserver.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LeaseFinesHandlers {

    public Mono<ServerResponse> fetchFines(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> fetchFineDetail(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> addFine(ServerRequest request){
        return null;
    }

    public Mono<ServerResponse> updateFine(ServerRequest request){
        return null;
    }

    public Mono<ServerResponse> deleteFine(ServerRequest request){
        return null;
    }

    public Mono<ServerResponse> generatePresignedUrl(ServerRequest request){
        return null;
    }

    public Mono<ServerResponse> fetchAttachments(ServerRequest request){
        return null;
    }


    public Mono<ServerResponse> addFineAttachment(ServerRequest request){
        return null;
    }

    public Mono<ServerResponse> deleteFineAttachment(ServerRequest request){
        return null;
    }

}