package helmet.bikelab.apiserver.services.clients;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserLog;
import helmet.bikelab.apiserver.domain.client.ClientGroups;
import helmet.bikelab.apiserver.domain.client.ClientInfo;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.clients.group.*;
import helmet.bikelab.apiserver.repositories.BikeUserLogRepository;
import helmet.bikelab.apiserver.repositories.ClientGroupRepository;
import helmet.bikelab.apiserver.repositories.ClientsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import io.undertow.util.MultipartParser;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@RequiredArgsConstructor
@Service
public class ClientGroupService extends SessService {

   private final ClientsRepository clientsRepository;
   private final ClientGroupRepository groupRepository;
   private final AutoKey autoKey;
   private final BikeUserLogRepository bikeUserLogRepository;

   public BikeSessionRequest fetchListOfGroup(BikeSessionRequest request){
      Map response = new HashMap();
      List<Map> groups = getList("bikelabs.commons.clients.fetchGroupList", request.getParam());
      response.put("group", groups);
      request.setResponse(response);
      return request;
   }

   @Transactional
   public BikeSessionRequest addNewGroup(BikeSessionRequest request){
      Map param = request.getParam();
      BikeUser session = request.getSessionUser();
      AddGroupRequest addGroupRequest = map(param, AddGroupRequest.class);
      addGroupRequest.checkValidation();
      String groupId = autoKey.makeGetKey("group");
      ClientGroups group = new ClientGroups();
      group.setGroupId(groupId);
      group.setGroupName(addGroupRequest.getGroupName());
      group.setCeoEmail(addGroupRequest.getCeoEmail());
      group.setCeoName(addGroupRequest.getCeoName());
      group.setCeoPhone(addGroupRequest.getCeoPhone());
      group.setRegNum(addGroupRequest.getRegNo());
      groupRepository.save(group);

      BikeUserLog userLog = new BikeUserLog();
      userLog.setLogType(BikeUserLogTypes.COMM_GROUP_ADDED);
      userLog.setFromUserNo(session.getUserNo());
      userLog.setReferenceId(group.getGroupNo().toString());
      bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_GROUP_ADDED, session.getUserNo(), group.getGroupNo().toString()));

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
         temp.setEmail(client.getEmail());
         temp.setPhone(client.getClientInfo().getPhone());
         responseList.add(temp);
      }
      response.put("clients", responseList);
      request.setResponse(response);
      return request;
   }

   @Transactional
   public BikeSessionRequest updateGroupInfo(BikeSessionRequest request){
      Map param = request.getParam();
      BikeUser session = request.getSessionUser();
      UpdateGroupRequest updateGroupRequest = map(param, UpdateGroupRequest.class);
      updateGroupRequest.checkValidation();
      ClientGroups group = groupRepository.findByGroupId(updateGroupRequest.getGroupId());
      if(bePresent(group)){
         addLogGroupInfo(updateGroupRequest, group, session);
         group.setGroupName(updateGroupRequest.getGroupName());
         group.setCeoEmail(updateGroupRequest.getCeoEmail());
         group.setCeoName(updateGroupRequest.getCeoName());
         group.setCeoPhone(updateGroupRequest.getCeoPhone());
         group.setRegNum(updateGroupRequest.getRegNo());
         groupRepository.save(group);
      }
      return request;
   }

   public void addLogGroupInfo(UpdateGroupRequest updateGroupRequest, ClientGroups group, BikeUser session){
      List<String> stringList = new ArrayList<>();
      if(bePresent(updateGroupRequest.getGroupName()) && !updateGroupRequest.getGroupName().equals(group.getGroupName())){
         stringList.add("그룹명을 <>" + group.getGroupName() + "</>에서 <>" + updateGroupRequest.getGroupName() + "</>으로 변경하였습니다.");
      }
      if(bePresent(updateGroupRequest.getCeoEmail()) && !updateGroupRequest.getCeoEmail().equals(group.getCeoEmail())){
         stringList.add("담당자 이메일을 <>" + group.getCeoEmail() + "</>에서 <>" + updateGroupRequest.getCeoEmail() + "</>으로 변경하였습니다.");
      }
      if(bePresent(updateGroupRequest.getCeoName()) && !updateGroupRequest.getCeoName().equals(group.getCeoName())){
         stringList.add("담당자 이름을 <>" + group.getCeoName() + "</>에서 <>" + updateGroupRequest.getCeoName() + "</>으로 변경하였습니다.");
      }
      if(bePresent(updateGroupRequest.getCeoPhone()) && !updateGroupRequest.getCeoPhone().equals(group.getCeoPhone())){
         stringList.add("담당자 연락처를 <>" + group.getCeoPhone() + "</>에서 <>" + updateGroupRequest.getCeoPhone() + "</>으로 변경하였습니다.");
      }
      if(bePresent(updateGroupRequest.getRegNo()) && !updateGroupRequest.getRegNo().equals(group.getRegNum())){
         stringList.add("그룹 사업자번호를 <>" + group.getRegNum() + "</>에서 <>" + updateGroupRequest.getRegNo() + "</>으로 변경하였습니다.");
      }
      if(bePresent(stringList) && stringList.size() > 0)
         bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_GROUP_UPDATED, session.getUserNo(), group.getGroupNo().toString(), stringList));
   }

   @Transactional
   public BikeSessionRequest deleteGroup (BikeSessionRequest request){
      Map param = request.getParam();
      DeleteGroupRequest deleteGroupRequest = map(param, DeleteGroupRequest.class);
      deleteGroupRequest.checkValidation();
      Integer count = clientsRepository.countAllByClientGroup_GroupId(deleteGroupRequest.getGroupId());
      if(count > 0) withException("300-006");
      ClientGroups group = groupRepository.findByGroupId(deleteGroupRequest.getGroupId());
      groupRepository.delete(group);
      return request;
   }

   @Transactional
   public BikeSessionRequest forceDeleteGroup (BikeSessionRequest request){
      Map param = request.getParam();
      DeleteGroupRequest deleteGroupRequest = map(param, DeleteGroupRequest.class);
      deleteGroupRequest.checkValidation();


      return request;
   }

   @Transactional
   public BikeSessionRequest uploadExcel (BikeSessionRequest request){
      FilePart filePart = (FilePart) request.getParam().get("test");
      File excel = new File("/Users/joohonga/workspaces/api-server/" + filePart.filename());
      filePart.transferTo(excel);
      try {
         FileInputStream fis = new FileInputStream(excel);
         XSSFWorkbook workbook = new XSSFWorkbook(fis);
         for(int i = 0; i < workbook.getNumberOfSheets(); i++){
            XSSFSheet sheet = workbook.getSheetAt(i);
            if(sheet.getSheetName().contains("그룹")){
               groupProcess(sheet);
            }else if(sheet.getSheetName().contains("고객")){

            }
         }



      }catch (Exception e){
         e.printStackTrace();
      }finally {
         if(excel != null && excel.exists()) {
            excel.deleteOnExit();
         }
      }
      return request;
   }


   @Transactional
   void groupProcess(XSSFSheet sheet){
      int rowIdx;
      int colIdx;
      int rows=sheet.getPhysicalNumberOfRows();
      for(rowIdx=0; rowIdx<rows; rowIdx++) {
         XSSFRow row = sheet.getRow(rowIdx);
         String head = row.getCell(0).toString();
         if(head.matches("\\d+\\.\\d")) {
            ClientGroups groups = new ClientGroups();
            String groupId = autoKey.makeGetKey("group");
            groups.setGroupId(groupId);
            for (colIdx = 1; colIdx < row.getPhysicalNumberOfCells(); colIdx++) {
               XSSFCell cell = row.getCell(colIdx);
               if (cell != null && !cell.getStringCellValue().equals("")) {
                  String value = cell.toString();
                  if(colIdx == 1){
                     groups.setGroupName(value);
                  }
                  if(colIdx == 2){
                     groups.setRegNum(value);
                  }
                  if(colIdx == 3){
                     groups.setCeoName(value);
                  }
                  if(colIdx == 4){
                     groups.setCeoEmail(value);
                  }
                  if(colIdx == 5){
                     groups.setCeoPhone(value);
                  }
               }
            }
            groupRepository.save(groups);
         }
      }
   }
