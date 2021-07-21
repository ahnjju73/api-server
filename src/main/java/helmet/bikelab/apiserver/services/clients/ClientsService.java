package helmet.bikelab.apiserver.services.clients;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.*;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.AccountStatusTypes;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.clients.*;
import helmet.bikelab.apiserver.objects.requests.ClientListDto;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.workers.ClientWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;
import static helmet.bikelab.apiserver.utils.Utils.randomPassword;

@RequiredArgsConstructor
@Service
public class ClientsService extends SessService {

    private final AutoKey autoKey;
    private final ClientsRepository clientsRepository;
    private final ClientInfoRepository clientInfoRepository;
    private final ClientPasswordRepository clientPasswordRepository;
    private final ClientGroupRepository groupRepository;
    private final ClientAddressesRepository clientAddressesRepository;
    private final ClientOverpayRepository clientOverpayRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final CommonWorker commonWorker;
    private final LeaseRepository leaseRepository;
    private final ClientWorker clientWorker;

    public BikeSessionRequest fetchHistoryOfClient(BikeSessionRequest request){
        Map param = request.getParam();
        ClientListDto requestListDto = map(param, ClientListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.bike_user_log.getBikeUserLogInClients", "bikelabs.bike_user_log.countAllBikeUserLogInClients", "log_no");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchListOfClients(BikeSessionRequest request){
        Map param = request.getParam();
        RequestListDto requestListDto = map(param, RequestListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.commons.clients.fetchClientList", "bikelabs.commons.clients.countAllClientList", "client_id");
        request.setResponse(responseListDto);
        return request;
    }

    @Deprecated
    public BikeSessionRequest bak_fetchListOfClients(BikeSessionRequest request){
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
            fcr.setManagerName(clientInfo.getManagerName());
            fcr.setManagerPhone(clientInfo.getManagerPhone());
            fcr.setManagerEmail(clientInfo.getManagerEmail());
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
        fetchClientDetailResponse.setUuid(client.getUuid());
        response.put("client", fetchClientDetailResponse);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest fetchClientOverpays(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        FetchClientDetailRequest clientDetail = map(param, FetchClientDetailRequest.class);
        Clients client = clientsRepository.findByClientId(clientDetail.getClientId());
        List<ClientOverpay> overpays = clientOverpayRepository.findAllByClientNo(client.getClientNo());
        response.put("overpay", overpays);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addClient(BikeSessionRequest request){
        Map param = request.getParam();
        BikeUser session = request.getSessionUser();
        AddClientRequest addClientRequest = map(param, AddClientRequest.class);
        String clientId = autoKey.makeGetKey("client");
        ClientGroups group = groupRepository.findByGroupId(addClientRequest.getGroupId());
        if(!bePresent(group)) withException("400-002");
        if(addClientRequest.getUuid() != null && clientsRepository.countAllByUuid(addClientRequest.getUuid()) > 0)
            withException("");
        if(addClientRequest.getRegNo() != null && clientsRepository.countAllByRegNum(addClientRequest.getRegNo()) > 0)
            withException("");

        Clients clients = new Clients();
        clients.setClientId(clientId);
        clients.setGroupNo(group.getGroupNo());
        clients.setDirectType(YesNoTypes.getYesNo(addClientRequest.getDirect()));
        clients.setStatus(AccountStatusTypes.PENDING);
        clients.setEmail(addClientRequest.getEmail());
        clients.setRegNum(addClientRequest.getRegNo());
        clients.setUuid(addClientRequest.getUuid());
        clientsRepository.save(clients);

        ClientInfo clientInfo = new ClientInfo();;
        clientInfo.setClientNo(clients.getClientNo());
        clientInfo.setDescription(addClientRequest.getClientInfo().getDescription());
        clientInfo.setPhone(addClientRequest.getClientInfo().getPhone());
        clientInfo.setName(addClientRequest.getClientInfo().getName());
        clientInfo.setManagerEmail(addClientRequest.getClientInfo().getManagerEmail());
        clientInfo.setManagerName(addClientRequest.getClientInfo().getManagerName());
        clientInfo.setManagerPhone(addClientRequest.getClientInfo().getManagerPhone());
        clientInfo.setClient(clients);
        clientInfoRepository.save(clientInfo);

        ClientAddresses clientAddresses = new ClientAddresses();
        clientAddresses.setModelAddress(addClientRequest.getAddress());
        clientAddresses.setClientNo(clients.getClientNo());
        clientAddresses.setClient(clients);

        clientAddressesRepository.save(clientAddresses);

        String password = randomPassword(10);
        ClientPassword clientPassword = new ClientPassword();
        clientPassword.newPassword(password);
        clientPassword.setClientNo(clients.getClientNo());
        clientPassword.setClient(clients);
        clientPasswordRepository.save(clientPassword);

        clients.setClientAddresses(clientAddresses);
        clients.setClientInfo(clientInfo);
        clients.setClientPassword(clientPassword);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_CLIENT_ADDED, session.getUserNo(), clients.getClientNo().toString()));

        return request;
    }

    @Transactional
    public BikeSessionRequest updateClient(BikeSessionRequest request){
        Map param = request.getParam();
        UpdateClientRequest updateClientRequest = map(param, UpdateClientRequest.class);
        Clients client = clientsRepository.findByClientId(updateClientRequest.getClientId());
        ClientInfo clientInfo = client.getClientInfo();
        ClientAddresses clientAddresses = client.getClientAddresses();
        if(!bePresent(updateClientRequest.getUuid())) withException("400-006");
        client.setEmail(updateClientRequest.getEmail());
        ClientGroups clientGroups = groupRepository.findByGroupId(updateClientRequest.getGroupId());
        if(!bePresent(clientGroups)) withException("400-003");
        if(clientsRepository.findByUuid(updateClientRequest.getUuid())!= null && !client.getClientId().equals(clientsRepository.findByUuid(updateClientRequest.getUuid()).getClientId()))
            withException("");
        if(clientsRepository.findByRegNum(updateClientRequest.getRegNo()) != null && !client.getClientId().equals(clientsRepository.findByRegNum(updateClientRequest.getRegNo()).getClientId()))
            withException("");

        BikeUser session = request.getSessionUser();
        ClientGroups preGroup = client.getClientGroup();
        client.setClientGroup(clientGroups);
        client.setGroupNo(clientGroups.getGroupNo());
        updateClientUserLog(BikeUserLogTypes.COMM_CLIENT_UPDATED, session.getUserNo(), updateClientRequest, client, clientInfo, preGroup);
        client.setUuid(updateClientRequest.getUuid());

        client.setDirectType(YesNoTypes.getYesNo(updateClientRequest.getDirect()));
        client.setRegNum(updateClientRequest.getRegNo());
        clientsRepository.save(client);
        clientInfo.setClientNo(client.getClientNo());
        clientInfo.setDescription(updateClientRequest.getClientInfo().getDescription());
        clientInfo.setPhone(updateClientRequest.getClientInfo().getPhone());
        clientInfo.setName(updateClientRequest.getClientInfo().getName());
        clientInfo.setManagerEmail(updateClientRequest.getClientInfo().getManagerEmail());
        clientInfo.setManagerName(updateClientRequest.getClientInfo().getManagerName());
        clientInfo.setManagerPhone(updateClientRequest.getClientInfo().getManagerPhone());
        clientAddresses.setModelAddress(updateClientRequest.getAddress());
        clientAddresses.setClientNo(client.getClientNo());
        clientInfoRepository.save(clientInfo);
        clientAddressesRepository.save(clientAddresses);

        return request;
    }

    public void updateClientUserLog(BikeUserLogTypes bikeUserLogTypes, Integer fromUserNo, UpdateClientRequest updateClientRequest, Clients clients, ClientInfo clientInfo, ClientGroups preGroup){
        if(bePresent(updateClientRequest)){
            List<String> stringList = new ArrayList<>();
            ClientInfo ci = updateClientRequest.getClientInfo();
            ClientAddresses clientAddresses = clients.getClientAddresses() == null ? new ClientAddresses() : clients.getClientAddresses();
            ModelAddress modelAddress = clientAddresses.getModelAddress() == null ? new ModelAddress() : clientAddresses.getModelAddress();
            ModelAddress address = updateClientRequest.getAddress();
            if(bePresent(updateClientRequest.getGroupId()) && !updateClientRequest.getGroupId().equals(preGroup.getGroupId())){
                ClientGroups clientGroup = clients.getClientGroup();
                stringList.add("그룹정보를 <>" + preGroup.getGroupName() + "[" +  preGroup.getGroupId()+ "]</>에서 <>" + clientGroup.getGroupName() + "[" + clientGroup.getGroupId() + "]</>으로 변경하였습니다.");
            }
            if(bePresent(updateClientRequest.getEmail()) && !updateClientRequest.getEmail().equals(clients.getEmail())){
                if(clients.getEmail() == null){
                    stringList.add("고객사 이메일을 <>" + updateClientRequest.getEmail() + "</>으로 변경하였습니다.");
                }else stringList.add("고객사 이메일을 <>" + clients.getEmail() + "</>에서 <>" + updateClientRequest.getEmail() + "</>으로 변경하였습니다.");
            }
            if(bePresent(updateClientRequest.getDirect()) && !YesNoTypes.getYesNo(updateClientRequest.getDirect()).equals(clients.getDirectType())){
                stringList.add("고객사 직영여부를 변경하였습니다.");
            }
            if(bePresent(updateClientRequest.getRegNo()) && !updateClientRequest.getRegNo().equals(clients.getRegNum())){
                if(clients.getRegNum() == null){
                    stringList.add("사업자번호를 <>" + updateClientRequest.getRegNo() + "</>으로 변경하였습니다.");
                }else stringList.add("사업자번호를 <>" + clients.getRegNum() + "</>에서 <>" + updateClientRequest.getRegNo() + "</>로 변경하였습니다.");
            }
            if(bePresent(updateClientRequest.getUuid()) && !updateClientRequest.getUuid().equals(clients.getUuid())){
                if(clients.getUuid() == null){
                    stringList.add("총판코드를 <>" + updateClientRequest.getUuid() + "</>으로 변경하였습니다.");
                }else stringList.add("총판코드를 <>" + clients.getUuid() + "</>에서 <>" + updateClientRequest.getUuid() + "</>로 변경하였습니다.");
            }
            if(bePresent(ci)){
                if(bePresent(ci.getDescription()) && !ci.getDescription().equals(clientInfo.getDescription())){
                    if(clientInfo.getDescription() == null){
                        stringList.add("고객사 설명을 <>" + ci.getDescription() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 설명을 <>" + clientInfo.getDescription() + "</>에서 <>" + ci.getDescription() + "</>로 변경하였습니다.");
                }
                if(bePresent(ci.getPhone()) && !ci.getPhone().equals(clientInfo.getPhone())){
                    if(clientInfo.getPhone() == null){
                        stringList.add("고객사 연락처를 <>" + ci.getPhone() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 연락처를 <>" + clientInfo.getPhone() + "</>에서 <>" + ci.getPhone() + "</>로 변경하였습니다.");
                }
                if(bePresent(ci.getName()) && !ci.getName().equals(clientInfo.getName())){
                    if(clientInfo.getName() == null){
                        stringList.add("고객사명을 <>" + ci.getName() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사명을 <>" + clientInfo.getName() + "</>에서 <>" + ci.getName() + "</>로 변경하였습니다.");
                }
                if(bePresent(ci.getManagerEmail()) && !ci.getManagerEmail().equals(clientInfo.getManagerEmail())){
                    if(clientInfo.getManagerEmail() == null){
                        stringList.add("고객사 담당자 Email을 <>" + ci.getManagerEmail() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 담당자 Email을 <>" + clientInfo.getManagerEmail() + "</>에서 <>" + ci.getManagerEmail() + "</>로 변경하였습니다.");
                }
                if(bePresent(ci.getManagerName()) && !ci.getManagerName().equals(clientInfo.getManagerName())){
                    if(clientInfo.getManagerName() == null){
                        stringList.add("고객사 담당자명을 <>" + ci.getManagerName() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 담당자명을 <>" + clientInfo.getManagerName() + "</>에서 <>" + ci.getManagerName() + "</>로 변경하였습니다.");
                }
                if(bePresent(ci.getManagerPhone()) && !ci.getManagerPhone().equals(clientInfo.getManagerPhone())){
                    if(clientInfo.getManagerPhone() == null){
                        stringList.add("고객사 담당자 연락처를 <>" + ci.getManagerPhone() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 담당자 연락처를 <>" + clientInfo.getManagerPhone() + "</>에서 <>" + ci.getManagerPhone() + "</>로 변경하였습니다.");
                }
                if(bePresent(address) && !address.getAddress().equals(modelAddress.getAddress())){
                    stringList.add("고객사 주소를 변경하였습니다.");
                }
            }
            if(bePresent(stringList) && stringList.size() > 0)
                bikeUserLogRepository.save(addLog(bikeUserLogTypes, fromUserNo, clients.getClientNo().toString(), stringList));
        }
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
        List<Leases> allByClients_clientId = leaseRepository.findAllByClients_ClientIdOrderByLeaseInfo_ContractDate(deleteClientRequest.getClientId());
        if(bePresent(allByClients_clientId)) withException("410-001");
        clientWorker.deleteClientAccount(deleteClientRequest.getClientId());
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
