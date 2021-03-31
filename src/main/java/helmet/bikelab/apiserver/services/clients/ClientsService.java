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
import helmet.bikelab.apiserver.utils.Crypt;
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
        fetchClientDetailResponse.setGroupId(client.getClientGroup().getGroupId());

        response.put("client", fetchClientDetailResponse);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addClient(BikeSessionRequest request){
        Map param = request.getParam();
        AddClientRequest addClientRequest = map(param, AddClientRequest.class);
        String clientId = autoKey.makeGetKey("client");
        ClientGroups group = groupRepository.findByGroupId(addClientRequest.getGroupId());
        Clients byEmail = clientsRepository.findByEmail(addClientRequest.getEmail());
        if(bePresent(byEmail)) withException("400-005");
        if(!bePresent(group)) withException("400-002");
        Clients clients = new Clients();
        clients.setClientId(clientId);
        clients.setGroupNo(group.getGroupNo());
        clients.setDirectType(YesNoTypes.getYesNo(addClientRequest.getDirect()));
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
        Clients byEmail = clientsRepository.findByEmail(updateClientRequest.getEmail());
        if(bePresent(byEmail) && !byEmail.getClientNo().equals(client.getClientNo())) withException("400-004");
        client.setEmail(updateClientRequest.getEmail());
        ClientGroups clientGroups = groupRepository.findByGroupId(updateClientRequest.getGroupId());
        if(!bePresent(clientGroups)) withException("400-003");
        client.setGroupNo(clientGroups.getGroupNo());
        client.setDirectType(YesNoTypes.getYesNo(updateClientRequest.getDirect()));
        client.setRegNum(updateClientRequest.getRegNo());
        clientsRepository.save(client);
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setClientNo(client.getClientNo());
        clientInfo.setDescription(updateClientRequest.getClientInfo().getDescription());
        clientInfo.setPhone(updateClientRequest.getClientInfo().getPhone());
        clientInfo.setName(updateClientRequest.getClientInfo().getName());

        ClientAddresses clientAddresses = new ClientAddresses();
        clientAddresses.setModelAddress(updateClientRequest.getAddress());
        clientAddresses.setClientNo(client.getClientNo());

        clientInfoRepository.save(clientInfo);
        clientAddressesRepository.save(clientAddresses);

        return request;
    }

    @Transactional
    public BikeSessionRequest resetPassword(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        ResetPasswordRequest resetPasswordRequest = map(param, ResetPasswordRequest.class);
        Clients client = clientsRepository.findByClientId(resetPasswordRequest.getClientId());
        String newPassword = getRandomString();
        ClientPassword clientPassword = clientPasswordRepository.findByClientNo(client.getClientNo());
        clientPassword.updatePassword(newPassword);
        clientPasswordRepository.save(clientPassword);
        response.put("password", newPassword);
        request.setResponse(response);

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

    private String getRandomString(){
        char[] tmp = new char[10];
        for(int i=0; i<tmp.length; i++) {
            boolean div = Math.random()*2<1;
            if(div) {
                tmp[i] = (char) (Math.random() * 10 + '0') ;
            }else {
                tmp[i] = (char) (Math.random() * 26 + 'A') ;
            }
        }
        return new String(tmp);
    }
}