//   @Transactional
//   void clientProcess(XSSFSheet sheet){
//      int rowIdx;
//      int colIdx;
//      int rows=sheet.getPhysicalNumberOfRows();
//      for(rowIdx=0; rowIdx<rows; rowIdx++) {
//         XSSFRow row = sheet.getRow(rowIdx);
//         String head = row.getCell(0).toString();
//         String sheetName = sheet.getSheetName();
//         String groupName = sheetName.split("_")[1];
//         ClientGroups group = groupRepository.findByGroupId();
//
//         if(head.matches("\\d+\\.\\d")) {
//            Clients clients = new Clients();
//            ClientInfo clientInfo = new ClientInfo();
//            String clientId = autoKey.makeGetKey("client");
//            groups.setGroupId(clientId);
//            for (colIdx = 1; colIdx < row.getPhysicalNumberOfCells(); colIdx++) {
//               XSSFCell cell = row.getCell(colIdx);
//               if (cell != null && !cell.getStringCellValue().equals("")) {
//                  String value = cell.toString();
//                  if(colIdx == 1){
//                     groups.setGroupName(value);
//                  }
//                  if(colIdx == 2){
//                     groups.setRegNum(value);
//                  }
//                  if(colIdx == 3){
//                     groups.setCeoName(value);
//                  }
//                  if(colIdx == 4){
//                     groups.setCeoEmail(value);
//                  }
//                  if(colIdx == 5){
//                     groups.setCeoPhone(value);
//                  }
//               }
//            }
//            groupRepository.save(groups);
//         }
//      }
//   }

}
