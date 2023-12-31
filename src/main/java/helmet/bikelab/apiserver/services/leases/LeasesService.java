package helmet.bikelab.apiserver.services.leases;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.bike.BikeRidersBak;
import helmet.bikelab.apiserver.domain.demands.DemandLeases;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.riders.*;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.bikelabs.fine.FetchFinesResponse;
import helmet.bikelab.apiserver.objects.bikelabs.leases.*;
import helmet.bikelab.apiserver.objects.bikelabs.release.ReleaseDto;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.InsuranceDto;
import helmet.bikelab.apiserver.objects.requests.AddLeaseAttachmentRequest;
import helmet.bikelab.apiserver.objects.requests.LeaseByIdRequest;
import helmet.bikelab.apiserver.objects.requests.LeasesRequestListDto;
import helmet.bikelab.apiserver.objects.requests.StopLeaseDto;
import helmet.bikelab.apiserver.objects.responses.BikeInsuranceListResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.BikeUserTodoService;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.PushComponent;
import helmet.bikelab.apiserver.utils.Utils;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.LeasePaymentWorker;
import helmet.bikelab.apiserver.workers.LeasesWorker;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@RequiredArgsConstructor
@Service
public class LeasesService extends SessService {

    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final LeaseInfoRepository leaseInfoRepository;
    private final LeasePriceRepository leasePriceRepository;
    private final LeaseAttachmentRepository leaseAttachmentRepository;
    private final BikesRepository bikesRepository;
    private final BikeRiderBakRepository bikeRiderBakRepository;
    private final LeaseExpenseRepository leaseExpenseRepository;
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
    private final DemandLeasesRepository demandLeasesRepository;
    private final RiderDemandLeaseRepository riderDemandLeaseRepository;
    private final RiderDemandLeaseHistoryRepository riderDemandLeaseHistoryRepository;
    private final RiderDemandLeaseAttachmentsRepository riderDemandLeaseAttachmentsRepository;
    private final RiderDemandLeaseTermsRepository riderDemandLeaseTermsRepository;
    private final RiderRepository riderRepository;
    private final PushComponent pushComponent;
    private final ActivitiesRepository activitiesRepository;
    private final LeaseExtensionsRepository leaseExtensionsRepository;
    private final SystemParameterRepository systemParameterRepository;
    private final LeasePaymentWorker leasePaymentWorker;
    private final LeasesWorker leasesWorker;
    private final BikeWorker bikeWorker;

    public BikeSessionRequest fetchCompaniesByLease(BikeSessionRequest request){
        Map param = request.getParam();
        String query = (String)param.get("q");
        List companies = getList("comm.common.fetchCompaniesByLease", param);
        request.setResponse(companies);
        return request;
    }

