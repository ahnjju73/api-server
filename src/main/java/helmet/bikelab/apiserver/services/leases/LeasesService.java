package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.fine.FetchFinesResponse;
import helmet.bikelab.apiserver.objects.bikelabs.leases.*;
import helmet.bikelab.apiserver.objects.bikelabs.release.ReleaseDto;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.InsuranceDto;
import helmet.bikelab.apiserver.objects.requests.LeasesRequestListDto;
import helmet.bikelab.apiserver.objects.requests.StopLeaseDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.BikeUserTodoService;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Utils;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@RequiredArgsConstructor
@Service
public class LeasesService extends SessService {

    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final LeaseInfoRepository leaseInfoRepository;
    private final LeasePriceRepository leasePriceRepository;
    private final BikesRepository bikesRepository;
    private final BikeLabUserRepository bikeLabUserRepository;
    private final ClientsRepository clientsRepository;
    private final ReleaseRepository releaseRepository;
    private final InsurancesRepository insurancesRepository;
    private final LeaseExtraRepository leaseExtraRepository;
    private final LeaseExpenseRepository expenseRepository;
    private final AutoKey autoKey;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final BikeUserTodoService bikeUserTodoService;
    private final CommonWorker commonWorker;
    private final LeaseInsurancesRepository leaseInsurancesRepository;

