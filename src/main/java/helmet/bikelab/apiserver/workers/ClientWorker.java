package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.client.ClientAccounts;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.keys.ENV;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClientWorker extends SessService {

    private final ClientAccountsRepository clientAccountsRepository;
    private final ClientAddressesRepository clientAddressesRepository;
    private final ClientInfoRepository clientInfoRepository;
    private final ClientPasswordRepository clientPasswordRepository;
    private final ClientSessionsRepository clientSessionsRepository;
    private final ClientsRepository clientsRepository;
    private final ClientOverpayRepository clientOverpayRepository;
    private final ClientManagersRepository clientManagersRepository;
    private final ClientAttachmentsRepository clientAttachmentsRepository;

    public Clients getClientByClientId(String clientId){
        Clients byClientId = clientsRepository.findByClientId(clientId);
        if(!bePresent(byClientId)) withException("400-100");
        return byClientId;
    }

    public void deleteClientAccount(String clientId){
        clientAccountsRepository.deleteAllByClient_ClientId(clientId);
        clientAddressesRepository.deleteAllByClient_ClientId(clientId);
        clientInfoRepository.deleteAllByClient_ClientId(clientId);
        clientPasswordRepository.deleteAllByClient_ClientId(clientId);
        clientSessionsRepository.deleteAllByClient_ClientId(clientId);
        clientOverpayRepository.deleteAllByClient_ClientId(clientId);
        clientManagersRepository.deleteAllByClient_ClientId(clientId);
        clientAttachmentsRepository.deleteAllByClient_ClientId(clientId);
        clientsRepository.deleteByClientId(clientId);
    }
}