    public BikeSessionRequest fetchLeases(BikeSessionRequest request){
        Map param = request.getParam();
        LeasesRequestListDto requestListDto = map(param, LeasesRequestListDto.class);
        ResponseListDto responseListDto;
        if(bePresent(requestListDto.getClientId())){
            Clients byClientId = clientsRepository.findByClientId(requestListDto.getClientId());
            requestListDto.setSearchClientNo(byClientId.getClientNo());
        }
        // todo: Insurance 정보 제거하기
        if(!bePresent(requestListDto.getSearchBike())){
            responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "leases.leases-manager.fetchLeases", "leases.leases-manager.countAllLeases", "lease_id");
        }else {
            responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "leases.leases-manager.fetchLeasesByBike", "leases.leases-manager.countAllLeasesByBike", "lease_id");
        }

        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchLeaseInsuranceByLeaseId(BikeSessionRequest request){
        LeaseByIdRequest leaseByIdRequest = map(request.getParam(), LeaseByIdRequest.class);
        Leases leaseByLeaseId = leasesWorker.getLeaseByLeaseId(leaseByIdRequest.getLeaseId());
        Bikes bike = leaseByLeaseId.getBike();
        if(bePresent(bike)){
            BikeInsuranceListResponse bikeInsuranceListResponse = bikeWorker.getBikeInsuranceListByBikeId(bike);
            request.setResponse(bikeInsuranceListResponse);
        }
        return request;
    }

    public BikeSessionRequest fetchDetailLease(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
        List<LeaseExpense> leaseExpenses = expenseRepository.findAllByLease_LeaseId(lease.getLeaseId());
        RiderDemandLeaseHistories riderDemandLeaseHistory = riderDemandLeaseHistoryRepository.findByLease_LeaseId(lease.getLeaseId());

        if(!bePresent(lease)) withException("850-002");

        List<FetchFinesResponse> fines = new ArrayList<>();
        FetchLeasesResponse fetchLeasesResponse = new FetchLeasesResponse();
        fetchLeasesResponse.setFines(fines);
        fetchLeasesResponse.setLeaseId(lease.getLeaseId());
        fetchLeasesResponse.setStatus(lease.getStatus().getStatus());
        fetchLeasesResponse.setManagementType(lease.getType().getStatus());
        fetchLeasesResponse.setContractType(lease.getContractTypes().getStatus());
        fetchLeasesResponse.setTakeLoc(lease.getTakeLocation());
        fetchLeasesResponse.setIsMt(lease.getIsMt());
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
        BikeDto bakBike;

        if(lease.getLeaseStopStatus() != LeaseStopStatusTypes.CONTINUE){
            Bikes bakBikes = bikesRepository.findById(lease.getBakBikeNo()).get();
            CommonBikes carModel = bakBikes.getCarModel();
            bakBike = new BikeDto();
            bakBike.setBikeId(bakBikes.getBikeId());
            bakBike.setBikeNum(bakBikes.getCarNum());
            bakBike.setVimNum(bakBikes.getVimNum());
            bakBike.setBikeModel(carModel.getModel());
            bakBike.setBikeType(carModel.getBikeType().getType());
            bakBike.setBikeVolume(carModel.getVolume());
            fetchLeasesResponse.setBakBike(bakBike);
        }

        if(leaseExpenses != null && leaseExpenses.size() > 0){
            List<ExpenseDto> expenseDtos = new ArrayList<>();
            for(LeaseExpense le:leaseExpenses){
                ExpenseDto expenseDto = new ExpenseDto();
                expenseDto.setExpenseType(le.getExpenseTypes().getType());
                expenseDto.setNumber(le.getNumber());
                expenseDto.setDescription(le.getDescription());
                expenseDto.setExpenseOptionType(le.getExpenseOptionTypes() == null ? ExpenseOptionTypes.OFF.getType() : le.getExpenseOptionTypeCode());
                if(le.getTransaction() != null){
                    expenseDto.setRegNum(le.getTransaction().getRegNum());
                    expenseDto.setCompanyName(le.getTransaction().getCompanyName());
                    expenseDto.setPrice(le.getTransaction().getPrice());
                }
                expenseDtos.add(expenseDto);
            }
            fetchLeasesResponse.setExpense(expenseDtos);
        }
        Bikes bike = lease.getBike();
        if(bePresent(bike)) {
            CommonBikes carModel = bike.getCarModel();
            fetchLeasesResponse.setBikeId(bike.getBikeId());
            BikeDto bikeDto = new BikeDto();
            bikeDto.setBikeId(bike.getBikeId());
            bikeDto.setBikeModel(carModel.getModel());
            bikeDto.setBikeNum(bike.getCarNum());
            bikeDto.setVimNum(bike.getVimNum());
            bikeDto.setYear(carModel.getYear());
            fetchLeasesResponse.setBike(bikeDto);
        }
        Clients leaseClient = lease.getClients();
        if(bePresent(leaseClient)) {
            fetchLeasesResponse.setClientId(leaseClient.getClientId());
            ClientDto clientDto = new ClientDto();
            clientDto.setClientId(leaseClient.getClientId());
            clientDto.setClientName(leaseClient.getClientInfo().getName());
            fetchLeasesResponse.setClient(clientDto);
        }
        LeasePrice leasePrice = lease.getLeasePrice();
        if(bePresent(leasePrice)){
            LeasePriceDto leasePriceDto = new LeasePriceDto();
            leasePriceDto.setLeasePrice(lease.getLeasePrice());
            fetchLeasesResponse.setLeasePrice(leasePriceDto);
        }
        LeaseInfo leaseInfo = lease.getLeaseInfo();
        if(bePresent(leaseInfo)){
            LeaseInfoDto leaseInfoDto = new LeaseInfoDto();
            leaseInfoDto.setLeaseInfo(lease.getLeaseInfo());
            leaseInfoDto.setPeriod(lease.getLeaseInfo().getPeriod());
            leaseInfoDto.setEndDt(lease.getLeaseInfo().getEndDate().toString());
            fetchLeasesResponse.setLeaseInfo(leaseInfoDto);
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
            leasePaymentDto.setPaymentEndDate(lp.getPaymentEndDate());
            leasePaymentDto.setPaidFee(lp.getPaidFee());
            leasePaymentDto.setIdx(lp.getIndex());
            leasePaymentDto.setPaidType(lp.getPaidType() != null ? lp.getPaidType().getStatus() : null);
            leasePaymentDto.setDescription(lp.getDescription());
            if(!bePresent(lp.getClientNo())){
                lp.setClientNo(lease.getClientNo());
                lp.setClient(lease.getClients());
            }
            Clients clients = clientsRepository.findById(lp.getClientNo()).get();

            ClientDto clientDto = new ClientDto();
            clientDto.setClientName(clients.getClientInfo().getName());
            clientDto.setClientId(clients.getClientId());
            leasePaymentDto.setPayClient(clientDto);
            List<LeaseExtras> allByPayment_paymentId = leaseExtraRepository.findAllByPayment_PaymentId(lp.getPaymentId());
            allByPayment_paymentId = allByPayment_paymentId == null ? new ArrayList<>() : allByPayment_paymentId;
            leasePaymentDto.setHasExtra(!allByPayment_paymentId.isEmpty());
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
        fetchLeasesResponse.getLeasePrice().setTotalLeaseFee(totalFee);
        fetchLeasesResponse.getLeasePrice().setLeaseFee(!bePresent(leasePayments) ? 0 : leasePayments.get(0).getLeaseFee());
        fetchLeasesResponse.setLeasePayments(leasePayments);
        if(bePresent(lease.getDemandLeaseNo())){
            DemandLeases byDemandLeaseNo = demandLeasesRepository.findByDemandLeaseNo(lease.getDemandLeaseNo());
            fetchLeasesResponse.setDemandLeaseId(byDemandLeaseNo.getDemandLeaseId());
        }
        RiderDemandLease riderDemandLease = riderDemandLeaseRepository.findByLease_LeaseId(lease.getLeaseId());
        if(bePresent(riderDemandLease)){
            fetchLeasesResponse.setRiderId(riderDemandLease.getRider().getRiderId());
        }
        if(riderDemandLeaseHistory != null){
            fetchLeasesResponse.setBakRiderLeaseAttachments(riderDemandLeaseHistory.getAttachmentHistoryString());
            fetchLeasesResponse.setBakRiderLeaseSpecialTerms(riderDemandLeaseHistory.getTermsHistoryString());
        }

        // extension 조회
        Integer extensionIndexByLeaseNo = leaseExtensionsRepository.getExtensionIndexByLeaseNo(lease.getLeaseNo());
        if(!bePresent(extensionIndexByLeaseNo)) extensionIndexByLeaseNo = 0;
        fetchLeasesResponse.setExtension(extensionIndexByLeaseNo);

        LeaseAttachments byLease_leaseId = leaseAttachmentRepository.findByLease_LeaseId(lease.getLeaseId());
        if(bePresent(byLease_leaseId))
            fetchLeasesResponse.setAttachments(byLease_leaseId.getAttachmentsList());

        response.put("lease", fetchLeasesResponse);
        request.setResponse(response);
        return request;
    }

    private void checkBikeStatusType(Bikes bike){
        if(!bePresent(bike)) writeMessage("존재하지않는 차량입니다.");
        if(BikeStatusTypes.JUNK.equals(bike.getBikeStatus()) || BikeStatusTypes.FOR_SALE.equals(bike.getBikeStatus()))
            writeMessage("\"보관중\"인 차량만 가능합니다.");
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
        lease.setIsMt(addUpdateLeaseRequest.getIsMt());
        //bike
        Bikes bike = bikesRepository.findByBikeId(addUpdateLeaseRequest.getBikeId());
        checkBikeStatusType(bike);
        if(!bePresent(bike)) writeMessage("존재하지않는 차량정보입니다.");
//        if(bike.getCarNum() == null) withException("850-011");
        if(bike!=null && leasesByBike.size() > 0) withException("850-001"); //이미 리스가 존재할때
        if(bike.getTransaction() == null) withException("850-034");
        // client
        Clients client = clientsRepository.findByClientId(addUpdateLeaseRequest.getClientId());
        // insurance
//        Insurances insurance = insurancesRepository.findByInsuranceId(addUpdateLeaseRequest.getInsuranceId());
//        if(bePresent(insurance)) lease.setInsuranceNo(insurance.getInsuranceNo());
        if(bePresent(client)) lease.setClientNo(client.getClientNo());
        if(bePresent(bike)) lease.setBikeNo(bike.getBikeNo());
        if(addUpdateLeaseRequest.getManagementType() != null)
            lease.setType(ManagementTypes.getManagementStatus(addUpdateLeaseRequest.getManagementType()));
        lease.setContractTypes(ContractTypes.getContractType(addUpdateLeaseRequest.getContractType()));
        lease.setCreatedAt(LocalDateTime.now());
        lease.setReleaseNo(1);
        lease.setCreatedUserNo(session.getUserNo());
        leaseRepository.save(lease);

        //lease info
        LeaseInfoDto leaseInfoDto = addUpdateLeaseRequest.getLeaseInfo();
        LeaseInfo leaseInfo = new LeaseInfo();
        leaseInfo.setLeaseNo(lease.getLeaseNo());
        List<LeasePayments> leasePaymentsList = new ArrayList<>();
        leaseInfo.setStart(LocalDate.parse(leaseInfoDto.getStartDt()));
        leaseInfo.setPeriod(leaseInfoDto.getPeriod());
        leaseInfo.setContractDate(LocalDate.parse(leaseInfoDto.getContractDt()));
        leaseInfo.setEndDate(leaseInfo.getContractDate().plusMonths(leaseInfo.getPeriod()));
        leaseInfo.setNote(leaseInfoDto.getNote());

        leasePaymentWorker.doLeasePayment(addUpdateLeaseRequest, lease, client, leaseInfo, session, leasePaymentsList);
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
        if(ContractTypes.MANAGEMENT.equals(lease.getContractTypes())){
            leasePrice.setType(PaymentTypes.MONTHLY);
        }else {
            leasePrice.setType(PaymentTypes.getPaymentType(addUpdateLeaseRequest.getLeasePrice().getPaymentType()));
        }
        if(bePresent(addUpdateLeaseRequest.getLeasePrice().getPrePayment()))
            leasePrice.setPrepayment(addUpdateLeaseRequest.getLeasePrice().getPrePayment());
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
        boolean isChanged = isChanged(addUpdateLeaseRequest, lease);
//        LeaseInsurances leaseInsurances = new LeaseInsurances();
        if(!bePresent(addUpdateLeaseRequest.getLeasePrice().getPrePayment())) withException("850-025");
        if(!bePresent(addUpdateLeaseRequest.getLeasePrice().getDeposit())) withException("850-026");
        if(!bePresent(addUpdateLeaseRequest.getLeasePrice().getProfitFee())) withException("850-027");
        if(!bePresent(addUpdateLeaseRequest.getLeasePrice().getTakeFee())) withException("850-028");
        if(!bePresent(addUpdateLeaseRequest.getLeasePrice().getRegisterFee())) withException("850-029");
        if(LeaseStatusTypes.PENDING.equals(lease.getStatus()) || !LeaseStopStatusTypes.CONTINUE.equals(lease.getLeaseStopStatus()))
            withException("850-004");
        List<Leases> leasesByBike = leaseRepository.findAllByBike_BikeId(addUpdateLeaseRequest.getBikeId());
        addUpdateLeaseRequest.validationCheck();
        if(LeaseStatusTypes.CONFIRM.equals(lease.getStatus())){
            String log = "";
            Bikes bike = bikesRepository.findByBikeId(addUpdateLeaseRequest.getBikeId());
            if (bike != null && leasesByBike.size() > 0 && !lease.equals(leasesByBike.get(0)))
                withException("850-003"); //이미 리스가 존재할때
            if(bike.getCarNum() == null) withException("850-024");
            if(bike.getTransaction() == null) withException("850-034");
            ModelTransaction modelTransaction = bike.getTransaction();
            List<LeaseExpense> expenses = expenseRepository.findAllByLease_LeaseIdAndExpenseTypes(lease.getLeaseId(), ExpenseTypes.BIKE);
            List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
            LeaseExpense leaseExpense;
            for(LeasePayments lp : payments){
                 if(lp.getPaymentDate().isAfter(LocalDate.now())){
                     lp.setClientNo(clientsRepository.findByClientId(addUpdateLeaseRequest.getClientId()).getClientNo());
                     leasePaymentsRepository.save(lp);
                 }
            }
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
            List<String> logList = new ArrayList<>();
            if((lease.getLeaseInfo().getNote() == null && addUpdateLeaseRequest.getLeaseInfo().getNote() != null) || (lease.getLeaseInfo().getNote() != null && !lease.getLeaseInfo().getNote().equals(addUpdateLeaseRequest.getLeaseInfo().getNote()))){
                if(lease.getLeaseInfo().getNote() == null)
                    logList.add("노트 내용을 <>" + addUpdateLeaseRequest.getLeaseInfo().getNote() + "</>로 설정하였습니다.");
                else
                    logList.add("노트 내용을 <>" +  lease.getLeaseInfo().getNote() + "</>에서 <>" + addUpdateLeaseRequest.getLeaseInfo().getNote() + "</>으로 변경하였습니다.\n");
            }
            if(!lease.getClientNo().equals(client.getClientNo())){
                log = "바이크 번호 <>" + bike.getVimNum() + "</>을 <>" + lease.getClients().getClientInfo().getName() + "</>에서 <>"+ client.getClientInfo().getName() + "</>로 이전하였습니다.\n";
                logList.add(log);
            }
            if(lease.getIsMt() != (addUpdateLeaseRequest.getIsMt())){ // 업데이트된 리스의 MT서비스 이용여부가 다를 때
                log = "MT서비스 사용여부를 <>" + (lease.getIsMt() ? "사용" : "사용안함") + "</>에서 <>" + (addUpdateLeaseRequest.getIsMt()? "사용" : "사용안함") + "</>으로 변경하였습니다.\n";
                logList.add(log);
            }
            LeaseInfo leaseInfo = lease.getLeaseInfo();
            lease.setClientNo(client.getClientNo());
            lease.setBikeNo(bike.getBikeNo());
            leaseInfo.setNote(addUpdateLeaseRequest.getLeaseInfo().getNote());
            leaseRepository.save(lease);
//            leaseInsurances.setLeaseNo(lease.getLeaseNo());
//            leaseInsurancesRepository.save(leaseInsurances);
            leaseInfoRepository.save(leaseInfo);
            bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), logList));
            if(!log.equals(""))
                bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_BIKE_UPDATED, request.getSessionUser().getUserNo(), lease.getBikeNo().toString(), log));

        } else {
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
//            Insurances insurance = insurancesRepository.findByInsuranceId(addUpdateLeaseRequest.getInsuranceId());
            //release
            Releases release = releaseRepository.findByReleaseId(addUpdateLeaseRequest.getReleaseId());
            LeaseInfoDto leaseInfoDto = addUpdateLeaseRequest.getLeaseInfo();
            LeaseInfo leaseInfo = lease.getLeaseInfo();
            LeasePriceDto leasePriceDto = addUpdateLeaseRequest.getLeasePrice();
            LeasePrice leasePrice = lease.getLeasePrice();
            updateLeaseInfoLog(request.getSessionUser(), addUpdateLeaseRequest, client, bike, lease);
            leaseInfo.setPeriod(addUpdateLeaseRequest.getLeaseInfo().getPeriod());
            if (bePresent(client)) lease.setClientNo(client.getClientNo());
            if (bePresent(bike)) lease.setBikeNo(bike.getBikeNo());
            if (bePresent(release)) lease.setReleaseNo(release.getReleaseNo());
            if (bePresent(addUpdateLeaseRequest.getManagementType())) lease.setType(ManagementTypes.getManagementStatus(addUpdateLeaseRequest.getManagementType()));
            if (bePresent(addUpdateLeaseRequest.getTakeLoc())) lease.setTakeLocation(addUpdateLeaseRequest.getTakeLoc());
            if (bePresent(addUpdateLeaseRequest.getTakeAt())) lease.setTakeAt(addUpdateLeaseRequest.getTakeAt());
            if (bePresent(addUpdateLeaseRequest.getReleaseAt())) lease.setReleaseAt(addUpdateLeaseRequest.getReleaseAt());
            lease.setIsMt(addUpdateLeaseRequest.getIsMt());
            lease.setContractTypes(ContractTypes.getContractType(addUpdateLeaseRequest.getContractType()));
            lease.setUpLease(addUpdateLeaseRequest.getUpLeaseNo());
            leaseRepository.save(lease);
//            leaseInsurances.setLeaseNo(lease.getLeaseNo());
//            leaseInsurancesRepository.save(leaseInsurances);

            //lease price
            if(ContractTypes.MANAGEMENT.equals(lease.getContractTypes())){
                leasePrice.setType(PaymentTypes.MONTHLY);
            }else {
                leasePrice.setType(PaymentTypes.getPaymentType(leasePriceDto.getPaymentType()));
            }
            leasePrice.setLeaseNo(lease.getLeaseNo());
            leasePrice.setDeposit(leasePriceDto.getDeposit());
            if (leasePriceDto.getPrePayment() != null)
                leasePrice.setPrepayment(leasePriceDto.getPrePayment());
            leasePrice.setProfit(leasePriceDto.getProfitFee());
            leasePrice.setTakeFee(leasePriceDto.getTakeFee());
            leasePrice.setRegisterFee(leasePriceDto.getRegisterFee());
            leasePriceRepository.save(leasePrice);

            leaseInfo.setContractDate(LocalDate.parse(leaseInfoDto.getContractDt()));
            leaseInfo.setNote(leaseInfoDto.getNote());

            BikeUser session = request.getSessionUser();
            List<LeasePayments> newPaymentList = new ArrayList<>();
            if(isChanged){
                leaseInfo.setStart(LocalDate.parse(leaseInfoDto.getStartDt()));
                leaseInfo.setPeriod(leaseInfoDto.getPeriod());
                leaseExtraRepository.deleteAllByLeaseNo(lease.getLeaseNo());
                leasePaymentsRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
                leasePaymentWorker.doLeasePayment(addUpdateLeaseRequest, lease, client, leaseInfo, session, newPaymentList);
                leasePaymentsRepository.saveAll(newPaymentList);
            }
            leaseInfo.setEndDate(leaseInfo.getContractDate().plusMonths(leaseInfo.getPeriod()));
            leaseInfoRepository.save(leaseInfo);

        }
        return request;
    }



    @Transactional
    public void updateLeaseInfoLog(BikeUser session, AddUpdateLeaseRequest leaseRequest, Clients clientRequested, Bikes bikeRequested, Leases leases){
        List<String> stringList = new ArrayList<>();
        String emptyBikeId = systemParameterRepository.findByRemark("공백바이크 ID").getValue();
        boolean isSet = true;
        LeasePayments firstByLease_leaseId = leasePaymentsRepository.findFirstByLease_LeaseId(leases.getLeaseId());
        if(bePresent(leaseRequest)){
            if(bePresent(clientRequested) && !clientRequested.getClientNo().equals(leases.getClientNo())){
                Clients clients = leases.getClients();
                if(clients == null){
                    stringList.add("고객정보 <>" + clientRequested.getClientInfo().getName() + " [" + clientRequested.getClientId() + "] " + "</>로 설정하였습니다.\n");
                }else{
                    stringList.add("고객정보를 <>" + clients.getClientInfo().getName() + " [" + clients.getClientId() + "] " + "</>에서 <>" + clientRequested.getClientInfo().getName() + " [" + clientRequested.getClientId() + "] " + "</>으로 변경하였습니다.\n");
                }
            }
            if(bePresent(bikeRequested) && !bikeRequested.getBikeNo().equals(leases.getBikeNo())){
                Bikes bike = leases.getBike();
                if(bike == null || bike.getBikeId().equals(emptyBikeId))
                    stringList.add("바이크 정보 <>" + bikeRequested.getVimNum() + " [" +  bikeRequested.getBikeId() + "] " + "</>로 설정하였습니다.\n");
                else
                    stringList.add("바이크 정보를 <>" + bike.getVimNum() + " [" + bike.getBikeId() + " ]" + "</>에서 <>" + bikeRequested.getVimNum() + " [" +  bikeRequested.getBikeId() + "] " + "</>으로 변경하였습니다.\n");
            }
//            if(bePresent(insurancesRequested) && !insurancesRequested.getInsuranceNo().equals(leases.getInsuranceNo())){
//                Insurances insurance = leases.getInsurances();
//                if(insurance == null)
//                    stringList.add("보험을 <>" + insurancesRequested.getCompanyName() + " " + insurancesRequested.getAge() + " [" +  insurancesRequested.getInsuranceId() + "] " + "</>로 설정하였습니다.\n");
//                else
//                    stringList.add("보험을 <>" + insurance.getCompanyName() + " " + insurance.getAge() + " [" + insurance.getInsuranceId() + " ]" + "</>에서 <>" + insurancesRequested.getCompanyName() + " " + insurancesRequested.getAge() + " [" +  insurancesRequested.getInsuranceId() + "] " + "</>으로 변경하였습니다.\n");
//            }
            // 납부료가 변경되었을 때 if문 작성
            if(bePresent(leaseRequest.getLeasePrice().getLeaseFee()) && !leaseRequest.getLeasePrice().getLeaseFee().equals(firstByLease_leaseId.getLeaseFee())){
                stringList.add("납부료를 기존 <> " + firstByLease_leaseId.getLeaseFee() + " </>원에서 <>" + leaseRequest.getLeasePrice().getLeaseFee() + " </>원으로 변경하였습니다.\n");
            }
            if(bePresent(leaseRequest.getLeasePrice().getPaymentType()) && !leaseRequest.getLeasePrice().getPaymentType().equals(leases.getLeasePrice().getType().getPaymentType())){
                Hibernate.initialize(leases.getPayments());
                List<LeasePayments> payments = leases.getPayments();
                if(leases.getLeasePrice().getType() == PaymentTypes.DAILY) {
                    stringList.add("결제구분을 <> 일차감 </>에서 <> 월차감 </>으로 변경하였습니다.\n");
                    if(bePresent(leaseRequest.getLeaseInfo().getPeriod()) && !leaseRequest.getLeaseInfo().getPeriod().equals(leases.getLeaseInfo().getPeriod())){
                       //stringList.add("계약기간을 <>" +  getDiffMonths(leases.getLeaseInfo().getStart(), leases.getLeaseInfo().getEndDate()) + " 일</>에서 <>" + leaseRequest.getLeaseInfo().getPeriod() + " 개월</>로 변경하였습니다.\n");
                        stringList.add("계약기간을 <>" +  getDiffDays(leases.getLeaseInfo().getStart(), leases.getLeaseInfo().getEndDate()) + " 일</>에서 <>" + leaseRequest.getLeaseInfo().getPeriod() + " 개월</>로 변경하였습니다.\n");
                    }
//                    if(bePresent(leaseRequest.getLeasePrice().getLeaseFee()) && !leaseRequest.getLeasePrice().getLeaseFee().equals(first.getLeaseFee()*365)){
//                        stringList.add("리스료를 <>" +  Utils.getCurrencyFormat(first.getLeaseFee()) + " 원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getLeaseFee()) + " 원</>로 변경하였습니다.");
//                    }
                }
                else {
                    stringList.add("결제구분을 <> 월차감 </>에서 <> 일차감 </>으로 변경하였습니다.\n");
                    if(bePresent(leaseRequest.getLeaseInfo().getPeriod()) && !leaseRequest.getLeaseInfo().getPeriod().equals(leases.getLeaseInfo().getPeriod())) {
                        //stringList.add("계약기간을 <>" + getDiffMonths(leases.getLeaseInfo().getStart(), leases.getLeaseInfo().getEndDate()) + " 개월</>에서 <>" + leaseRequest.getLeaseInfo().getPeriod() + " 일</>로 변경하였습니다.\n");
                        stringList.add("계약기간을 <>" + getDiffMonths(leases.getLeaseInfo().getStart(), leases.getLeaseInfo().getEndDate()) + " 개월</>에서 <>" + getDiffDays(leases.getLeaseInfo().getStart(), leases.getLeaseInfo().getEndDate()) + " 일</>로 변경하였습니다.\n");
                    }
//                    if(bePresent(leaseRequest.getLeasePrice().getLeaseFee()) && !leaseRequest.getLeasePrice().getLeaseFee().equals(first.getLeaseFee()*12)){
//                        stringList.add("리스료를 <>" +  Utils.getCurrencyFormat(first.getLeaseFee()) + " 원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getLeaseFee()) + " 원</>로 변경하였습니다.");
//                    }
                }
            }
            if(bePresent(leaseRequest.getContractType()) && !leaseRequest.getContractType().equals(leases.getContractTypes().getStatus())){
                stringList.add("계약 형태를 <>" + leases.getContractTypes().getStatusName() + "</>에서 <>" + ContractTypes.getContractType(leaseRequest.getContractType()).getStatusName() + "</>으로 변경하였습니다.\n");
            }

            if(bePresent(leaseRequest.getIsMt()) && !leaseRequest.getIsMt().equals(leases.getIsMt())){
                stringList.add("메인터넌스 서비스 여부를 <>" + (leases.getIsMt() ? "사용" : "사용안함") + "</>에서 <>" + (leaseRequest.getIsMt() ? "사용" : "사용안함") + "</>으로 변경하였습니다.\n");
            }

            if(bePresent(leaseRequest.getManagementType()) && !leaseRequest.getManagementType().equals(leases.getType().getStatus())){
                stringList.add("운용 형태를 <>" + leases.getType() + "</>에서 <>" + ManagementTypes.getManagementStatus(leaseRequest.getManagementType()) + "</>으로 변경하였습니다.\n");
            }

            if(bePresent(leaseRequest.getLeaseInfo().getContractDt()) && !LocalDate.parse(leaseRequest.getLeaseInfo().getContractDt()).equals(leases.getLeaseInfo().getContractDate())){
                stringList.add("리스 시작 날짜를 <>" +  leases.getLeaseInfo().getContractDate() + "</>에서 <>" + LocalDate.parse(leaseRequest.getLeaseInfo().getContractDt()) + "</>으로 변경하였습니다.\n");
            }
            if(bePresent(leaseRequest.getLeaseInfo().getStartDt()) && !LocalDate.parse(leaseRequest.getLeaseInfo().getStartDt()).equals(leases.getLeaseInfo().getStart())){
                stringList.add("리스 첫 납부일을 <>" +  leases.getLeaseInfo().getStart() + "</>에서 <>" + LocalDate.parse(leaseRequest.getLeaseInfo().getStartDt()) + "</>으로 변경하였습니다.\n");
            }

            if(bePresent(leaseRequest.getLeaseInfo().getNote()) && !leaseRequest.getLeaseInfo().getNote().equals(leases.getLeaseInfo().getNote())){
                if(leases.getLeaseInfo().getNote() == null)
                    stringList.add("노트 내용을 <>" + leaseRequest.getLeaseInfo().getNote() + "</>로 설정하였습니다.\n");
                else
                    stringList.add("노트 내용을 <>" +  leases.getLeaseInfo().getNote() + "</>에서 <>" + leaseRequest.getLeaseInfo().getNote() + "</>으로 변경하였습니다.\n");
            }

            if(bePresent(leaseRequest.getLeasePrice().getPrePayment()) && !leaseRequest.getLeasePrice().getPrePayment().equals(leases.getLeasePrice().getPrepayment())){
                stringList.add("선입금을 <>" + Utils.getCurrencyFormat(leases.getLeasePrice().getPrepayment()) + "원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getPrePayment()) + "원</>으로 변경하였습니다.\n");
            }
            if(bePresent(leaseRequest.getLeasePrice().getDeposit()) && !leaseRequest.getLeasePrice().getDeposit().equals(leases.getLeasePrice().getDeposit())){
                stringList.add("보증금을 <>" + Utils.getCurrencyFormat(leases.getLeasePrice().getDeposit()) + "원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getDeposit()) + "원</>으로 변경하였습니다.\n");
            }
            if(bePresent(leaseRequest.getLeasePrice().getProfitFee()) && !leaseRequest.getLeasePrice().getProfitFee().equals(leases.getLeasePrice().getProfit())){
                stringList.add("수익금을 <>" + Utils.getCurrencyFormat(leases.getLeasePrice().getProfit()) + "원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getProfitFee()) + "원</>으로 변경하였습니다.\n");
            }
            if(bePresent(leaseRequest.getLeasePrice().getTakeFee()) && !leaseRequest.getLeasePrice().getTakeFee().equals(leases.getLeasePrice().getTakeFee())){
                stringList.add("인수비를 <>" + Utils.getCurrencyFormat(leases.getLeasePrice().getTakeFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getTakeFee()) + "원</>으로 변경하였습니다.\n");
            }
            if(bePresent(leaseRequest.getLeasePrice().getRegisterFee()) && !leaseRequest.getLeasePrice().getRegisterFee().equals(leases.getLeasePrice().getRegisterFee())){
                stringList.add("등록비를 <>" + Utils.getCurrencyFormat(leases.getLeasePrice().getRegisterFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(leaseRequest.getLeasePrice().getRegisterFee()) + "</>으로 변경하였습니다.\n");
            }
            bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, session.getUserNo(), leases.getLeaseNo().toString(), stringList));
        }

    }

    private int getDiffMonths(LocalDate start, LocalDate end){
        return (end.getYear()-start.getYear())*12 + end.getMonthValue()-start.getMonthValue();
    }

    private int getDiffDays(LocalDate start, LocalDate end){
        Long dayDiffL = ChronoUnit.DAYS.between(start,end);
        int dayDiff = dayDiffL.intValue();
        return dayDiff;
    }
    @Transactional
    public BikeSessionRequest confirmLease (BikeSessionRequest request){
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        BikeUser session = request.getSessionUser();
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        Bikes bike = lease.getBike();
        checkBikeStatusType(bike);
        if(!lease.getApprovalUser().getUserId().equals(session.getUserId())) withException("850-021");
        if(!lease.getStatus().getStatus().equals("550-002")) withException("850-009");
        lease.setStatus(LeaseStatusTypes.CONFIRM);
        lease.setApprovalDt(LocalDateTime.now());
        lease.setBakBikeNo(lease.getBikeNo());
        leaseRepository.save(lease);

        // 리스가 체결이 되면 차량 보관상태는 운영중으로 변경된다.
        bike.setBikeStatus(BikeStatusTypes.RIDING);
        bikesRepository.save(bike);

        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_APPROVE_COMPLETED, session.getUserNo(), lease.getLeaseNo().toString()));
        bikeUserTodoService.addTodo(BikeUserTodoTypes.LEASE_CONFIRM, session.getUserNo(), lease.getSubmittedUserNo(), lease.getLeaseNo().toString(), lease.getLeaseId());
        RiderDemandLease riderDemandLease = riderDemandLeaseRepository.findByLease_LeaseId(lease.getLeaseId());
        if(bePresent(riderDemandLease)){
            confirmRiderLease(lease, bike, riderDemandLease);
        }
        return request;
    }

    private void confirmRiderLease(Leases lease, Bikes bike, RiderDemandLease riderDemandLease) {
        Riders rider = riderDemandLease.getRider();
        bike.setRiderNo(rider.getRiderNo());
        bike.setRiderStatus(BikeRiderStatusTypes.TAKEN);
        bike.setRiderApprovalAt(LocalDateTime.now());
        bike.setRiderLeaseNo(lease.getLeaseNo());
        bike.setRiderRequestAt(riderDemandLease.getCreatedAt());
        bike.setRiderStartAt(lease.getLeaseInfo().getStart().atStartOfDay());
        bike.setRiderEndAt(lease.getLeaseInfo().getEndDate().atStartOfDay());
        bikesRepository.save(bike);

        BikeRidersBak bikeRidersBak = new BikeRidersBak();
        bikeRidersBak.setBikeNo(bike.getBikeNo());
        bikeRidersBak.setRiderNo(rider.getRiderNo());
        bikeRidersBak.setRiderStartAt(lease.getLeaseInfo().getStart().atStartOfDay());
        bikeRidersBak.setRiderLeaseNo(lease.getLeaseNo());
        bikeRidersBak.setRiderEndAt(lease.getLeaseInfo().getEndDate().atStartOfDay());
        bikeRidersBak.setRiderApprovalAt(LocalDateTime.now());
        bikeRidersBak.setRiderRequestAt(riderDemandLease.getCreatedAt());
        bikeRiderBakRepository.save(bikeRidersBak);

        List<RiderDemandLeaseSpecialTerms> specialTerms = riderDemandLeaseTermsRepository.findAllByRiderNo(rider.getRiderNo());

        for(RiderDemandLeaseSpecialTerms st : specialTerms){
            LeaseExpense leaseExpense = new LeaseExpense();
            leaseExpense.setLeaseNo(lease.getLeaseNo());
            leaseExpense.setExpenseTypes(st.getSpecialTerms().getExpenseTypes());
            ModelTransaction mt = new ModelTransaction();
            mt.setPrice(0);
            leaseExpense.setTransaction(mt);
            leaseExpense.setNumber(1);
            leaseExpenseRepository.save(leaseExpense);
        }
        RiderDemandLeaseHistories riderDemandLeaseHistories = new RiderDemandLeaseHistories();
        riderDemandLeaseHistories.setHistory(riderDemandLease);
        List<RiderDemandLeaseAttachments> attachments = riderDemandLeaseAttachmentsRepository.findAllByRiderNo(rider.getRiderNo());
        List<RiderDemandLeaseSpecialTerms> terms = riderDemandLeaseTermsRepository.findAllByRiderNo(rider.getRiderNo());
        riderDemandLeaseHistories.setAttachmentsHistory(attachments);
        riderDemandLeaseHistories.setTermsHistory(terms);
        riderDemandLeaseHistoryRepository.save(riderDemandLeaseHistories);
        riderDemandLeaseAttachmentsRepository.deleteAllByRiderNo(rider.getRiderNo());
        riderDemandLeaseTermsRepository.deleteAllByRiderNo(rider.getRiderNo());
        riderDemandLeaseRepository.deleteAllByRiderNo(rider.getRiderNo());

        Clients clients = lease.getClients();
        Activities activities = new Activities();
        activities.setRiderNo(rider.getRiderNo());
        activities.setBikeNo(bike.getBikeNo());
        activities.setClientNo(clients.getClientNo());
        activities.setActivityType(ActivityTypes.RIDER_DEMAND_LEASE_COMPLETED);
        activitiesRepository.save(activities);
        if(bePresent(rider.getNotificationToken())) pushComponent.pushNotification(rider.getNotificationToken(), "리스신청서 계약이 완료되었습니다,", "드디어 리스계약서가 체결되었습니다. 요청하신 차량을 이용가능합니다.");
    }


    @Transactional
    public BikeSessionRequest pendingLease (BikeSessionRequest request){
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        BikeUser session = request.getSessionUser();
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        String emptyBikeId = (String)getItem("comm.common.getEmptyCar", null);
        Bikes bikes = lease.getBike();
        if(!bePresent(bikes)) writeMessage("차량정보가 없습니다.");
        if(emptyBikeId.equals(bikes.getBikeId())) withException("850-035");
        checkBikeStatusType(bikes);
        lease.setSubmittedUserNo(session.getUserNo());
        if(!LeaseStatusTypes.IN_PROGRESS.equals(lease.getStatus()) && !LeaseStatusTypes.DECLINE.equals(lease.getStatus())) withException("850-008");
        BikeUser byUserId = bikeLabUserRepository.findByUserId(leasesDto.getApprovalUserId());
        lease.setApprovalUserNo(byUserId.getUserNo());
        LeaseInfo leaseInfo = lease.getLeaseInfo();
        LeasePrice leasePrice = lease.getLeasePrice();
        if(!bePresent(lease.getClientNo())||!bePresent(lease.getReleaseNo())||!bePresent(lease.getBikeNo())) withException("850-005");
        if(!bePresent(leaseInfo.getStart())) withException("850-006");
        if(!bePresent(leasePrice.getDeposit())||!bePresent(leasePrice.getPrepayment())||!bePresent(leasePrice.getProfit())||!bePresent(leasePrice.getTakeFee())||!bePresent(leasePrice.getRegisterFee())) withException("850-007");
        if(leasesDto.getApprovalUserId()==null) withException("850-020");
        lease.setStatus(LeaseStatusTypes.PENDING);
        leaseRepository.save(lease);
//        leaseInsurances.setLeaseNo(lease.getLeaseNo());
//        leaseInsurancesRepository.save(leaseInsurances);
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
        leaseAttachmentRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        leaseInfoRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        leasePaymentsRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        leasePriceRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        leaseInsurancesRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        expenseRepository.deleteAllByLease_LeaseId(lease.getLeaseId());
        RiderDemandLease riderDemandLease = riderDemandLeaseRepository.findByLease_LeaseId(lease.getLeaseId());
        if(bePresent(riderDemandLease)){
            riderDemandLease.setLeaseNo(null);
            riderDemandLease.setDemandLeaseStatusTypes(DemandLeaseStatusTypes.PENDING);
            riderDemandLeaseRepository.save(riderDemandLease);
        }
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

        StopLeaseDto stopLeaseDto = map(param, StopLeaseDto.class);
        double stopFee = stopLeaseDto.getStopFee();
        Leases lease = leaseRepository.findByLeaseId(stopLeaseDto.getLeaseId());
        Clients client = lease.getClients();
        Bikes bike = lease.getBike();
        bike.doDeclineRider();
        if(lease == null || lease.getStatus() != LeaseStatusTypes.CONFIRM) withException("");
        Bikes emptyBike = bikesRepository.findByBikeId(systemParameterRepository.findByRemark("공백바이크 ID").getValue());
        lease.setBakBikeNo(lease.getBike().getBikeNo());
        lease.setBikeNo(emptyBike.getBikeNo());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        LocalDate stopDate = LocalDate.parse(stopLeaseDto.getStopDt());
        String formattedString = stopDate.format(formatter);
        String log = setStopLeaseLog(stopLeaseDto, stopFee, lease, formattedString);
        lease.setLeaseStopStatus(LeaseStopStatusTypes.STOP_CONTINUE);
        lease.setStopDt(LocalDate.parse(stopLeaseDto.getStopDt()).atStartOfDay());
        lease.setStopFee(Math.round(stopFee));
        lease.setStopPaidFee(0L);
        lease.setStopReason(stopLeaseDto.getStopReason());
        leasePaymentWorker.changeByStopLease(lease.getLeaseId(), lease.getStopDt());
        if(lease.getBike().getRiderNo() != null)
            detachRiderFromBike(lease.getBike().getBikeId());
        leaseRepository.save(lease);

        // 중도해지가 될 경우, 차량 보관상태는 '보관중'으로 변경된다.
        bike.setBikeStatus(BikeStatusTypes.PENDING);
        bike.setWarehouse(client.getClientInfo().getName());
        bikesRepository.save(bike);

        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), log));
        return request;
    }

    private String setStopLeaseLog(StopLeaseDto stopLeaseDto, double stopFee, Leases lease, String formattedString) {
        String log ="";
        log = "바이크 번호 <>" + lease.getBike().getCarNum() + "</>가 중도해지 되었습니다.<br>";
        log += "중도 해지 위약금은 <>" + Utils.getCurrencyFormat(Math.round(stopFee)) + "원</>으로 설정 되었습니다.<br>" + "중도 해지 일자는 <>"
                + formattedString + "</>로 설정 되었습니다.<br>" + "중도 해지 이유는 <>" + stopLeaseDto.getStopReason() + "</>입니다.";
        return log;
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
        if(!stopLeaseDto.getStopFee().equals(lease.getStopFee())){
            log += "중도 해지 위약금이 <>" + Utils.getCurrencyFormat(lease.getStopFee().intValue()) + "원</>에서 <>" + Utils.getCurrencyFormat(stopLeaseDto.getStopFee()) + "원</>으로 수정 되었습니다.<br>";
            lease.setStopFee(stopLeaseDto.getStopFee());
        }
        if(!stopLeaseDto.getStopReason().equals(lease.getStopReason())){
            log += "중도 해지 이유가 <>" + lease.getStopReason() + "</>에서 <>" + stopLeaseDto.getStopReason() + "</>로 변경 되었습니다.<br>";
            lease.setStopReason(stopLeaseDto.getStopReason());
        }
        if(!lease.getStopPaidFee().equals(stopLeaseDto.getStopPaidFee())){
            log += "중도 해지 위약금 납부 금액이 <>" + Utils.getCurrencyFormat(lease.getStopFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(stopLeaseDto.getStopFee()) + "원</>으로 수정 되었습니다.<br>";
            lease.setStopPaidFee(stopLeaseDto.getStopPaidFee());
        }
        leaseRepository.save(lease);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), log.endsWith("<br>")? log.substring(0, log.length()-4) : log));
        return request;
    }

    public BikeSessionRequest generatePreSignedURLToUploadLeaseFile(BikeSessionRequest request) {
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        String filename = leasesDto.getFilename().substring(0, leasesDto.getFilename().lastIndexOf("."));
        String extension = leasesDto.getFilename().substring(leasesDto.getFilename().lastIndexOf(".") + 1);
        PresignedURLVo presignedURLVo = commonWorker.generatePreSignedUrl(filename, extension);
        request.setResponse(presignedURLVo);
        return request;
    }

    @Transactional
    public BikeSessionRequest addAttachments(BikeSessionRequest request){
        Map param = request.getParam();
        AddLeaseAttachmentRequest addLeaseAttachmentRequest = map(param, AddLeaseAttachmentRequest.class);
        Leases lease = leaseRepository.findByLeaseId(addLeaseAttachmentRequest.getLeaseId());
        LeaseAttachments leaseAttachments = lease.getAttachments();
        if(bePresent(addLeaseAttachmentRequest.getAttachments())){
            if(!bePresent(leaseAttachments)){
                leaseAttachments = new LeaseAttachments();
                leaseAttachments.setLeaseNo(lease.getLeaseNo());
            }
            List<ModelAttachment> attachments = leaseAttachments.getAttachmentsList();
            List<ModelAttachment> toAdd = addLeaseAttachmentRequest
                    .getAttachments()
                    .stream().map(presignedURLVo -> {
                        AmazonS3 amazonS3 = AmazonUtils.amazonS3();
                        String fileKey = "lease-attachment/" + lease.getLeaseNo() + "/" + presignedURLVo.getFileKey();
                        CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
                        amazonS3.copyObject(objectRequest);
                        ModelAttachment leaseAttachment = new ModelAttachment();
                        leaseAttachment.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
                        leaseAttachment.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
                        leaseAttachment.setUri("/" + fileKey);
                        leaseAttachment.setFileName(presignedURLVo.getFilename());
                        return leaseAttachment;
                    }).collect(Collectors.toList());
            if(!bePresent(attachments))
                attachments = new ArrayList<>();
            attachments.addAll(toAdd);
            leaseAttachments.setAttachmentsList(attachments);
            leaseAttachmentRepository.save(leaseAttachments);
        }
        return request;
    }