    public BikeSessionRequest fetchLeases(BikeSessionRequest request){
        Map param = request.getParam();
        LeasesRequestListDto requestListDto = map(param, LeasesRequestListDto.class);
        ResponseListDto responseListDto;
        if(bePresent(requestListDto.getClientId())){
            Clients byClientId = clientsRepository.findByClientId(requestListDto.getClientId());
            requestListDto.setSearchClientNo(byClientId.getClientNo());
        }
        if(!bePresent(requestListDto.getSearchBike())){
            responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "leases.leases-manager.fetchLeases", "leases.leases-manager.countAllLeases", "lease_id");
        }else {
            responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "leases.leases-manager.fetchLeasesByBike", "leases.leases-manager.countAllLeasesByBike", "lease_id");
        }

        request.setResponse(responseListDto);
        return request;
    }

    @Deprecated
    public BikeSessionRequest bak_fetchLeases(BikeSessionRequest request){
        Map response = new HashMap();
        List<Leases> leases = leaseRepository.findAll();
        List<FetchLeasesResponse> fetchLeasesResponses = new ArrayList<>();
        for(Leases lease : leases){
            FetchLeasesResponse fetchLeasesResponse = new FetchLeasesResponse();
            fetchLeasesResponse.setLeaseId(lease.getLeaseId());
            fetchLeasesResponse.setStatus(lease.getStatus().getStatus());
            if(lease.getBike() != null) {
                fetchLeasesResponse.setBikeId(lease.getBike().getBikeId());
                BikeDto bikeDto = new BikeDto();
                bikeDto.setBikeNum(lease.getBike().getCarNum());
                bikeDto.setBikeId(lease.getBike().getBikeId());
                bikeDto.setBikeModel(lease.getBike().getCarModel().getModel());
                fetchLeasesResponse.setBike(bikeDto);
            }
            if(lease.getClients() != null) {
                fetchLeasesResponse.setClientId(lease.getClients().getClientId());
                ClientDto clientDto = new ClientDto();
                clientDto.setClientId(lease.getClients().getClientId());
                clientDto.setClientName(lease.getClients().getClientInfo().getName());
                fetchLeasesResponse.setClient(clientDto);
            }
            if(lease.getLeasePrice() != null){
                LeasePriceDto leasePriceDto = new LeasePriceDto();
                leasePriceDto.setLeasePrice(lease.getLeasePrice());
                fetchLeasesResponse.setLeasePrice(leasePriceDto);
            }
            if(lease.getLeaseInfo() != null){
                LeaseInfoDto leaseInfoDto = new LeaseInfoDto();
                leaseInfoDto.setLeaseInfo(lease.getLeaseInfo());
                List<LeasePayments> leasePaymentsList = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
                leaseInfoDto.setPeriod(lease.getLeaseInfo().getPeriod());
                fetchLeasesResponse.setLeaseInfo(leaseInfoDto);
            }
            if(lease.getInsurances() != null){
                fetchLeasesResponse.setInsuranceId(lease.getInsurances().getInsuranceId());
                InsuranceDto insuranceDto = new InsuranceDto();
                insuranceDto.setInsurance(lease.getInsurances());
                fetchLeasesResponse.setInsurance(insuranceDto);
            }
            if(lease.getReleases() != null) {
                fetchLeasesResponse.setReleaseId(lease.getReleases().getReleaseId());
                ReleaseDto releaseDto = new ReleaseDto();
                releaseDto.setReleaseName(lease.getReleases().getReleaseName());
                releaseDto.setUseYn(lease.getReleases().getYesNoTypes().getYesNo());
                releaseDto.setCreatedAt(lease.getReleases().getCreatedAt());
                releaseDto.setReleaseAddress(lease.getReleases().getAddress().getAddress());
                fetchLeasesResponse.setRelease(releaseDto);
            }
            if(bePresent(lease.getCreatedUser())) {
                UserDto createdUser = new UserDto();
                createdUser.setUserId(lease.getCreatedUser().getUserId());
                createdUser.setEmail(lease.getCreatedUser().getEmail());
                createdUser.setName(lease.getCreatedUser().getBikeUserInfo().getName());
                fetchLeasesResponse.setCreatedUser(createdUser);
            }
            if(bePresent(lease.getSubmittedUser())) {
                UserDto submittedUser = new UserDto();
                submittedUser.setUserId(lease.getSubmittedUser().getUserId());
                submittedUser.setEmail(lease.getSubmittedUser().getEmail());
                submittedUser.setName(lease.getSubmittedUser().getBikeUserInfo().getName());
                fetchLeasesResponse.setSubmittedUser(submittedUser);
            }
            if(bePresent(lease.getApprovalUser())) {
                UserDto approvalUser = new UserDto();
                approvalUser.setUserId(lease.getApprovalUser().getUserId());
                approvalUser.setEmail(lease.getApprovalUser().getEmail());
                approvalUser.setName(lease.getApprovalUser().getBikeUserInfo().getName());
                fetchLeasesResponse.setApprovalUser(approvalUser);
            }
            fetchLeasesResponse.setStatus(lease.getStatus().getStatus());
            fetchLeasesResponse.setManagementType(lease.getType().getStatus());
            fetchLeasesResponse.setContractType(lease.getContractTypes().getStatus());
            fetchLeasesResponses.add(fetchLeasesResponse);
        }
        response.put("leases",fetchLeasesResponses);
        request.setResponse(response);
        return  request;
    }

    public BikeSessionRequest fetchDetailLease(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
        List<LeaseExpense> leaseExpenses = expenseRepository.findAllByLease_LeaseId(lease.getLeaseId());

        if(lease == null) withException("850-002");
        List<FetchFinesResponse> fines = new ArrayList<>();
        FetchLeasesResponse fetchLeasesResponse = new FetchLeasesResponse();
        fetchLeasesResponse.setFines(fines);
        fetchLeasesResponse.setLeaseId(lease.getLeaseId());
        fetchLeasesResponse.setStatus(lease.getStatus().getStatus());
        fetchLeasesResponse.setManagementType(lease.getType().getStatus());
        fetchLeasesResponse.setContractType(lease.getContractTypes().getStatus());
        fetchLeasesResponse.setTakeLoc(lease.getTakeLocation());
        fetchLeasesResponse.setTakeAt(lease.getTakeAt());
        fetchLeasesResponse.setCreatedAt(lease.getCreatedAt());
        fetchLeasesResponse.setReleaseAt(lease.getReleaseAt());
        StopLeaseDto stopLeaseDto = new StopLeaseDto();
        stopLeaseDto.setLeaseStopStatus(lease.getLeaseStopStatus()==null? LeaseStopStatusTypes.CONTINUE.getStatus(): lease.getLeaseStopStatus().getStatus());
        stopLeaseDto.setStopDt(lease.getStopDt() == null ? "" : lease.getStopDt().toString());
        stopLeaseDto.setLeaseId(lease.getLeaseId());
        stopLeaseDto.setStopFee(lease.getStopFee() == null? 0 : lease.getStopFee());
        stopLeaseDto.setStopReason(lease.getStopReason() == null ? "" : lease.getStopReason());
        stopLeaseDto.setStopPaidFee(lease.getStopPaidFee() == null ? 0 : lease.getStopPaidFee());
        fetchLeasesResponse.setStopLeaseInfo(stopLeaseDto);


        if(leaseExpenses != null && leaseExpenses.size() > 0){
            List<ExpenseDto> expenseDtos = new ArrayList<>();
            for(LeaseExpense le:leaseExpenses){
                ExpenseDto expenseDto = new ExpenseDto();
                expenseDto.setExpenseType(le.getExpenseTypes().getType());
                expenseDto.setNumber(le.getNumber());
                expenseDto.setDescription(le.getDescription());
                if(le.getTransaction() != null){
                    expenseDto.setRegNum(le.getTransaction().getRegNum());
                    expenseDto.setCompanyName(le.getTransaction().getCompanyName());
                    expenseDto.setPrice(le.getTransaction().getPrice());
                }
                expenseDtos.add(expenseDto);
            }
            fetchLeasesResponse.setExpense(expenseDtos);
        }
        if(lease.getBike()!=null) {
            fetchLeasesResponse.setBikeId(lease.getBike().getBikeId());
            BikeDto bikeDto = new BikeDto();
            bikeDto.setBikeId(lease.getBike().getBikeId());
            bikeDto.setBikeModel(lease.getBike().getCarModel().getModel());
            bikeDto.setBikeNum(lease.getBike().getCarNum());
            bikeDto.setVimNum(lease.getBike().getVimNum());
            fetchLeasesResponse.setBike(bikeDto);
        }
        if(lease.getClients()!=null) {
            fetchLeasesResponse.setClientId(lease.getClients().getClientId());
            ClientDto clientDto = new ClientDto();
            clientDto.setClientId(lease.getClients().getClientId());
            clientDto.setClientName(lease.getClients().getClientInfo().getName());
            fetchLeasesResponse.setClient(clientDto);
        }
        if(lease.getLeasePrice() != null){
            LeasePriceDto leasePriceDto = new LeasePriceDto();
            leasePriceDto.setLeasePrice(lease.getLeasePrice());
            fetchLeasesResponse.setLeasePrice(leasePriceDto);
        }
        if(lease.getLeaseInfo()!=null){
            LeaseInfoDto leaseInfoDto = new LeaseInfoDto();
            leaseInfoDto.setLeaseInfo(lease.getLeaseInfo());
            leaseInfoDto.setPeriod(lease.getLeaseInfo().getPeriod());
            leaseInfoDto.setEndDt(lease.getLeaseInfo().getEndDate().toString());
            fetchLeasesResponse.setLeaseInfo(leaseInfoDto);
        }
        if(lease.getInsurances()!=null){
            fetchLeasesResponse.setInsuranceId(lease.getInsurances().getInsuranceId());
            InsuranceDto insuranceDto = new InsuranceDto();
            insuranceDto.setInsurance(lease.getInsurances());
            fetchLeasesResponse.setInsurance(insuranceDto);
        }
        if(lease.getReleases()!=null) {
            fetchLeasesResponse.setReleaseId(lease.getReleases().getReleaseId());
            ReleaseDto releaseDto = new ReleaseDto();
            releaseDto.setReleaseName(lease.getReleases().getReleaseName());
            releaseDto.setUseYn(lease.getReleases().getYesNoTypes().getYesNo());
            releaseDto.setCreatedAt(lease.getReleases().getCreatedAt());
            releaseDto.setReleaseAddress(lease.getReleases().getAddress().getAddress());
        }
        List<LeasePaymentDto> leasePayments = new ArrayList<>();
        int totalFee = 0;
        for(LeasePayments lp : payments){
            LeasePaymentDto leasePaymentDto = new LeasePaymentDto();
            leasePaymentDto.setLeaseFee(lp.getLeaseFee());
            leasePaymentDto.setPaymentId(lp.getPaymentId());
            leasePaymentDto.setPaymentDate(lp.getPaymentDate());
            leasePaymentDto.setPaidFee(lp.getPaidFee());
            leasePaymentDto.setIdx(lp.getIndex());
            totalFee += lp.getLeaseFee();
            leasePayments.add(leasePaymentDto);
        }
        if(bePresent(lease.getCreatedUser())) {
            UserDto createdUser = new UserDto();
            createdUser.setUserId(lease.getCreatedUser().getUserId());
            createdUser.setEmail(lease.getCreatedUser().getEmail());
            createdUser.setName(lease.getCreatedUser().getBikeUserInfo().getName());
            fetchLeasesResponse.setCreatedUser(createdUser);
        }
        if(bePresent(lease.getSubmittedUser())) {
            UserDto submittedUser = new UserDto();
            submittedUser.setUserId(lease.getSubmittedUser().getUserId());
            submittedUser.setEmail(lease.getSubmittedUser().getEmail());
            submittedUser.setName(lease.getSubmittedUser().getBikeUserInfo().getName());
            fetchLeasesResponse.setSubmittedUser(submittedUser);
        }
        if(bePresent(lease.getApprovalUser())) {
            UserDto approvalUser = new UserDto();
            approvalUser.setUserId(lease.getApprovalUser().getUserId());
            approvalUser.setEmail(lease.getApprovalUser().getEmail());
            approvalUser.setName(lease.getApprovalUser().getBikeUserInfo().getName());
            fetchLeasesResponse.setApprovalUser(approvalUser);
        }
        fetchLeasesResponse.getLeasePrice().setLeaseFee(totalFee);
        fetchLeasesResponse.setLeasePayments(leasePayments);
        response.put("lease", fetchLeasesResponse);
        request.setResponse(response);
        return request;
    }


    @Transactional
    public BikeSessionRequest addLease(BikeSessionRequest request){
        Map param = request.getParam();
        AddUpdateLeaseRequest addUpdateLeaseRequest = map(param, AddUpdateLeaseRequest.class);
        BikeUser session = request.getSessionUser();
        addUpdateLeaseRequest.validationCheck();
        Leases lease = new Leases();
        String leaseId = autoKey.makeGetKey("lease");
        lease.setLeaseId(leaseId);
        List<Leases> leasesByBike = leaseRepository.findAllByBike_BikeId(addUpdateLeaseRequest.getBikeId());
        //bike
        Bikes bike = bikesRepository.findByBikeId(addUpdateLeaseRequest.getBikeId());
//        if(bike.getCarNum() == null) withException("850-011");
        if(bike!=null && leasesByBike.size() > 0) withException("850-001"); //이미 리스가 존재할때
        if(bike.getTransaction() == null) withException("850-034");
        //clientÎ
        Clients client = clientsRepository.findByClientId(addUpdateLeaseRequest.getClientId());
        //insurance
        Insurances insurance = insurancesRepository.findByInsuranceId(addUpdateLeaseRequest.getInsuranceId());
        if(client!=null)
            lease.setClientNo(client.getClientNo());
        if(bike!=null)
            lease.setBikeNo(bike.getBikeNo());
        if(insurance!=null)
            lease.setInsuranceNo(insurance.getInsuranceNo());
        if(addUpdateLeaseRequest.getManagementType() != null)
            lease.setType(ManagementTypes.getManagementStatus(addUpdateLeaseRequest.getManagementType()));
        lease.setContractTypes(ContractTypes.OPERATING);
        lease.setCreatedAt(LocalDateTime.now());
        lease.setReleaseNo(1);
        lease.setCreatedUserNo(session.getUserNo());
        leaseRepository.save(lease);

        //lease info
        LeaseInfoDto leaseInfoDto = addUpdateLeaseRequest.getLeaseInfo();
        LeaseInfo leaseInfo = new LeaseInfo();
        leaseInfo.setLeaseNo(lease.getLeaseNo());
        List<LeasePayments> leasePaymentsList = new ArrayList<>();
        if(leaseInfoDto.getStartDt()!=null) {
            leaseInfo.setStart(LocalDate.parse(leaseInfoDto.getStartDt()));//payment시작
            if(leaseInfoDto.getPeriod() != null) {
                leaseInfo.setPeriod(leaseInfoDto.getPeriod());
                leaseInfo.setEndDate(leaseInfo.getStart().plusMonths(leaseInfoDto.getPeriod()));
                if(PaymentTypes.getPaymentType(addUpdateLeaseRequest.getLeasePrice().getPaymentType()) == PaymentTypes.MONTHLY) {
                    for (int i = 0; i < addUpdateLeaseRequest.getLeaseInfo().getPeriod(); i++) {
                        LeasePayments leasePayment = new LeasePayments();
                        String paymentId = autoKey.makeGetKey("payment");
                        leasePayment.setPaymentId(paymentId);
                        leasePayment.setLeaseNo(lease.getLeaseNo());
                        leasePayment.setIndex(i + 1);
                        leasePayment.setPaymentDate(leaseInfo.getStart().plusMonths(i));
                        leasePayment.setInsertedUserNo(session.getUserNo());
                        leasePayment.setLeaseFee(addUpdateLeaseRequest.getLeasePrice().getLeaseFee());
                        leasePaymentsList.add(leasePayment);
                    }
                }else{
                    int days = (int)(ChronoUnit.DAYS.between(leaseInfo.getStart(), leaseInfo.getStart().plusMonths(addUpdateLeaseRequest.getLeaseInfo().getPeriod())));
                    for(int i = 0 ; i < days; i++){
                        LeasePayments leasePayment = new LeasePayments();
                        String paymentId = autoKey.makeGetKey("payment");
                        leasePayment.setPaymentId(paymentId);
                        leasePayment.setLeaseNo(lease.getLeaseNo());
                        leasePayment.setIndex(i + 1);
                        leasePayment.setPaymentDate(leaseInfo.getStart().plusDays(i));
                        leasePayment.setInsertedUserNo(session.getUserNo());
                        leasePayment.setLeaseFee(addUpdateLeaseRequest.getLeasePrice().getLeaseFee());
                        leasePaymentsList.add(leasePayment);
                    }
                }
            }
        }
        leaseInfo.setContractDate(LocalDate.parse(leaseInfoDto.getContractDt()));
        leaseInfo.setNote(leaseInfoDto.getNote());
        leaseInfoRepository.save(leaseInfo);

        //lease expense-bike
        LeaseExpense expenseBike = new LeaseExpense();
        expenseBike.setTransaction(bike.getTransaction());
        expenseBike.setExpenseTypes(ExpenseTypes.BIKE);
        expenseBike.setLeaseNo(lease.getLeaseNo());
        expenseBike.setNumber(1);
        expenseBike.setDescription("");
        //lease expense -bike registration
        LeaseExpense expenseReg = new LeaseExpense();
        ModelTransaction modelTransaction = new ModelTransaction();
        modelTransaction.setPrice(bike.getTransaction().getPrice() / 50);
        modelTransaction.setRegNum("-");
        modelTransaction.setCompanyName("-");
        expenseReg.setTransaction(modelTransaction);
        expenseReg.setExpenseTypes(ExpenseTypes.REGISTER);
        expenseReg.setLeaseNo(lease.getLeaseNo());
        expenseReg.setNumber(1);
        expenseReg.setDescription("");
        expenseRepository.save(expenseBike);
        expenseRepository.save(expenseReg);

        LeasePrice leasePrice = new LeasePrice();
        leasePrice.setLeaseNo(lease.getLeaseNo());
//        leasePrice.setProfit(addUpdateLeaseRequest.getLeasePrice().getProfitFee());
        leasePrice.setType(PaymentTypes.getPaymentType(addUpdateLeaseRequest.getLeasePrice().getPaymentType()));
//        leasePrice.setRegisterFee(addUpdateLeaseRequest.getLeasePrice().getRegisterFee() != null ? addUpdateLeaseRequest.getLeasePrice().getRegisterFee() : 0);
//        leasePrice.setTakeFee(addUpdateLeaseRequest.getLeasePrice().getTakeFee() == null ? 0 : addUpdateLeaseRequest.getLeasePrice().getTakeFee());
        if(addUpdateLeaseRequest.getLeasePrice().getPrePayment()!= null)
            leasePrice.setPrepayment(addUpdateLeaseRequest.getLeasePrice().getPrePayment());
//        leasePrice.setTakeFee(addUpdateLeaseRequest.getLeasePrice().getTakeFee() != null ? addUpdateLeaseRequest.getLeasePrice().getTakeFee() : 0);
//        leasePrice.setDeposit(addUpdateLeaseRequest.getLeasePrice().getDeposit() != null ? addUpdateLeaseRequest.getLeasePrice().getDeposit() : 0);
        leasePriceRepository.save(leasePrice);
        leasePaymentsRepository.saveAll(leasePaymentsList);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_ADDED, session.getUserNo(), lease.getLeaseNo().toString()));
        Map response = new HashMap();
        response.put("lease_id", leaseId);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateLease(BikeSessionRequest request){
        Map param = request.getParam();
        AddUpdateLeaseRequest addUpdateLeaseRequest = map(param, AddUpdateLeaseRequest.class);
        Leases lease = leaseRepository.findByLeaseId(addUpdateLeaseRequest.getLeaseId());
        LeaseInsurances leaseInsurances = new LeaseInsurances();
        List<Leases> leasesByBike = leaseRepository.findAllByBike_BikeId(addUpdateLeaseRequest.getBikeId());
        addUpdateLeaseRequest.validationCheck();
        if(addUpdateLeaseRequest.getLeasePrice().getPrePayment() == null) withException("850-025");
        if(addUpdateLeaseRequest.getLeasePrice().getDeposit() == null) withException("850-026");
        if(addUpdateLeaseRequest.getLeasePrice().getProfitFee() == null) withException("850-027");
        if(addUpdateLeaseRequest.getLeasePrice().getTakeFee() == null) withException("850-028");
        if(addUpdateLeaseRequest.getLeasePrice().getRegisterFee() == null) withException("850-029");
        if(lease.getStatus() == LeaseStatusTypes.PENDING || !lease.getLeaseStopStatus().equals(LeaseStopStatusTypes.CONTINUE)) withException("850-004");
        if(lease.getStatus() == LeaseStatusTypes.CONFIRM){
            String log = "";
            Bikes bike = bikesRepository.findByBikeId(addUpdateLeaseRequest.getBikeId());
            if (bike != null && leasesByBike.size() > 0 && !lease.equals(leasesByBike.get(0)))
                withException("850-003"); //이미 리스가 존재할때
            if(bike.getCarNum() == null) withException("850-024");
            if(bike.getTransaction() == null) withException("850-034");
            ModelTransaction modelTransaction = bike.getTransaction();
            List<LeaseExpense> expenses = expenseRepository.findAllByLease_LeaseIdAndExpenseTypes(lease.getLeaseId(), ExpenseTypes.BIKE);
            LeaseExpense leaseExpense;
            if (expenses.size() > 0) {
                leaseExpense = expenses.get(0);
                leaseExpense.setTransaction(modelTransaction);
                expenseRepository.save(leaseExpense);
            } else {
                leaseExpense = new LeaseExpense();
                leaseExpense.setLeaseNo(lease.getLeaseNo());
                leaseExpense.setExpenseTypes(ExpenseTypes.BIKE);
                leaseExpense.setTransaction(modelTransaction);
                leaseExpense.setNumber(1);
                expenseRepository.save(leaseExpense);
            }
            List<LeaseExpense> expenseList = expenseRepository.findAllByLease_LeaseIdAndExpenseTypes(lease.getLeaseId(), ExpenseTypes.REGISTER);
            if (expenseList.size() > 0) {
                leaseExpense = expenseList.get(0);
                ModelTransaction transaction = new ModelTransaction();
                transaction.setPrice(bike.getTransaction().getPrice() == null ? null : getRegistrationFee(modelTransaction.getPrice()));
                transaction.setRegNum("-");
                transaction.setCompanyName("-");
                leaseExpense.setTransaction(transaction);
                expenseRepository.save(leaseExpense);
            } else {
                LeaseExpense expenseReg = new LeaseExpense();
                expenseReg.setLeaseNo(lease.getLeaseNo());
                expenseReg.setExpenseTypes(ExpenseTypes.REGISTER);
                ModelTransaction transaction = new ModelTransaction();
                transaction.setPrice(bike.getTransaction().getPrice() == null ? null : getRegistrationFee(modelTransaction.getPrice()));
                transaction.setRegNum("-");
                transaction.setCompanyName("-");
                expenseReg.setTransaction(transaction);
                expenseReg.setNumber(1);
                expenseRepository.save(expenseReg);
            }
            Clients client = clientsRepository.findByClientId(addUpdateLeaseRequest.getClientId());
            Insurances insurance = insurancesRepository.findByInsuranceId(addUpdateLeaseRequest.getInsuranceId());
            leaseInsurances.setInsurance(insurance);
            List<String> logList = new ArrayList<>();
            if(insurance.getInsuranceNo() != lease.getInsuranceNo()){
                logList.add("보험을 <>" + insurance.getCompanyName() + " " + insurance.getAge() + " [" + insurance.getInsuranceName() + " ]" + "</>에서 <>" + insurance.getCompanyName() + " " + insurance.getAge() + " [" +  insurance.getInsuranceName() + "] " + "</>으로 변경하였습니다.");
            }
            if(!lease.getLeaseInfo().getNote().equals(addUpdateLeaseRequest.getLeaseInfo().getNote())){
                if(lease.getLeaseInfo().getNote() == null)
                    logList.add("노트 내용을 <>" + addUpdateLeaseRequest.getLeaseInfo().getNote() + "</>로 설정하였습니다.");
                else
                    logList.add("노트 내용을 <>" +  lease.getLeaseInfo().getNote() + "</>에서 <>" + addUpdateLeaseRequest.getLeaseInfo().getNote() + "</>으로 변경하였습니다.");
            }
            if(!lease.getClientNo().equals(client.getClientNo())){
                log = "바이크 번호 <>" + bike.getCarNum() + "</>을 <>" + lease.getClients().getClientInfo().getName() + "</>에서 <>"+ client.getClientInfo().getName() + "</>로 이전하였습니다.";
                logList.add("바이크 번호 <>" + bike.getCarNum() + "</>을 <>" + lease.getClients().getClientInfo().getName() + "</>에서 <>"+ client.getClientInfo().getName() + "</>로 이전하였습니다.");
            }
            LeaseInfo leaseInfo = lease.getLeaseInfo();
            lease.setClientNo(client.getClientNo());
            lease.setBikeNo(bike.getBikeNo());
            lease.setInsuranceNo(insurance.getInsuranceNo());
            leaseInfo.setNote(addUpdateLeaseRequest.getLeaseInfo().getNote());
            leaseRepository.save(lease);
            leaseInsurances.setLeaseNo(lease.getLeaseNo());
            leaseInsurancesRepository.save(leaseInsurances);
            leaseInfoRepository.save(leaseInfo);
            bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), logList));
            if(!log.equals(""))
                bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_BIKE_UPDATED, request.getSessionUser().getUserNo(), lease.getBikeNo().toString(), log));

        }
        else {
            //bike
            Bikes bike = bikesRepository.findByBikeId(addUpdateLeaseRequest.getBikeId());
            if (bike != null && leasesByBike.size() > 0 && !lease.equals(leasesByBike.get(0))) withException("850-003"); //이미 리스가 존재할때
            if(bike.getTransaction() == null) withException("850-034");
            ModelTransaction modelTransaction = bike.getTransaction();
            List<LeaseExpense> expenses = expenseRepository.findAllByLease_LeaseIdAndExpenseTypes(lease.getLeaseId(), ExpenseTypes.BIKE);
            LeaseExpense leaseExpense;
            if (expenses.size() > 0) {
                leaseExpense = expenses.get(0);
                leaseExpense.setTransaction(modelTransaction);
                expenseRepository.save(leaseExpense);
            } else {
                leaseExpense = new LeaseExpense();
                leaseExpense.setLeaseNo(lease.getLeaseNo());
                leaseExpense.setExpenseTypes(ExpenseTypes.BIKE);
                leaseExpense.setTransaction(modelTransaction);
                leaseExpense.setNumber(1);
                expenseRepository.save(leaseExpense);
            }
            List<LeaseExpense> expenseList = expenseRepository.findAllByLease_LeaseIdAndExpenseTypes(lease.getLeaseId(), ExpenseTypes.REGISTER);
            if (expenseList.size() > 0) {
                leaseExpense = expenseList.get(0);
                ModelTransaction transaction = new ModelTransaction();
                transaction.setPrice(bike.getTransaction().getPrice() == null ? null : getRegistrationFee(modelTransaction.getPrice()));
                transaction.setRegNum("-");
                transaction.setCompanyName("-");
                leaseExpense.setTransaction(transaction);
                expenseRepository.save(leaseExpense);
            } else {
                LeaseExpense expenseReg = new LeaseExpense();
                expenseReg.setLeaseNo(lease.getLeaseNo());
                expenseReg.setExpenseTypes(ExpenseTypes.REGISTER);
                ModelTransaction transaction = new ModelTransaction();
                transaction.setPrice(bike.getTransaction().getPrice() == null ? null : getRegistrationFee(modelTransaction.getPrice()));
                transaction.setRegNum("-");
                transaction.setCompanyName("-");
                expenseReg.setTransaction(transaction);
                expenseReg.setNumber(1);
                expenseRepository.save(expenseReg);
            }

            //client
            Clients client = clientsRepository.findByClientId(addUpdateLeaseRequest.getClientId());
            //insurance
            Insurances insurance = insurancesRepository.findByInsuranceId(addUpdateLeaseRequest.getInsuranceId());
            //release
            Releases release = releaseRepository.findByReleaseId(addUpdateLeaseRequest.getReleaseId());
            LeaseInfoDto leaseInfoDto = addUpdateLeaseRequest.getLeaseInfo();
            LeaseInfo leaseInfo = lease.getLeaseInfo();
            PaymentTypes paymentType = leasePriceRepository.findByLease_LeaseId(lease.getLeaseId()).getType();
            LeasePriceDto leasePriceDto = addUpdateLeaseRequest.getLeasePrice();
            LeasePrice leasePrice = lease.getLeasePrice();
            List<LeasePayments> leasePaymentsList = leasePaymentsRepository.findAllByLease_LeaseId(addUpdateLeaseRequest.getLeaseId());
            List<LeasePaymentDto> dtosList = addUpdateLeaseRequest.getLeasePayments();
            updateLeaseInfoLog(request.getSessionUser(), addUpdateLeaseRequest, client, insurance, bike, lease);
            leaseInsurances.setInsurance(insurance);
            leaseInfo.setPeriod(addUpdateLeaseRequest.getLeaseInfo().getPeriod());
            if (client != null)
                lease.setClientNo(client.getClientNo());
            if (bike != null)
                lease.setBikeNo(bike.getBikeNo());
            if (release != null)
                lease.setReleaseNo(release.getReleaseNo());
            if (insurance != null)
                lease.setInsuranceNo(insurance.getInsuranceNo());
            if (addUpdateLeaseRequest.getManagementType() != null)
                lease.setType(ManagementTypes.getManagementStatus(addUpdateLeaseRequest.getManagementType()));
            if (addUpdateLeaseRequest.getTakeLoc() != null)
                lease.setTakeLocation(addUpdateLeaseRequest.getTakeLoc());
            if (addUpdateLeaseRequest.getTakeAt() != null)
                lease.setTakeAt(addUpdateLeaseRequest.getTakeAt());
            if (addUpdateLeaseRequest.getReleaseAt() != null)
                lease.setReleaseAt(addUpdateLeaseRequest.getReleaseAt());
            lease.setUpLesase(addUpdateLeaseRequest.getUpLeaseNo());
            leaseRepository.save(lease);
            leaseInsurances.setLeaseNo(lease.getLeaseNo());
            leaseInsurancesRepository.save(leaseInsurances);
            //leaseInfo.setLeaseNo(lease.getLeaseNo());
            if (leaseInfoDto.getStartDt() != null) {
                leaseInfo.setStart(LocalDate.parse(leaseInfoDto.getStartDt()));
                leaseInfo.setEndDate(leaseInfo.getStart().plusMonths(addUpdateLeaseRequest.getLeasePayments().size()));
            }
            leaseInfo.setContractDate(LocalDate.parse(leaseInfoDto.getContractDt()));
            leaseInfo.setNote(leaseInfoDto.getNote());
            leaseInfoRepository.save(leaseInfo);

            //lease price
            leasePrice.setLeaseNo(lease.getLeaseNo());
            leasePrice.setType(PaymentTypes.getPaymentType(leasePriceDto.getPaymentType()));
            leasePrice.setDeposit(leasePriceDto.getDeposit());
            if (leasePriceDto.getPrePayment() != null)
                leasePrice.setPrepayment(leasePriceDto.getPrePayment());
            leasePrice.setProfit(leasePriceDto.getProfitFee());
            leasePrice.setTakeFee(leasePriceDto.getTakeFee());
            leasePrice.setRegisterFee(leasePriceDto.getRegisterFee());
            leasePriceRepository.save(leasePrice);

            BikeUser session = request.getSessionUser();
            List<LeasePayments> newPaymentList = new ArrayList<>();
            if (leasePrice.getType() != paymentType) {
                if (leasePrice.getType() == PaymentTypes.MONTHLY) {
                    for (int i = 0; i < addUpdateLeaseRequest.getLeaseInfo().getPeriod(); i++) {
                        LeasePayments leasePayment = new LeasePayments();
                        String paymentId = autoKey.makeGetKey("payment");
                        leasePayment.setPaymentId(paymentId);
                        leasePayment.setLeaseNo(lease.getLeaseNo());
                        leasePayment.setIndex(i + 1);
                        leasePayment.setPaymentDate(leaseInfo.getStart().plusMonths(i));
                        leasePayment.setInsertedUserNo(session.getUserNo());
                        leasePayment.setLeaseFee(addUpdateLeaseRequest.getLeasePrice().getLeaseFee());
                        newPaymentList.add(leasePayment);
                    }
                } else {
                    int days = (int) (ChronoUnit.DAYS.between(leaseInfo.getStart(), leaseInfo.getStart().plusMonths(addUpdateLeaseRequest.getLeaseInfo().getPeriod())));
                    for (int i = 0; i < days; i++) {
                        LeasePayments leasePayment = new LeasePayments();
                        String paymentId = autoKey.makeGetKey("payment");
                        leasePayment.setPaymentId(paymentId);
                        leasePayment.setLeaseNo(lease.getLeaseNo());
                        leasePayment.setIndex(i + 1);
                        leasePayment.setPaymentDate(leaseInfo.getStart().plusDays(i));
                        leasePayment.setInsertedUserNo(session.getUserNo());
                        leasePayment.setLeaseFee(addUpdateLeaseRequest.getLeasePrice().getLeaseFee());
                        newPaymentList.add(leasePayment);
                    }
                }
                for (LeasePayments lp : leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId())) {
                    List<LeaseExtras> extrasList = leaseExtraRepository.findAllByPayment_PaymentId(lp.getPaymentId());
                    if (!extrasList.isEmpty()) {
                        leaseExtraRepository.deleteAll(extrasList);
                    }
                }
                leasePaymentsRepository.deleteAll(leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId()));
                leasePaymentsRepository.saveAll(newPaymentList);
            }else{
                for(int i = 0; i < leasePaymentsList.size(); i++){
                    if(!leasePaymentsList.get(i).getLeaseFee().equals(dtosList.get(i).getLeaseFee())){
                        leasePaymentsList.get(i).setLeaseFee(dtosList.get(i).getLeaseFee());
                    }
                    if(!leasePaymentsList.get(i).getPaidFee().equals(dtosList.get(i).getPaidFee())){
                        leasePaymentsList.get(i).setPaidFee(dtosList.get(i).getPaidFee());
                    }
                }
                leasePaymentsRepository.saveAll(leasePaymentsList);
            }
        }
        return request;
    }

    @Transactional
    public void updateLeaseInfoLog(BikeUser session, AddUpdateLeaseRequest leaseRequest, Clients clientRequested, Insurances insurancesRequested, Bikes bikeRequested, Leases leases){
        List<String> stringList = new ArrayList<>();
        boolean isSet = true;
        if(bePresent(leaseRequest)){
            if(bePresent(clientRequested) && !clientRequested.getClientNo().equals(leases.getClientNo())){
                Clients clients = leases.getClients();
                if(clients == null){
                    stringList.add("고객정보 <>" + clientRequested.getClientInfo().getName() + " [" + clientRequested.getClientId() + "] " + "</>로 설정하였습니다.");
                }else{
                    stringList.add("고객정보를 <>" + clients.getClientInfo().getName() + " [" + clients.getClientId() + "] " + "</>에서 <>" + clientRequested.getClientInfo().getName() + " [" + clientRequested.getClientId() + "] " + "</>으로 변경하였습니다.");
                }
            }
            if(bePresent(bikeRequested) && !bikeRequested.getBikeNo().equals(leases.getBikeNo())){
                Bikes bike = leases.getBike();
                if(bike == null)
                    stringList.add("바이크 정보 <>" + bikeRequested.getCarNum() + " [" +  bikeRequested.getBikeId() + "] " + "</>로 설정하였습니다.");
                else
                    stringList.add("바이크 정보를 <>" + bike.getCarNum() + " [" + bike.getBikeId() + " ]" + "</>에서 <>" + bikeRequested.getCarNum() + " [" +  bikeRequested.getBikeId() + "] " + "</>으로 변경하였습니다.");
            }
            if(bePresent(insurancesRequested) && !insurancesRequested.getInsuranceNo().equals(leases.getInsuranceNo())){
                Insurances insurance = leases.getInsurances();
                if(insurance == null)
                    stringList.add("보험을 <>" + insurancesRequested.getCompanyName() + " " + insurancesRequested.getAge() + " [" +  insurancesRequested.getInsuranceId() + "] " + "</>로 설정하였습니다.");
                else
                    stringList.add("보험을 <>" + insurance.getCompanyName() + " " + insurance.getAge() + " [" + insurance.getInsuranceId() + " ]" + "</>에서 <>" + insurancesRequested.getCompanyName() + " " + insurancesRequested.getAge() + " [" +  insurancesRequested.getInsuranceId() + "] " + "</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getLeasePrice().getPaymentType()) && !leaseRequest.getLeasePrice().getPaymentType().equals(leases.getLeasePrice().getType().getPaymentType())){
                LeasePayments first = leasePaymentsRepository.findFirstByLease_LeaseId(leases.getLeaseId());
                if(leases.getLeasePrice().getType() == PaymentTypes.DAILY) {
                    stringList.add("리스 납부 방법을 <> 일차감 </>에서 <> 월차감 </>으로 변경하였습니다.");
                    if(bePresent(leaseRequest.getLeaseInfo().getPeriod()) && !leaseRequest.getLeaseInfo().getPeriod().equals(leases.getLeaseInfo().getPeriod())){
                        stringList.add("리스 계약기간을 <>" +  getDiffMonths(leases.getLeaseInfo().getStart(), leases.getLeaseInfo().getEndDate()) + " 일</>에서 <>" + leaseRequest.getLeaseInfo().getPeriod() + " 개월</>로 변경하였습니다.");
                    }
//                    if(bePresent(leaseRequest.getLeasePrice().getLeaseFee()) && !leaseRequest.getLeasePrice().getLeaseFee().equals(first.getLeaseFee()*365)){
//                        stringList.add("리스료를 <>" +  Utils.getCurrencyFormat(first.getLeaseFee()) + " 원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getLeaseFee()) + " 원</>로 변경하였습니다.");
//                    }
                }
                else {
                    stringList.add("리스 납부 방법을 <> 월차감 </>에서 <> 일차감 </>으로 변경하였습니다.");
                    if(bePresent(leaseRequest.getLeaseInfo().getPeriod()) && !leaseRequest.getLeaseInfo().getPeriod().equals(leases.getLeaseInfo().getPeriod())) {
                        stringList.add("리스 계약기간을 <>" + getDiffMonths(leases.getLeaseInfo().getStart(), leases.getLeaseInfo().getEndDate()) + " 개월</>에서 <>" + leaseRequest.getLeaseInfo().getPeriod() + " 일</>로 변경하였습니다.");
                    }
//                    if(bePresent(leaseRequest.getLeasePrice().getLeaseFee()) && !leaseRequest.getLeasePrice().getLeaseFee().equals(first.getLeaseFee()*12)){
//                        stringList.add("리스료를 <>" +  Utils.getCurrencyFormat(first.getLeaseFee()) + " 원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getLeaseFee()) + " 원</>로 변경하였습니다.");
//                    }
                }
            }
            if(bePresent(leaseRequest.getManagementType()) && !leaseRequest.getManagementType().equals(leases.getType().getStatus())){
                stringList.add("운용 형태룰 <>" + leases.getType() + "</>에서 <>" + ManagementTypes.getManagementStatus(leaseRequest.getManagementType()) + "</>으로 변경하였습니다.");
            }

            if(bePresent(leaseRequest.getLeaseInfo().getContractDt()) && !LocalDate.parse(leaseRequest.getLeaseInfo().getContractDt()).equals(leases.getLeaseInfo().getContractDate())){
                stringList.add("리스 시작 날짜를 <>" +  leases.getLeaseInfo().getContractDate() + "</>에서 <>" + LocalDate.parse(leaseRequest.getLeaseInfo().getContractDt()) + "</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getLeaseInfo().getStartDt()) && !LocalDate.parse(leaseRequest.getLeaseInfo().getStartDt()).equals(leases.getLeaseInfo().getStart())){
                stringList.add("리스 첫 납부일을 <>" +  leases.getLeaseInfo().getStart() + "</>에서 <>" + LocalDate.parse(leaseRequest.getLeaseInfo().getStartDt()) + "</>으로 변경하였습니다.");
            }

            if(bePresent(leaseRequest.getLeaseInfo().getNote()) && !leaseRequest.getLeaseInfo().getNote().equals(leases.getLeaseInfo().getNote())){
                if(leases.getLeaseInfo().getNote() == null)
                    stringList.add("노트 내용을 <>" + leaseRequest.getLeaseInfo().getNote() + "</>로 설정하였습니다.");
                else
                    stringList.add("노트 내용을 <>" +  leases.getLeaseInfo().getNote() + "</>에서 <>" + leaseRequest.getLeaseInfo().getNote() + "</>으로 변경하였습니다.");
            }

            if(bePresent(leaseRequest.getLeasePrice().getPrePayment()) && !leaseRequest.getLeasePrice().getPrePayment().equals(leases.getLeasePrice().getPrepayment())){
                stringList.add("리스 선입금을 <>" + Utils.getCurrencyFormat(leases.getLeasePrice().getPrepayment()) + "원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getPrePayment()) + "원</>으로 변경하였습니다.");

            }
            if(bePresent(leaseRequest.getLeasePrice().getDeposit()) && !leaseRequest.getLeasePrice().getDeposit().equals(leases.getLeasePrice().getDeposit())){
                stringList.add("리스 보증금을 <>" + Utils.getCurrencyFormat(leases.getLeasePrice().getDeposit()) + "원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getDeposit()) + "원</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getLeasePrice().getProfitFee()) && !leaseRequest.getLeasePrice().getProfitFee().equals(leases.getLeasePrice().getProfit())){
                stringList.add("리스 수익금을 <>" + Utils.getCurrencyFormat(leases.getLeasePrice().getProfit()) + "원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getProfitFee()) + "원</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getLeasePrice().getTakeFee()) && !leaseRequest.getLeasePrice().getTakeFee().equals(leases.getLeasePrice().getTakeFee())){
                stringList.add("리스 인수비를 <>" + Utils.getCurrencyFormat(leases.getLeasePrice().getTakeFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getTakeFee()) + "원</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getLeasePrice().getRegisterFee()) && !leaseRequest.getLeasePrice().getRegisterFee().equals(leases.getLeasePrice().getRegisterFee())){
                stringList.add("리스 등록비를 <>" + Utils.getCurrencyFormat(leases.getLeasePrice().getRegisterFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getRegisterFee()) + "</>으로 변경하였습니다.");
            }
            bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, session.getUserNo(), leases.getLeaseNo().toString(), stringList));
        }

    }

    private int getDiffMonths(LocalDate start, LocalDate end){
        return (end.getYear()-start.getYear())*12 + end.getMonthValue()-start.getMonthValue();
    }

    @Transactional
    public BikeSessionRequest confirmLease (BikeSessionRequest request){
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        BikeUser session = request.getSessionUser();
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        if(!lease.getApprovalUser().getUserId().equals(session.getUserId())) withException("850-021");
        if(!lease.getStatus().getStatus().equals("550-002")) withException("850-009");
        lease.setStatus(LeaseStatusTypes.CONFIRM);
        lease.setApprovalDt(LocalDateTime.now());
        leaseRepository.save(lease);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_APPROVE_COMPLETED, session.getUserNo(), lease.getLeaseNo().toString()));
        bikeUserTodoService.addTodo(BikeUserTodoTypes.LEASE_CONFIRM, session.getUserNo(), lease.getSubmittedUserNo(), lease.getLeaseNo().toString(), lease.getLeaseId());
        return request;
    }


    @Transactional
    public BikeSessionRequest pendingLease (BikeSessionRequest request){
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        BikeUser session = request.getSessionUser();
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        LeaseInsurances leaseInsurances = new LeaseInsurances();
        leaseInsurances.setInsurance(lease.getInsurances());
        lease.setSubmittedUserNo(session.getUserNo());
        if(!LeaseStatusTypes.IN_PROGRESS.equals(lease.getStatus()) && !LeaseStatusTypes.DECLINE.equals(lease.getStatus())) withException("850-008");
        BikeUser byUserId = bikeLabUserRepository.findByUserId(leasesDto.getApprovalUserId());
        lease.setApprovalUserNo(byUserId.getUserNo());
        LeaseInfo leaseInfo = lease.getLeaseInfo();
        LeasePrice leasePrice = lease.getLeasePrice();
        if(!bePresent(lease.getClientNo())||!bePresent(lease.getReleaseNo())||!bePresent(lease.getBikeNo())||!bePresent(lease.getInsuranceNo())) withException("850-005");
        if(!bePresent(leaseInfo.getStart())) withException("850-006");
        if(!bePresent(leasePrice.getDeposit())||!bePresent(leasePrice.getPrepayment())||!bePresent(leasePrice.getProfit())||!bePresent(leasePrice.getTakeFee())||!bePresent(leasePrice.getRegisterFee())) withException("850-007");
        if(leasesDto.getApprovalUserId()==null) withException("850-020");
        lease.setStatus(LeaseStatusTypes.PENDING);
        leaseRepository.save(lease);
        leaseInsurances.setLeaseNo(lease.getLeaseNo());
        leaseInsurancesRepository.save(leaseInsurances);
        bikeUserTodoService.addTodo(BikeUserTodoTypes.LEASE_APPROVAL, session.getUserNo(), byUserId.getUserNo(), lease.getLeaseNo().toString(), lease.getLeaseId());
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_APPROVE_REQUESTED, session.getUserNo(), lease.getLeaseNo().toString()));
        return request;
    }

    @Transactional
    public BikeSessionRequest rejectLease(BikeSessionRequest request){
        Map param = request.getParam();
        BikeUser session = request.getSessionUser();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        if(!lease.getApprovalUser().getUserId().equals(session.getUserId())) withException("850-032");
        if(lease.getStatus() != LeaseStatusTypes.PENDING) withException("850-023");
        lease.setStatus(LeaseStatusTypes.DECLINE);
        leaseRepository.save(lease);
        bikeUserTodoService.addTodo(BikeUserTodoTypes.LEASE_REJECT, session.getUserNo(), lease.getSubmittedUserNo(), lease.getLeaseNo().toString(), lease.getLeaseId());
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_APPROVE_REJECTED, session.getUserNo(), lease.getLeaseNo().toString()));
        return request;
    }

    @Transactional
    public BikeSessionRequest cancelLease(BikeSessionRequest request){
        Map param = request.getParam();
        BikeUser session = request.getSessionUser();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        if(!lease.getSubmittedUser().getUserId().equals(session.getUserId())) withException("850-033");
        if(lease.getStatus() != LeaseStatusTypes.PENDING) withException("850-031");
        lease.setStatus(LeaseStatusTypes.IN_PROGRESS);
        String log = "<>" + session.getBikeUserInfo().getName() + "님</>께서 해당 리스 신청을 취소하였습니다.";
        leaseRepository.save(lease);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, session.getUserNo(), lease.getLeaseNo().toString(), log));
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteLease(BikeSessionRequest request){
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        if(lease.getStatus() != LeaseStatusTypes.IN_PROGRESS && lease.getStatus() != LeaseStatusTypes.DECLINE) withException("850-022");
        leaseExtraRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        leaseInfoRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        leasePaymentsRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        leasePriceRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        leaseInsurancesRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        expenseRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        leaseRepository.delete(lease);
        return request;
    }

    @Transactional
    public BikeSessionRequest fetchStopLeaseFee(BikeSessionRequest request){
        Map param = request.getParam();
        StopLeaseDto stopLeaseDto = map(param, StopLeaseDto.class);
        Leases lease = leaseRepository.findByLeaseId(stopLeaseDto.getLeaseId());
        LocalDate stopDate = LocalDate.parse(stopLeaseDto.getStopDt());
        double stopFee;
        double totalFee = 0;
        for(LeasePayments lp : leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId())){
            totalFee += lp.getLeaseFee();
        }

        int days = (int) (ChronoUnit.DAYS.between(stopDate, lease.getLeaseInfo().getStart().plusMonths(lease.getLeaseInfo().getPeriod())));
        if(days > 180){
            stopFee = (double) days/365 * totalFee * 0.2;
        }else if(days > 30){
            stopFee = (double) days/365 * totalFee * 0.4;
        }else{
            stopFee = (double) days/365 * totalFee * 0.6;
        }
        stopFee = Math.round(stopFee);
        Map response = new HashMap();
        response.put("stop_fee", stopFee);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest stopLease(BikeSessionRequest request){
        Map param = request.getParam();
        String log ="";
        StopLeaseDto stopLeaseDto = map(param, StopLeaseDto.class);
        double stopFee = stopLeaseDto.getStopFee();
        Leases lease = leaseRepository.findByLeaseId(stopLeaseDto.getLeaseId());
        if(lease == null || lease.getStatus() != LeaseStatusTypes.CONFIRM) withException("");
        Bikes emptyBike = bikesRepository.findByBikeId("BK000000");
        lease.setBakBikeNo(lease.getBike().getBikeNo());
        lease.setBikeNo(emptyBike.getBikeNo());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        LocalDate stopDate = LocalDate.parse(stopLeaseDto.getStopDt());
        String formattedString = stopDate.format(formatter);
        log = "바이크 번호 <>" + lease.getBike().getCarNum() + "</>가 중도해지 되었습니다.<br>";
        log += "중도 해지 위약금은 <>" + Utils.getCurrencyFormat(Math.round(stopFee)) + "원</>으로 설정 되었습니다.<br>" + "중도 해지 일자는 <>"
                + formattedString + "</>로 설정 되었습니다.<br>" + "중도 해지 이유는 <>" + stopLeaseDto.getStopReason() + "</>입니다.";
        lease.setLeaseStopStatus(LeaseStopStatusTypes.STOP_CONTINUE);
        lease.setStopDt(LocalDate.parse(stopLeaseDto.getStopDt()).atStartOfDay());
        lease.setStopFee(Math.round(stopFee));
        lease.setStopPaidFee(0L);
        lease.setStopReason(stopLeaseDto.getStopReason());
        leaseRepository.save(lease);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), log));
        return request;
    }

    @Transactional
    public BikeSessionRequest updateStopLease(BikeSessionRequest request){
        Map param = request.getParam();
        String log ="";
        StopLeaseDto stopLeaseDto = map(param, StopLeaseDto.class);
        Leases lease = leaseRepository.findByLeaseId(stopLeaseDto.getLeaseId());
        if(lease == null || !lease.getLeaseStopStatus().equals(LeaseStopStatusTypes.STOP_CONTINUE)) withException("");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String formattedString = LocalDate.parse(stopLeaseDto.getStopDt()).format(formatter);
        if(!LocalDate.parse(stopLeaseDto.getStopDt()).isEqual(lease.getStopDt().toLocalDate())){
            log += "중도 해지 일자가 <>" + lease.getStopDt().toLocalDate().format(formatter) + "</>에서 <>" + formattedString + "</>로 수정 되었습니다.<br>";
            lease.setStopDt(LocalDate.parse(stopLeaseDto.getStopDt()).atStartOfDay());
        }
        if(stopLeaseDto.getStopFee() != lease.getStopFee()){
            log += "중도 해지 위약금이 <>" + Utils.getCurrencyFormat(lease.getStopFee().intValue()) + "원</>에서 <>" + Utils.getCurrencyFormat(stopLeaseDto.getStopFee()) + "원</>으로 수정 되었습니다.<br>";
            lease.setStopFee(stopLeaseDto.getStopFee());
        }
        if(!stopLeaseDto.getStopReason().equals(lease.getStopReason())){
            log += "중도 해지 이유가 <>" + lease.getStopReason() + "</>에서 <>" + stopLeaseDto.getStopReason() + "</>로 변경 되었습니다.<br>";
            lease.setStopReason(stopLeaseDto.getStopReason());
        }
        if(lease.getStopPaidFee() != stopLeaseDto.getStopPaidFee()){
            log += "중도 해지 위약금 납부 금액이 <>" + Utils.getCurrencyFormat(lease.getStopFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(stopLeaseDto.getStopFee()) + "원</>으로 수정 되었습니다.<br>";
            lease.setStopPaidFee(stopLeaseDto.getStopPaidFee());
        }
        leaseRepository.save(lease);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), log.endsWith("<br>")? log.substring(0, log.length()-4) : log));
        return request;
    }

    private Integer getRegistrationFee(Integer bikePrice){
        //취등록세 공식 적용 2%
        return bikePrice/50;
    }

}
