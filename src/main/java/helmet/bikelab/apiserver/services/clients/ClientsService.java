package helmet.bikelab.apiserver.services.clients;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.bike.BikeAttachments;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bike.PartsTypeDiscountClient;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.*;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.shops.ShopInfo;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.types.AccountStatusTypes;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.BusinessTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.bikelabs.clients.*;
import helmet.bikelab.apiserver.objects.requests.*;
import helmet.bikelab.apiserver.objects.responses.PartsDiscountRateByClient;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.ClientWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.ShopWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ClientAttachmentRepository clientAttachmentRepository;
    private final CommonWorker commonWorker;
    private final LeaseRepository leaseRepository;
    private final ClientWorker clientWorker;
    private final PartsTypeDiscountClientRepository partsTypeDiscountClientRepository;
    private final ShopWorker shopWorker;
    private final ClientShopRepository clientShopRepository;

    @Transactional
    public BikeSessionRequest updatePartsDiscountRateByClient(BikeSessionRequest request){
        PartsDiscountRateByClientRequest requestParam = map(request.getParam(), PartsDiscountRateByClientRequest.class);
        requestParam.checkValidation();
        List<DiscountedParts> discountedParts = requestParam.getDiscountedParts();
        BikeUser sessionUser = request.getSessionUser();
        Clients clientByClientId = clientWorker.getClientByClientId(requestParam.getClientId());
        updateClientDiscountRateByUserLog(BikeUserLogTypes.COMM_CLIENT_UPDATED, sessionUser.getUserNo(), clientByClientId, requestParam);
        clientByClientId.setDiscountRate(requestParam.getDiscountRate());
        clientsRepository.save(clientByClientId);

        partsTypeDiscountClientRepository.deleteAllByClientNo(clientByClientId.getClientNo());
        if(bePresent(discountedParts)){
            List<PartsTypeDiscountClient> collect = discountedParts.stream().map(elm -> {
                PartsTypeDiscountClient partsTypeDiscountClient = new PartsTypeDiscountClient();
                partsTypeDiscountClient.setPartsTypeNo(elm.getPartsTypeNo());
                partsTypeDiscountClient.setClientNo(clientByClientId.getClientNo());
                partsTypeDiscountClient.setDiscountRate(elm.getDiscountRate());
                return partsTypeDiscountClient;
            }).collect(Collectors.toList());
            partsTypeDiscountClientRepository.saveAll(collect);
        }
        //if(clients.getDiscountRate() == null){
        //                    //stringList.add("부품할인가를 <>" + String.format("%.2f", updateClientRequest.getDiscountRate()) + "%</>(으)로 설정하였습니다.");
        //                    stringList.add("고객사 할인율을 <>" + String.format("%.1f",updateClientRequest.getDiscountRate()*100) + "%</>(으)로 설정하였습니다.");
        //                //}else stringList.add("부품할인가를 <>" + String.format("%.2f", clients.getDiscountRate()) + "%</>에서 <>" + String.format("%.2f", updateClientRequest.getDiscountRate()) + "%</>로 변경하였습니다.");
        //                }else stringList.add("고객사 할인율을 <>" + String.format("%.1f",clients.getDiscountRate()*100) + "%</>에서 <>" + String.format("%.1f",updateClientRequest.getDiscountRate()*100) + "%</>로 변경하였습니다.");
//        bikeUserLogRepository.save(addLog(bikeUserLogTypes, fromUserNo, clients.getClientNo().toString(), stringList));


        return request;
    }

    public void updateClientDiscountRateByUserLog(BikeUserLogTypes bikeUserLogTypes, Integer fromUserNo, Clients clients, PartsDiscountRateByClientRequest requestParam){
        if(bePresent(requestParam)){
            List<String> stringList = new ArrayList<>();
            if(bePresent(requestParam.getDiscountRate()) && !requestParam.getDiscountRate().equals(clients.getDiscountRate())){
                if(bePresent(clients.getDiscountRate()))
                    //stringList.add("고객사 <>부품할인율</>을 <>" + clients.getDiscountRate() + "</>에서 <>" + requestParam.getDiscountRate() + "</>(으)로 변경하였습니다.");
                    stringList.add("고객사 할인율을 <>" + String.format("%.1f",clients.getDiscountRate()*100) + "%</>에서 <>" + String.format("%.1f",requestParam.getDiscountRate()*100) + "%</>(으)로 변경하였습니다.");
                //else stringList.add("고객사 <>부품할인율</>를 <>" +  requestParam.getDiscountRate() + "</>(으)로 등록하였습니다.");
                else stringList.add("고객사 할인율을 <>" +  String.format("%.1f",requestParam.getDiscountRate()*100) + "%</>(으)로 등록하였습니다.");
            }
            if(bePresent(stringList) && stringList.size() > 0)
                bikeUserLogRepository.save(addLog(bikeUserLogTypes, fromUserNo, clients.getClientNo().toString(), stringList));
        }
    }

    public BikeSessionRequest fetchPartsDiscountRateByClient(BikeSessionRequest request){
        Map param = request.getParam();
        String clientId = (String)param.get("client_id");
        Clients clientByClientId = clientWorker.getClientByClientId(clientId);
        param.put("client_no", clientByClientId.getClientNo());
        PartsDiscountRateByClient partsDiscountRateByClient = new PartsDiscountRateByClient();
        partsDiscountRateByClient.setDiscountRate(clientByClientId.getDiscountRate());
        List discountedParts = getList("bikelabs.commons.clients.fetchPartsDiscountRateByClient", param);
        partsDiscountRateByClient.setDiscountedParts(discountedParts);
        request.setResponse(partsDiscountRateByClient);
        return request;
    }

    public BikeSessionRequest fetchHistoryOfClient(BikeSessionRequest request){
        Map param = request.getParam();
        ClientListDto requestListDto = map(param, ClientListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.bike_user_log.getBikeUserLogInClients", "bikelabs.bike_user_log.countAllBikeUserLogInClients", "log_no");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchListOfClients(BikeSessionRequest request){
        Map param = request.getParam();
        ClientListByConditionRequest requestListDto = map(param, ClientListByConditionRequest.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.commons.clients.fetchClientList", "bikelabs.commons.clients.countAllClientList", "client_id");
        request.setResponse(responseListDto);
        return request;
    }

    @Deprecated
    public BikeSessionRequest bak_fetchListOfClients(BikeSessionRequest request){
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
        fetchClientDetailResponse.setRegNo(client.getRegNum());
        fetchClientDetailResponse.setClientInfo(info);
        fetchClientDetailResponse.setAddress(addresses.getModelAddress());
        fetchClientDetailResponse.setGroupId(client.getClientGroup().getGroupId());
        fetchClientDetailResponse.setUuid(client.getUuid());
        fetchClientDetailResponse.setBusinessNo(client.getBusinessNo());
        fetchClientDetailResponse.setBusinessType(client.getBusinessType());
        fetchClientDetailResponse.setBusinessTypeCode(client.getBusinessType().getBusinessType());
        fetchClientDetailResponse.setDiscountRate(client.getDiscountRate());

        ClientShop byClientNo = clientShopRepository.findByClientNo(client.getClientNo());
        if(bePresent(byClientNo)){
            Shops shop = byClientNo.getShop();
            ShopInfo shopInfo = shop.getShopInfo();
            fetchClientDetailResponse.setShopId(shop.getShopId());
            fetchClientDetailResponse.setShopName(shopInfo.getName());
        }

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
        addClientRequest.checkValidation();
        if(addClientRequest.getUuid() != null && clientsRepository.countAllByUuid(addClientRequest.getUuid()) > 0) withException("400-020");
        if(addClientRequest.getRegNo() != null && clientsRepository.countAllByRegNum(addClientRequest.getRegNo()) > 0) withException("400-021");

        Clients clients = new Clients();
        clients.setBusinessNo(addClientRequest.getBusinessNo());
        clients.setBusinessType(addClientRequest.getBusinessType());

        clients.setClientId(clientId);
        clients.setGroupNo(group.getGroupNo());
        clients.setDirectType(YesNoTypes.getYesNo(addClientRequest.getDirect()));
        clients.setStatus(AccountStatusTypes.PENDING);
        clients.setEmail(addClientRequest.getEmail());
        clients.setRegNum(addClientRequest.getRegNo());
        clients.setUuid(addClientRequest.getUuid());
        clientsRepository.save(clients);

        ClientInfo clientInfo = addClientRequest.getClientInfo();
        clientInfo.setClientNo(clients.getClientNo());
        clientInfo.setClient(clients);
        clientInfoRepository.save(clientInfo);

        ClientAddresses clientAddresses = new ClientAddresses();
        clientAddresses.setModelAddress(addClientRequest.getAddress());
        clientAddresses.setClientNo(clients.getClientNo());
        clientAddresses.setClient(clients);
        clientAddressesRepository.save(clientAddresses);

        String password = randomPassword(10);
        ClientPassword clientPassword = new ClientPassword();
        clientPassword.updatePasswordWithoutSHA256(password);
        clientPassword.setClientNo(clients.getClientNo());
        clientPassword.setClient(clients);
        clientPasswordRepository.save(clientPassword);

        clients.setClientAddresses(clientAddresses);
        clients.setClientInfo(clientInfo);
        clients.setClientPassword(clientPassword);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_CLIENT_ADDED, session.getUserNo(), clients.getClientNo().toString()));

        Shops shopByShopId = shopWorker.getShopByShopId(addClientRequest.getShopId());
        ClientShop clientShop = new ClientShop();
        clientShop.setClientNo(clients.getClientNo());
        clientShop.setShopNo(shopByShopId.getShopNo());
        clientShopRepository.save(clientShop);

        Map response = new HashMap();
        response.put("password", password);
        request.setResponse(response);

        return request;
    }

    @Transactional
    public BikeSessionRequest updateClient(BikeSessionRequest request){
        Map param = request.getParam();
        UpdateClientRequest updateClientRequest = map(param, UpdateClientRequest.class);
        updateClientRequest.checkValidation();
        ClientInfo requestClientInfo = updateClientRequest.getClientInfo();
        Clients client = clientsRepository.findByClientId(updateClientRequest.getClientId());
        ClientInfo clientInfo = client.getClientInfo();
        ClientAddresses clientAddresses = client.getClientAddresses();
        if(!bePresent(updateClientRequest.getUuid())) withException("400-006");
        ClientGroups clientGroups = groupRepository.findByGroupId(updateClientRequest.getGroupId());
        if(!bePresent(clientGroups)) withException("400-003");
        if(!updateClientRequest.getUuid().equals(client.getUuid())){
            if(clientsRepository.findByUuid(updateClientRequest.getUuid()) != null && !client.getClientId().equals(clientsRepository.findByUuid(updateClientRequest.getUuid()).getClientId()))
                withException("400-007");
        }
        if(!updateClientRequest.getRegNo().equals(client.getRegNum())){
            if(clientsRepository.findByRegNum(updateClientRequest.getRegNo()) != null && !client.getClientId().equals(clientsRepository.findByRegNum(updateClientRequest.getRegNo()).getClientId()))
                withException("400-008");
        }

        if(bePresent(requestClientInfo) && !requestClientInfo.getPhone().equals(clientInfo.getPhone())){
            ClientInfo byPhone = clientInfoRepository.findByPhone(requestClientInfo.getPhone());
            if(bePresent(byPhone)) withException("400-009");
        }

        BikeUser session = request.getSessionUser();
        ClientGroups preGroup = client.getClientGroup();
        client.setClientGroup(clientGroups);
        client.setGroupNo(clientGroups.getGroupNo());

        updateClientUserLog(BikeUserLogTypes.COMM_CLIENT_UPDATED, session.getUserNo(), updateClientRequest, client, clientInfo, preGroup);

        client.setBusinessNo(updateClientRequest.getBusinessNo());
        client.setBusinessType(updateClientRequest.getBusinessType());

        client.setEmail(updateClientRequest.getEmail());
        client.setUuid(updateClientRequest.getUuid());
        client.setDirectType(YesNoTypes.getYesNo(updateClientRequest.getDirect()));
        client.setRegNum(updateClientRequest.getRegNo());
        clientsRepository.save(client);

        clientInfo.setClientNo(client.getClientNo());
        clientInfo.setDescription(requestClientInfo.getDescription());
        clientInfo.setPhone(requestClientInfo.getPhone());
        clientInfo.setName(requestClientInfo.getName());
        clientInfo.setManagerEmail(requestClientInfo.getManagerEmail());
        clientInfo.setManagerName(requestClientInfo.getManagerName());
        clientInfo.setManagerPhone(requestClientInfo.getManagerPhone());

        clientInfo.setRegDate(requestClientInfo.getRegDate());
        clientInfo.setRegBusinessType(requestClientInfo.getRegBusinessType());
        clientInfo.setRegSectorType(requestClientInfo.getRegSectorType());
        clientInfo.setReceiptType(requestClientInfo.getReceiptType());

        clientAddresses.setModelAddress(updateClientRequest.getAddress());
        clientAddresses.setClientNo(client.getClientNo());
        clientInfoRepository.save(clientInfo);
        clientAddressesRepository.save(clientAddresses);

        Shops shopByShopId = shopWorker.getShopByShopId(updateClientRequest.getShopId());
        List<ClientShop> byClientNo = clientShopRepository.findAllByClientNo(client.getClientNo());
        if(bePresent(byClientNo) && byClientNo.size() < 1)
            clientShopRepository.deleteAll(byClientNo);
        ClientShop clientShop = new ClientShop();
        clientShop.setClientNo(client.getClientNo());
        clientShop.setShopNo(shopByShopId.getShopNo());
        clientShopRepository.save(clientShop);

        return request;
    }

    public void updateClientUserLog(BikeUserLogTypes bikeUserLogTypes, Integer fromUserNo, UpdateClientRequest updateClientRequest, Clients clients, ClientInfo clientInfo, ClientGroups preGroup){
        if(bePresent(updateClientRequest)){
            List<String> stringList = new ArrayList<>();
            ClientInfo ci = updateClientRequest.getClientInfo();
            ClientAddresses clientAddresses = clients.getClientAddresses() == null ? new ClientAddresses() : clients.getClientAddresses();
            ModelAddress modelAddress = clientAddresses.getModelAddress() == null ? new ModelAddress() : clientAddresses.getModelAddress();
            ModelAddress address = updateClientRequest.getAddress();

            if(bePresent(updateClientRequest.getBusinessNo()) && !updateClientRequest.getBusinessNo().equals(clients.getBusinessNo())){
                if(bePresent(clients.getBusinessNo())) stringList.add("고객사 <>법인등록번호</>를 <>" + clients.getBusinessNo() + "</>에서 <>" + updateClientRequest.getBusinessNo() + "</>(으)로 변경하였습니다.");
                else stringList.add("고객사 <>법인등록번호</>를 <>" +  updateClientRequest.getBusinessNo() + "</>(으)로 등록하였습니다.");
            }

            if(bePresent(updateClientRequest.getBusinessType()) && !updateClientRequest.getBusinessType().equals(clients.getBusinessType())){
                stringList.add("고객사 <>사업자형태</>를 <>" + clients.getBusinessType().getBusiness() + "</>에서 <>" + updateClientRequest.getBusinessType().getBusiness() + "</>(으)로 변경하였습니다.");
            }

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
//            if(bePresent(updateClientRequest.getDiscountRate()) && !updateClientRequest.getDiscountRate().equals(clients.getDiscountRate())){
//                if(clients.getDiscountRate() == null){
//                    //stringList.add("부품할인가를 <>" + String.format("%.2f", updateClientRequest.getDiscountRate()) + "%</>(으)로 설정하였습니다.");
//                    stringList.add("고객사 할인율을 <>" + String.format("%.1f",updateClientRequest.getDiscountRate()*100) + "%</>(으)로 설정하였습니다.");
//                //}else stringList.add("부품할인가를 <>" + String.format("%.2f", clients.getDiscountRate()) + "%</>에서 <>" + String.format("%.2f", updateClientRequest.getDiscountRate()) + "%</>로 변경하였습니다.");
//                }else stringList.add("고객사 할인율을 <>" + String.format("%.1f",clients.getDiscountRate()*100) + "%</>에서 <>" + String.format("%.1f",updateClientRequest.getDiscountRate()*100) + "%</>로 변경하였습니다.");
//            }
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
                        stringList.add("고객사 대표자 Email을 <>" + ci.getManagerEmail() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 대표자 Email을 <>" + clientInfo.getManagerEmail() + "</>에서 <>" + ci.getManagerEmail() + "</>로 변경하였습니다.");
                }
                if(bePresent(ci.getManagerName()) && !ci.getManagerName().equals(clientInfo.getManagerName())){
                    if(clientInfo.getManagerName() == null){
                        stringList.add("고객사 대표자명을 <>" + ci.getManagerName() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 대표자명을 <>" + clientInfo.getManagerName() + "</>에서 <>" + ci.getManagerName() + "</>로 변경하였습니다.");
                }
                if(bePresent(ci.getManagerPhone()) && !ci.getManagerPhone().equals(clientInfo.getManagerPhone())){
                    if(clientInfo.getManagerPhone() == null){
                        stringList.add("고객사 대표자 연락처를 <>" + ci.getManagerPhone() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 대표자 연락처를 <>" + clientInfo.getManagerPhone() + "</>에서 <>" + ci.getManagerPhone() + "</>로 변경하였습니다.");
                }

                if(bePresent(ci.getRegDate()) && !ci.getRegDate().equals(clientInfo.getRegDate())){
                    if(clientInfo.getRegDate() == null){
                        stringList.add("고객사 사업등록일자를 <>" + ci.getRegDate() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 사업등록일자를 <>" + clientInfo.getRegDate() + "</>에서 <>" + ci.getRegDate() + "</>로 변경하였습니다.");
                }

                if(bePresent(ci.getRegBusinessType()) && !ci.getRegBusinessType().equals(clientInfo.getRegBusinessType())){
                    if(clientInfo.getRegBusinessType() == null){
                        stringList.add("고객사 사업자 업종을 <>" + ci.getRegBusinessType() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 사업자 업종을 <>" + clientInfo.getRegBusinessType() + "</>에서 <>" + ci.getRegBusinessType() + "</>로 변경하였습니다.");
                }

                if(bePresent(ci.getRegSectorType()) && !ci.getRegSectorType().equals(clientInfo.getRegSectorType())){
                    if(clientInfo.getRegSectorType() == null){
                        stringList.add("고객사 사업자 업태을 <>" + ci.getRegSectorType() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 사업자 업태을 <>" + clientInfo.getRegSectorType() + "</>에서 <>" + ci.getRegSectorType() + "</>로 변경하였습니다.");
                }

                if(bePresent(ci.getReceiptType()) && !ci.getReceiptType().equals(clientInfo.getReceiptType())){
                    if(clientInfo.getReceiptType() == null){
                        stringList.add("고객사 청구수령방법을 <>" + ci.getReceiptType().getReceiptName() + "</>로 변경하였습니다.");
                    }else stringList.add("고객사 청구수령방법을 <>" + clientInfo.getReceiptType().getReceiptName() + "</>에서 <>" + ci.getReceiptType().getReceiptName() + "</>로 변경하였습니다.");
                }

                if(bePresent(address) && !address.getAddress().equals(modelAddress.getAddress())){
                    stringList.add("고객사 주소를 변경하였습니다.");
                }
//                if(updateClientRequest.getDiscountRate() != clients.getDiscountRate()){
//                    stringList.add("고객사 할인율을 <>" + clients.getDiscountRate() * 100 + " %</>에서 <>"+ updateClientRequest.getDiscountRate() * 100 +" %</>로 변경하였습니다.");
//                }
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
        clientPassword.updatePasswordWithoutSHA256(newPassword);
        clientPasswordRepository.save(clientPassword);
        response.put("password", newPassword);
        request.setResponse(response);

        return request;
    }


    @Transactional
    public BikeSessionRequest deleteClient(BikeSessionRequest request){
        Map param = request.getParam();
        DeleteClientRequest deleteClientRequest = map(param, DeleteClientRequest.class);
        List<Leases> allByClients_clientId = leaseRepository.findAllByClients_ClientId(deleteClientRequest.getClientId());
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

    public BikeSessionRequest generatePreSignedURLToUploadClientFile(BikeSessionRequest request){
        Map param = request.getParam();
        ClientDto clientDto = map(param, ClientDto.class);
        Clients client = clientsRepository.findByClientId(clientDto.getClientId());
        if(!bePresent(clientDto.getFilename())) withException("");
        String uuid = UUID.randomUUID().toString();
        String filename = clientDto.getFilename().substring(0, clientDto.getFilename().lastIndexOf("."));
        String extension =  clientDto.getFilename().substring(clientDto.getFilename().lastIndexOf(".")+1);
        PresignedURLVo presignedURLVo = new PresignedURLVo();
        presignedURLVo.setBucket(ENV.AWS_S3_QUEUE_BUCKET);
        presignedURLVo.setFileKey("bikes/" + client.getClientId() + "/" + uuid + "/" + filename + "." + extension);
        presignedURLVo.setFilename(filename + "." + extension);
        presignedURLVo.setUrl(AmazonUtils.AWSGeneratePresignedURL(presignedURLVo));
        request.setResponse(presignedURLVo);
        return request;
    }

    @Transactional
    public BikeSessionRequest checkFileUploadComplete(BikeSessionRequest request){
        Map param = request.getParam();
        PresignedURLVo presignedURLVo = map(param, PresignedURLVo.class);
        String clientId = (String) param.get("client_id");
        Clients client = clientsRepository.findByClientId(clientId);
        ClientAttachments clientAttachments = new ClientAttachments();
        clientAttachments.setClientNo(client.getClientNo());
        clientAttachments.setFileName(presignedURLVo.getFilename());
        clientAttachments.setFileKey("/" + presignedURLVo.getFileKey());
        clientAttachments.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
        clientAttachmentRepository.save(clientAttachments);
        // todo: filename required
        AmazonS3 amazonS3 = AmazonUtils.amazonS3();
        CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, presignedURLVo.getFileKey());
        amazonS3.copyObject(objectRequest);
        Map response = new HashMap();
        response.put("url", clientAttachments.getFileKey());
        request.setResponse(response);
        return request;
    }


    public BikeSessionRequest fetchFilesByClient(BikeSessionRequest request){
        Map param = request.getParam();
        List<ClientAttachments> attachments = clientAttachmentRepository.findAllByClient_ClientId((String) param.get("client_id"));
        request.setResponse(attachments);
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteFile(BikeSessionRequest request){
        Map param = request.getParam();
        Integer attachmentNo = Integer.parseInt((String)param.get("client_attachment_no"));
        ClientAttachments byAttachNo = clientAttachmentRepository.findByAttachNo(attachmentNo);
        String url = byAttachNo.getDomain() + byAttachNo.getFileKey();
        clientAttachmentRepository.deleteById(byAttachNo.getAttachNo());
        AmazonS3 amazonS3 = AmazonUtils.amazonS3();
        amazonS3.deleteObject(ENV.AWS_S3_ORIGIN_BUCKET, url);
        return request;
    }




}
