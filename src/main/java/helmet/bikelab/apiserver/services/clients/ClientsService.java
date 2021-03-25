package helmet.bikelab.apiserver.services.clients;

import helmet.bikelab.apiserver.domain.client.*;
import helmet.bikelab.apiserver.domain.embeds.ModelPassword;
import helmet.bikelab.apiserver.domain.types.AccountStatusTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.clients.*;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ClientsService extends SessService {

    private final AutoKey autoKey;
    private final ClientsRepository clientsRepository;
    private final ClientInfoRepository clientInfoRepository;
    private final ClientPasswordRepository clientPasswordRepository;
    private final ClientGroupRepository groupRepository;
    private final ClientAddressesRepository clientAddressesRepository;

    public BikeSessionRequest fetchListOfClients(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        List<Clients> clients = clientsRepository.findAll();
        List<FetchClientsResponse> responseList = new ArrayList<>();
        for(Clients cl: clients){
            ClientInfo clientInfo = cl.getClientInfo();
            FetchClientsResponse fcr = new FetchClientsResponse();
            fcr.setClientId(cl.getClientId());
            fcr.setClientName(clientInfo.getName());
            fcr.setClientPhone(clientInfo.getPhone());
            responseList.add(fcr);
        }
        response.put("clients", responseList);
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchDetailClient(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        FetchClientDetailRequest clientDetail = map(param, FetchClientDetailRequest.class);
        clientDetail.checkValidation();
        Clients client = clientsRepository.findByClientId(clientDetail.getClientId());
        ClientInfo info = client.getClientInfo();
        ClientAddresses addresses = client.getClientAddresses();
        FetchClientDetailResponse fetchClientDetailResponse = new FetchClientDetailResponse();
        fetchClientDetailResponse.setClientId(client.getClientId());
        fetchClientDetailResponse.setGroupName(client.getClientGroup().getGroupName());
        fetchClientDetailResponse.setDirect(client.getDirectType().getYesNo());
        fetchClientDetailResponse.setEmail(client.getEmail());
        fetchClientDetailResponse.setClientDescription(info.getDescription());
        fetchClientDetailResponse.setRegNo(client.getRegNum());
        fetchClientDetailResponse.setClientInfo(info);
        fetchClientDetailResponse.setAddress(addresses.getModelAddress());

        response.put("client", fetchClientDetailResponse);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addClient(BikeSessionRequest request){
        Map param = request.getParam();
        AddClientRequest addClientRequest = map(param, AddClientRequest.class);
        addClientRequest.checkValidation();
        String clientId = autoKey.makeGetKey("client");
        ClientGroups group = groupRepository.findByGroupId(addClientRequest.getGroupId());
        Clients clients = new Clients();
        clients.setClientId(clientId);
        clients.setGroupNo(group.getGroupNo());
        clients.setDirectType(YesNoTypes.getYesNo(addClientRequest.getDirectYn()));
        clients.setStatus(AccountStatusTypes.PENDING);
        clients.setEmail(addClientRequest.getEmail());
        clients.setRegNum(addClientRequest.getRegNo());
        clientsRepository.save(clients);

        ClientInfo clientInfo = new ClientInfo();;
        clientInfo.setClientNo(clients.getClientNo());
        clientInfo.setDescription(addClientRequest.getClientInfo().getDescription());
        clientInfo.setPhone(addClientRequest.getClientInfo().getPhone());
        clientInfo.setName(addClientRequest.getClientInfo().getName());

        ClientAddresses clientAddresses = new ClientAddresses();
        clientAddresses.setModelAddress(addClientRequest.getAddress());
        clientAddresses.setClientNo(clients.getClientNo());

        ClientPassword clientPassword = new ClientPassword();
        clientPassword.newPassword(addClientRequest.getEmail());
        clientPassword.setClientNo(clients.getClientNo());

        clientInfoRepository.save(clientInfo);
        clientAddressesRepository.save(clientAddresses);
        clientPasswordRepository.save(clientPassword);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateClient(BikeSessionRequest request){
        Map param = request.getParam();
        UpdateClientRequest updateClientRequest = map(param, UpdateClientRequest.class);
        Clients client = clientsRepository.findByClientId(updateClientRequest.getClientId());
        client.setEmail(updateClientRequest.getEmail());
        client.setGroupNo(groupRepository.findByGroupId(updateClientRequest.getGroupId()).getGroupNo());
        client.setDirectType(YesNoTypes.getYesNo(updateClientRequest.getDirectYn()));
        client.setRegNum(updateClientRequest.getRegNo());
        clientsRepository.save(client);

        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setClientNo(client.getClientNo());
        clientInfo.setDescription(updateClientRequest.getClientInfo().getDescription());
        clientInfo.setPhone(updateClientRequest.getClientInfo().getPhone());
        clientInfo.setName(updateClientRequest.getClientInfo().getName());

        ClientPassword clientPassword = new ClientPassword();
        clientPassword.setClientNo(client.getClientNo());
        ModelPassword modelPassword = client.getClientPassword().getModelPassword();
        modelPassword.modifyPassword(updateClientRequest.getClientPassword());
        clientPassword.setModelPassword(modelPassword);

        ClientAddresses clientAddresses = new ClientAddresses();
        clientAddresses.setModelAddress(updateClientRequest.getAddress());
        clientAddresses.setClientNo(client.getClientNo());

        clientInfoRepository.save(clientInfo);
        clientPasswordRepository.save(clientPassword);
        clientAddressesRepository.save(clientAddresses);

        return request;
    }

    @Transactional
    public BikeSessionRequest deleteClient(BikeSessionRequest request){
        Map param = request.getParam();
        DeleteClientRequest deleteClientRequest = map(param, DeleteClientRequest.class);
        Clients client = clientsRepository.findByClientId(deleteClientRequest.getClientId());
        client.setStatus(AccountStatusTypes.DELETE);
        clientsRepository.save(client);
        return request;
    }
}
