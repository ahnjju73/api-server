package helmet.bikelab.apiserver.services.clients;

import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserLog;
import helmet.bikelab.apiserver.domain.client.ClientGroups;
import helmet.bikelab.apiserver.domain.client.ClientInfo;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.client.GroupAddresses;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import helmet.bikelab.apiserver.domain.types.ManagementTypes;
import helmet.bikelab.apiserver.domain.types.PaymentTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.clients.group.*;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import io.undertow.util.MultipartParser;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@RequiredArgsConstructor
@Service
public class ClientGroupService extends SessService {

   private final ClientsRepository clientsRepository;
   private final ClientGroupRepository groupRepository;
   private final ClientGroupAddressRepository clientGroupAddressRepository;
   private final LeaseRepository leaseRepository;
   private final LeasePaymentsRepository leasePaymentsRepository;
   private final LeaseInfoRepository leaseInfoRepository;
   private final LeasePriceRepository leasePriceRepository;
   private final BikesRepository bikesRepository;
   private final BikeLabUserRepository bikeLabUserRepository;
   private final ReleaseRepository releaseRepository;
   private final InsurancesRepository insurancesRepository;
   private final AutoKey autoKey;
   private final BikeUserLogRepository bikeUserLogRepository;
   private final BikeModelsRepository bikeModelsRepository;
   private final LeaseInsurancesRepository leaseInsurancesRepository;

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

