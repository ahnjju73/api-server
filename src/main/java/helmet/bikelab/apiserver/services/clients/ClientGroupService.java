package helmet.bikelab.apiserver.services.clients;

import helmet.bikelab.apiserver.domain.client.ClientGroups;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.clients.group.*;
import helmet.bikelab.apiserver.repositories.ClientGroupRepository;
import helmet.bikelab.apiserver.repositories.ClientInfoRepository;
import helmet.bikelab.apiserver.repositories.ClientsRepository;
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
public class ClientGroupService extends SessService {

   private final ClientsRepository clientsRepository;
   private final ClientGroupRepository groupRepository;
   private final ClientInfoRepository infoRepository;
   private final AutoKey autoKey;

   public BikeSessionRequest fetchListOfGroup(BikeSessionRequest request){
      Map response = new HashMap();
      List<ClientGroups> groups = groupRepository.findAll();
      response.put("group", groups);
      request.setResponse(response);
      return request;
   }

   @Transactional
   public BikeSessionRequest addNewGroup(BikeSessionRequest request){
      Map param = request.getParam();
      AddGroupRequest addGroupRequest = map(param, AddGroupRequest.class);
      addGroupRequest.checkValidation();
      String groupId = autoKey.makeGetKey("group");
      ClientGroups group = new ClientGroups();
      group.setGroupId(groupId);
      group.setGroupName(addGroupRequest.getGroupName());
      groupRepository.save(group);
      return request;
   }

   public BikeSessionRequest fetchClientsByGroup(BikeSessionRequest request){
      Map param = request.getParam();
      Map response = new HashMap();
      FetchClientsByGroupRequest fetchClientsByGroupRequest = map(param, FetchClientsByGroupRequest.class);
      fetchClientsByGroupRequest.checkValidation();
      List<Clients> clientList = clientsRepository.findByClientGroup_GroupId(fetchClientsByGroupRequest.getGroupId());
      List<FetchClientsByGroupResponse> responseList = new ArrayList<>();
      for(Clients client : clientList){
         FetchClientsByGroupResponse temp = new FetchClientsByGroupResponse();
         temp.setClientId(client.getClientId());
         temp.setClientName(client.getClientInfo().getName());
         responseList.add(temp);
      }
      response.put("clients", responseList);
      request.setResponse(response);
      return request;
   }

   @Transactional
   public BikeSessionRequest updateGroupInfo(BikeSessionRequest request){
      Map param = request.getParam();
      UpdateGroupRequest updateGroupRequest = map(param, UpdateGroupRequest.class);
      updateGroupRequest.checkValidation();
      ClientGroups group = groupRepository.findByGroupId(updateGroupRequest.getGroupId());
      group.setGroupName(updateGroupRequest.getGroupName());
      groupRepository.save(group);
      return request;

   }

   public BikeSessionRequest deleteGroup (BikeSessionRequest request){
      Map param = request.getParam();
      DeleteGroupRequest deleteGroupRequest = map(param, DeleteGroupRequest.class);
      deleteGroupRequest.checkValidation();
      ClientGroups group = groupRepository.findByGroupId(deleteGroupRequest.getGroupId());
      groupRepository.delete(group);
      return request;
   }


}