//    @Transactional
//    public BikeSessionRequest checkFileUploadComplete(BikeSessionRequest request) {
//        Map param = request.getParam();
//        PresignedURLVo presignedURLVo = map(param, PresignedURLVo.class);
//        String leaseId = (String) param.get("lease_id");
//        Leases lease = leaseRepository.findByLeaseId(leaseId);
//        Leases bikeAttachments = new BikeAttachments();
//        bikeAttachments.setBikeNo(bike.getBikeNo());
//        bikeAttachments.setFileName(presignedURLVo.getFilename());
//        bikeAttachments.setFileKey("/" + presignedURLVo.getFileKey());
//        bikeAttachments.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
//        bikeAttachmentRepository.save(bikeAttachments);
//        //
//        AmazonS3 amazonS3 = AmazonS3Client.builder()
//                .withRegion(Regions.AP_NORTHEAST_2)
//                .withCredentials(AmazonUtils.awsCredentialsProvider())
//                .build();
//        CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, presignedURLVo.getFileKey());
//        amazonS3.copyObject(objectRequest);
//        String log = "바이크에 <>" + presignedURLVo.getFilename() + "</> 파일명의 파일이 추가 되었습니다.";
//        bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_BIKE_UPDATED, request.getSessionUser().getUserNo(), bike.getBikeNo().toString(), log));
//        Map response = new HashMap();
//        response.put("url", bikeAttachments.getFileKey());
//        request.setResponse(response);
//        return request;
//    }


    @Transactional
    public BikeSessionRequest deleteAttachments(BikeSessionRequest request){
        Map param = request.getParam();
        String leaseId = (String) param.get("lease_id");
        String uuid = (String) param.get("uuid");
        LeaseAttachments attachments = leaseAttachmentRepository.findByLease_LeaseId(leaseId);
        List<ModelAttachment> attachmentsList = attachments.getAttachmentsList();
        String removedUrl = "";
        for (int i = 0; i < attachmentsList.size(); i++) {
            if(attachmentsList.get(i).getUuid().equals(uuid)){
                ModelAttachment remove = attachmentsList.remove(i);
                removedUrl = remove.getDomain() + remove.getUri();
                break;
            }
        }
        if(!"".equals(removedUrl)) {
            AmazonS3 amazonS3 = AmazonUtils.amazonS3();
            amazonS3.deleteObject(ENV.AWS_S3_ORIGIN_BUCKET, removedUrl);
            leaseAttachmentRepository.save(attachments);
        }
        return request;
    }







    private Integer getRegistrationFee(Integer bikePrice){
        //취등록세 공식 적용 2%
        return bikePrice / 500 * 10;
    }


    //contract_type, lease_fee, period, start_dt, payment_type
    private boolean isChanged(AddUpdateLeaseRequest request, Leases lease){
        boolean isChange = false;
        LeasePriceDto leasePriceDto = request.getLeasePrice();
        LeaseInfoDto leaseInfoDto = request.getLeaseInfo();
        LeasePayments firstByLease_leaseId = leasePaymentsRepository.findFirstByLease_LeaseId(lease.getLeaseId());
        if(!lease.getContractTypes().equals(request.getContractType()))
            isChange = true;
        if(!leasePriceDto.getLeaseFee().equals(firstByLease_leaseId.getLeaseFee()))
            isChange = true;
        if(!lease.getLeasePrice().getType().getPaymentType().equals(leasePriceDto.getPaymentType()))
            isChange = true;
        if(!leaseInfoDto.getPeriod().equals(lease.getLeaseInfo().getPeriod()))
            isChange = true;
        if(!LocalDate.parse(leaseInfoDto.getStartDt()).equals(lease.getLeaseInfo().getStart()))
            isChange = true;
        return isChange;
    }

    private void detachRiderFromBike(String bikeId){
        Bikes bike = bikesRepository.findByBikeId(bikeId);
        Riders rider = riderRepository.findById(bike.getRiderNo()).get();
        BikeRidersBak bikeRidersBak = bikeRiderBakRepository.findByRider_RiderIdAndBike_BikeId(rider.getRiderId(), bike.getBikeId());
        if(bikeRidersBak != null && LocalDateTime.now().isBefore(bikeRidersBak.getRiderEndAt())){
            bikeRidersBak.setRiderEndAt(LocalDateTime.now());
        }
        bike.setRiderNo(null);
        bikesRepository.save(bike);
        if(bikeRidersBak != null)
            bikeRiderBakRepository.save(bikeRidersBak);
    }


    public BikeSessionRequest getLeaseAttachments(BikeSessionRequest request) {
        LeasesDto leasesDto = map(request.getParam(), LeasesDto.class);
        List<ModelAttachment> attachmentsList = leaseRepository.findByLeaseId(leasesDto.getLeaseId()).getAttachments() == null ? null : leaseRepository.findByLeaseId(leasesDto.getLeaseId()).getAttachments().getAttachmentsList();
        if(!bePresent(attachmentsList))
            attachmentsList = new ArrayList<>();
        request.setResponse(attachmentsList);
        return request;
    }
}