      GroupAddresses groupAddresses = group.getGroupAddresses();
      if(!bePresent(groupAddresses)) {
         groupAddresses = new GroupAddresses();
         groupAddresses.setGroupNo(group.getGroupNo());
      }
      groupAddresses.setModelAddress(addGroupRequest.getAddress());
      clientGroupAddressRepository.save(groupAddresses);

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
         GroupAddresses groupAddresses = group.getGroupAddresses();
         if(!bePresent(groupAddresses)) {
            groupAddresses = new GroupAddresses();
            groupAddresses.setGroupNo(group.getGroupNo());
         }
         groupAddresses.setModelAddress(updateGroupRequest.getAddress());
         clientGroupAddressRepository.save(groupAddresses);
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
      File excel = new File("/Users/joohonga/workspaces/api-server/" +  filePart.filename());
      filePart.transferTo(excel);
      try {
         FileInputStream fis = new FileInputStream(excel);
         XSSFWorkbook workbook = new XSSFWorkbook(fis);
         for(int i = 0; i < workbook.getNumberOfSheets(); i++){
            XSSFSheet sheet = workbook.getSheetAt(i);
            if(sheet.getSheetName().contains("그룹")){
               groupProcess(sheet);
            }else if(sheet.getSheetName().contains("바이크")){
               leaseProcess(sheet, request.getSessionUser());
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

   void leaseProcess(XSSFSheet sheet, BikeUser session){
      int rowIdx;
      int colIdx;
      int rows=sheet.getPhysicalNumberOfRows();
      for(rowIdx=0; rowIdx<rows; rowIdx++) {
         XSSFRow row = sheet.getRow(rowIdx);
         String head = row.getCell(0).toString();
         if(head.matches("\\d+\\.\\d")) {
            int leaseFee = 0;
            Leases lease = new Leases();
            LeaseInfo leaseInfo = new LeaseInfo();
            LeasePrice leasePrice = new LeasePrice();
            String leaseId = autoKey.makeGetKey("lease");
            lease.setLeaseId(leaseId);
            Bikes bike = new Bikes();
            LeaseInsurances leaseInsurances = new LeaseInsurances();
            String bikeNum = "";
            String vimNum = "";
            String color = "";
            String model = "";
            for (colIdx = 1; colIdx < row.getPhysicalNumberOfCells(); colIdx++) {
               XSSFCell cell = row.getCell(colIdx);
               if (cell != null && !"".equals(cell.getRawValue())) {
                  String value = cell.toString();
                  if(colIdx == 1){
                     Clients client = clientsRepository.findByClientInfo_Name(value);
                     lease.setClientNo(client.getClientNo());
                  }
                  if(colIdx == 3){
                     vimNum = value;
                  }
                  if(colIdx == 5){
                     model = value;
                  }
                  if(colIdx == 6){
                     color = value;
                  }
                  if(colIdx == 7){
                     bike = bikesRepository.findByCarNum(value);
                     if(bike != null)
                        lease.setBikeNo(bike.getBikeNo());
                     bikeNum = value;
                  }
                  if(colIdx == 9){
                     int age = (int)Double.parseDouble(value);
                     int insNo = 0;
                     if(age == 21){
                        lease.setInsuranceNo(15);
                        insNo = 15;
                     }else if(age == 24){
                        lease.setInsuranceNo(13);
                        insNo = 13;
                     }else{
                        lease.setInsuranceNo(14);
                        insNo = 14;
                     }
                     Insurances insurance = insurancesRepository.findById(insNo).get();
                     leaseInsurances.setInsurance(insurance);
                  }
                  if(colIdx == 11){
                     DateTimeFormatter dTF =
                             new DateTimeFormatterBuilder().parseCaseInsensitive()
                                     .appendPattern("dd-MMM-yyyy")
                                     .toFormatter();
                     leaseInfo.setContractDate(LocalDate.parse(value, dTF));
                  }
                  if(colIdx == 13){
                     //바로고 테라 무빙 딜리체
//                     String price = value.replaceAll("," , "");
//                     leasePrice.setDeposit((int)Double.parseDouble(price));
                     //만나
                     double price = cell.getNumericCellValue();
                     leasePrice.setDeposit((int) price);
                  }
//                바로고 테라 무빙 딜리 정
//                  if(colIdx == 14){
//                     String price = value.replaceAll("," , "");
//                     leaseFee = (int)Double.parseDouble(price);
//                  }
//                  if(colIdx == 15){
//                     leaseInfo.setNote(value);
//                  }
//                  if(colIdx == 16){
//                     DateTimeFormatter dTF =
//                             new DateTimeFormatterBuilder().parseCaseInsensitive()
//                                     .appendPattern("dd-MMM-yyyy")
//                                     .toFormatter();
//                     leaseInfo.setStart(LocalDate.parse(value, dTF));
//                  }
                  //만나
                  if(colIdx == 15){
                     double price = cell.getNumericCellValue();
                     leaseFee = Math.round((float)price);
                  }
                  if(colIdx == 16){
                     leaseInfo.setNote(value);
                  }
                  if(colIdx == 17){
                     DateTimeFormatter dTF =
                             new DateTimeFormatterBuilder().parseCaseInsensitive()
                                     .appendPattern("dd-MMM-yyyy")
                                     .toFormatter();
                     leaseInfo.setStart(LocalDate.parse(value, dTF));
                  }
               }
            }
            if(bike == null){
               bike = new Bikes();
               bike.setBikeId(autoKey.makeGetKey("bike"));
               bike.setCarModel(bikeModelsRepository.findByCode(model));
               bike.setColor(color);
               bike.setCarNum(bikeNum);
               bike.setVimNum(vimNum);
               bikesRepository.save(bike);
               lease.setBikeNo(bike.getBikeNo());
            }

            lease.setType(ManagementTypes.FINANCIAL);
            lease.setCreatedAt(LocalDateTime.now());
            lease.setReleaseNo(1);
            lease.setCreatedUserNo(session.getUserNo());
            lease.setStatus(LeaseStatusTypes.CONFIRM);
            lease.setSubmittedUserNo(25);
            lease.setApprovalUserNo(26);
            leaseRepository.save(lease);
            leaseInsurances.setLeaseNo(lease.getLeaseNo());
            leaseInsurancesRepository.save(leaseInsurances);
            bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, session.getUserNo(), lease.getLeaseNo().toString(), Arrays.asList("초기 데이터 셋팅에 의해 생성되였습니다.")));
            leaseInfo.setPeriod(12);
            leaseInfo.setEndDate(leaseInfo.getStart().plusMonths(12));
            leaseInfo.setLeaseNo(lease.getLeaseNo());
            leaseInfoRepository.save(leaseInfo);
            leasePrice.setLeaseNo(lease.getLeaseNo());
            leasePrice.setPrepayment(0);
            leasePrice.setProfit(0);
            leasePrice.setTakeFee(0);
            leasePrice.setRegisterFee(0);
            leasePrice.setType(PaymentTypes.DAILY);
            leasePriceRepository.save(leasePrice);

            List<LeasePayments> leasePaymentsList = new ArrayList<>();

            if(leasePrice.getType()==PaymentTypes.MONTHLY) {
               for (int i = 0; i < leaseInfo.getPeriod(); i++) {
                  LeasePayments leasePayment = new LeasePayments();
                  String paymentId = autoKey.makeGetKey("payment");
                  leasePayment.setPaymentId(paymentId);
                  leasePayment.setLeaseNo(lease.getLeaseNo());
                  leasePayment.setIndex(i + 1);
                  leasePayment.setPaymentDate(leaseInfo.getStart().plusMonths(i));
                  leasePayment.setInsertedUserNo(session.getUserNo());
                  leasePayment.setLeaseFee(leaseFee);
                  leasePaymentsList.add(leasePayment);
               }
            }
            else{
               int days = (int) (ChronoUnit.DAYS.between(leaseInfo.getStart(), leaseInfo.getStart().plusMonths(leaseInfo.getPeriod())));
               for (int i = 0; i < days; i++) {
                  LeasePayments leasePayment = new LeasePayments();
                  String paymentId = autoKey.makeGetKey("payment");
                  leasePayment.setPaymentId(paymentId);
                  leasePayment.setLeaseNo(lease.getLeaseNo());
                  leasePayment.setIndex(i + 1);
                  leasePayment.setPaymentDate(leaseInfo.getStart().plusDays(i));
                  leasePayment.setInsertedUserNo(session.getUserNo());
                  leasePayment.setLeaseFee(leaseFee);
                  leasePaymentsList.add(leasePayment);
               }
            }
            leasePaymentsRepository.saveAll(leasePaymentsList);
         }
      }
   }
}
