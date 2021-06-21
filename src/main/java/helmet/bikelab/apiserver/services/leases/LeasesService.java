package helmet.bikelab.apiserver.services.leases;

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
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.BikeUserTodoService;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final LeaseFineRepository leaseFineRepository;
    private final FinesRepository finesRepository;
    private final AutoKey autoKey;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final BikeUserTodoService bikeUserTodoService;
    private final CommonWorker commonWorker;

    public BikeSessionRequest fetchLeases(BikeSessionRequest request){
        Map param = request.getParam();
        LeasesRequestListDto requestListDto = map(param, LeasesRequestListDto.class);
        if(bePresent(requestListDto.getClientId())){
            Clients byClientId = clientsRepository.findByClientId(requestListDto.getClientId());
            requestListDto.setSearchClientNo(byClientId.getClientNo());
        }
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "leases.leases-manager.fetchLeases", "leases.leases-manager.countAllLeases", "lease_id");
        request.setResponse(responseListDto);
        return request;
    }

    @Deprecated
    public BikeSessionRequest bak_fetchLeases(BikeSessionRequest request){
        Map response = new HashMap();
        List<Leases> leases = leaseRepository.findAll();
        List<FetchLeasesResponse> fetchLeasesResponses = new ArrayList<>();
        for(Leases lease : leases){
            List<LeaseFine> leaseFineList = leaseFineRepository.findAllByLease_LeaseId(lease.getLeaseId());
            List<FetchFinesResponse> fines = new ArrayList<>();
            for(LeaseFine lf : leaseFineList){
                FetchFinesResponse fineResponse = new FetchFinesResponse();
                Fines fine = lf.getFine();
                fineResponse.setFineId(fine.getFineId());
                fineResponse.setFee(fine.getFee());
                fineResponse.setPaidFee(fine.getPaidFee());
                fineResponse.setFineNum(fine.getFineNum());
                fineResponse.setFineDate(fine.getFineDt());
                fineResponse.setFineExpireDate(fine.getExpireDt());
                fines.add(fineResponse);
            }
            FetchLeasesResponse fetchLeasesResponse = new FetchLeasesResponse();
            fetchLeasesResponse.setLeaseId(lease.getLeaseId());
            fetchLeasesResponse.setStatus(lease.getStatus().getStatus());
            fetchLeasesResponse.setFines(fines);
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
        if(lease == null) withException("850-002");
        List<LeaseFine> leaseFineList = leaseFineRepository.findAllByLease_LeaseId(lease.getLeaseId());
        List<FetchFinesResponse> fines = new ArrayList<>();
        for(LeaseFine lf : leaseFineList){
            FetchFinesResponse fineResponse = new FetchFinesResponse();
            Fines fine = lf.getFine();
            fineResponse.setFineId(fine.getFineId());
            fineResponse.setFee(fine.getFee());
            fineResponse.setPaidFee(fine.getPaidFee());
            fineResponse.setFineNum(fine.getFineNum());
            fineResponse.setFineDate(fine.getFineDt());
            fineResponse.setFineExpireDate(fine.getExpireDt());
            fines.add(fineResponse);
        }
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
        if(lease.getBike()!=null) {
            fetchLeasesResponse.setBikeId(lease.getBike().getBikeId());
            BikeDto bikeDto = new BikeDto();
            bikeDto.setBikeId(lease.getBike().getBikeId());
            bikeDto.setBikeModel(lease.getBike().getCarModel().getModel());
            bikeDto.setBikeNum(lease.getBike().getCarNum());
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
        //exception
        if(addUpdateLeaseRequest.getBikeId() == null)withException("850-010");
        if(addUpdateLeaseRequest.getClientId() == null)withException("850-012");
        if(addUpdateLeaseRequest.getInsuranceId() == null)withException("850-013");
        if(addUpdateLeaseRequest.getLeasePrice().getPaymentType() == null)withException("850-014");
        if(addUpdateLeaseRequest.getLeaseInfo().getContractDt() == null)withException("850-016");
        if(addUpdateLeaseRequest.getLeaseInfo().getStartDt() == null)withException("850-017");
        Leases lease = new Leases();
        String leaseId = autoKey.makeGetKey("lease");
        lease.setLeaseId(leaseId);
        //bike
        Bikes bike = bikesRepository.findByBikeId(addUpdateLeaseRequest.getBikeId());
        if(bike!=null && bike.getLease()!=null) withException("850-001"); //이미 리스가 존재할때
        //client
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
        if(leaseInfoDto.getStartDt()!=null) {
            leaseInfo.setStart(LocalDate.parse(leaseInfoDto.getStartDt()));//payment시작
            leaseInfo.setPeriod(leaseInfoDto.getPeriod());
            leaseInfo.setEndDate(leaseInfo.getStart().plusMonths(leaseInfoDto.getPeriod()));
        }
        leaseInfo.setContractDate(LocalDate.parse(leaseInfoDto.getContractDt()));
        leaseInfo.setNote(leaseInfoDto.getNote());
        leaseInfoRepository.save(leaseInfo);

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

        List<LeasePayments> leasePaymentsList = new ArrayList<>();
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
        if(lease.getStatus() != LeaseStatusTypes.IN_PROGRESS && lease.getStatus() != LeaseStatusTypes.DECLINE) withException("850-004");
        //bike
        Bikes bike = bikesRepository.findByBikeId(addUpdateLeaseRequest.getBikeId());
        if(bike!=null && bike.getLease() != null && !lease.equals(bike.getLease())) withException("850-003"); //이미 리스가 존재할때
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
        updateLeaseInfoLog(request.getSessionUser(), addUpdateLeaseRequest, client, insurance, bike, lease, leaseInfo, leasePrice, leasePaymentsList);

        if(client!=null)
            lease.setClientNo(client.getClientNo());
        if(bike!=null)
            lease.setBikeNo(bike.getBikeNo());
        if(release!=null)
            lease.setReleaseNo(release.getReleaseNo());
        if(insurance!=null)
            lease.setInsuranceNo(insurance.getInsuranceNo());
        if(addUpdateLeaseRequest.getManagementType() != null)
            lease.setType(ManagementTypes.getManagementStatus(addUpdateLeaseRequest.getManagementType()));
        if(addUpdateLeaseRequest.getTakeLoc() != null)
            lease.setTakeLocation(addUpdateLeaseRequest.getTakeLoc());
        if(addUpdateLeaseRequest.getTakeAt() != null)
            lease.setTakeAt(addUpdateLeaseRequest.getTakeAt());
        if(addUpdateLeaseRequest.getReleaseAt() != null)
            lease.setReleaseAt(addUpdateLeaseRequest.getReleaseAt());
            lease.setUpLesase(addUpdateLeaseRequest.getUpLeaseNo());
        leaseRepository.save(lease);

        //leaseInfo.setLeaseNo(lease.getLeaseNo());
        if(leaseInfoDto.getStartDt()!=null) {
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
        if(leasePriceDto.getPrePayment()!= null)
            leasePrice.setPrepayment(leasePriceDto.getPrePayment());
        leasePrice.setProfit(leasePriceDto.getProfitFee());
        leasePrice.setTakeFee(leasePriceDto.getTakeFee());
        leasePrice.setRegisterFee(leasePriceDto.getRegisterFee());
        leasePriceRepository.save(leasePrice);

        BikeUser session = request.getSessionUser();
        List<LeasePayments> newPaymentList = new ArrayList<>();
        if(leasePrice.getType() != paymentType){
            if(leasePrice.getType() == PaymentTypes.MONTHLY) {
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

        }
        else{
            for (int i = 0; i < dtosList.size(); i++) {
                LeasePayments payment = new LeasePayments();
                String paymentId;
                if (dtosList.get(i).getPaymentId() == null) {
                    paymentId = autoKey.makeGetKey("payment");
                    payment.setPaymentId(paymentId);
                } else {
                    paymentId = dtosList.get(i).getPaymentId();
                    payment = leasePaymentsRepository.findByPaymentId(paymentId);
                }
                payment.setLeaseNo(lease.getLeaseNo());
                payment.setIndex(i + 1);
                payment.setLeaseFee(dtosList.get(i).getLeaseFee());
                payment.setPaymentDate(leaseInfo.getStart().plusMonths(i));
                payment.setInsertedUserNo(session.getUserNo());
                newPaymentList.add(payment);
            }

            for (int i = 0; i < leasePaymentsList.size(); i++) {
                LeasePayments leasePayment = leasePaymentsList.get(i);
                boolean contains = false;
                for (LeasePaymentDto dto : dtosList) {
                    if (dto.equals(leasePayment)) {
                        contains = true;
                    }
                }
                if (!contains) {
                    leasePaymentsRepository.delete(leasePayment);
                    leasePaymentsList.remove(i);
                    i--;
                }
            }
        }
        leasePaymentsRepository.saveAll(newPaymentList);
        return request;
    }

    @Transactional
    public void updateLeaseInfoLog(BikeUser session, AddUpdateLeaseRequest leaseRequest, Clients clientRequested, Insurances insurancesRequested, Bikes bikeRequested, Leases leases, LeaseInfo leaseInfo, LeasePrice leasePrice, List<LeasePayments> leasePaymentsList){
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
            if(bePresent(leaseRequest.getContractType()) && !leaseRequest.getContractType().equals(leases.getContractTypes().getStatus())){
                stringList.add("계약 정보를 <>" + leases.getContractTypes() + "</>에서 <>" + ContractTypes.getContractType(leaseRequest.getContractType()) + "</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getManagementType()) && !leaseRequest.getManagementType().equals(leases.getType().getStatus())){
                stringList.add("운용 정보룰 <>" + leases.getType() + "</>에서 <>" + ManagementTypes.getManagementStatus(leaseRequest.getManagementType()) + "</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getLeaseInfo().getPeriod()) && getDiffMonths(leases.getLeaseInfo().getStart(), leases.getLeaseInfo().getEndDate()) != leaseRequest.getLeaseInfo().getPeriod()){
                if(leases.getLeaseInfo().getEndDate() == null) {
                    stringList.add("리스 계약기간을 <>" + leaseRequest.getLeaseInfo().getPeriod() + "</>으로 설정하였습니다.");
                    isSet = false;
                }
            }
            if(bePresent(leaseRequest.getLeaseInfo().getStartDt()) && !LocalDate.parse(leaseRequest.getLeaseInfo().getStartDt()).equals(leases.getLeaseInfo().getStart())){
                stringList.add("리스 시작 날짜를 <>" +  leases.getLeaseInfo().getStart() + "</>에서 <>" + LocalDate.parse(leaseRequest.getLeaseInfo().getStartDt()) + "</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getLeaseInfo().getNote()) && !leaseRequest.getLeaseInfo().getNote().equals(leases.getLeaseInfo().getNote())){
                if(leases.getLeaseInfo().getNote() == null)
                    stringList.add("노트 내용을 <>" + leaseRequest.getLeaseInfo().getNote() + "</>로 설정하였습니다.");
                else
                    stringList.add("노트 내용을 <>" +  leases.getLeaseInfo().getNote() + "</>에서 <>" + leaseRequest.getLeaseInfo().getNote() + "</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getLeasePrice().getPaymentType()) && !leaseRequest.getLeasePrice().getPaymentType().equals(leases.getLeasePrice().getType().getPaymentType())){
                if(leases.getLeasePrice().getType() == PaymentTypes.DAILY) {
                    stringList.add("리스 납부 방법을 <> 일차감 </>에서 <> 월차감 </>으로 변경하였습니다.");
                    if(isSet){
                        stringList.add("리스 계약기간을 <>" +  getDiffMonths(leases.getLeaseInfo().getStart(), leases.getLeaseInfo().getEndDate()) + " 일</>에서 <>" + leaseRequest.getLeaseInfo().getPeriod() + " 개월</>로 변경하였습니다.");
                    }
                }
                else {
                    stringList.add("리스 납부 방법을 <> 월차감 </>에서 <> 일차감 </>으로 변경하였습니다.");
                    if(isSet)
                        stringList.add("리스 계약기간을 <>" +  getDiffMonths(leases.getLeaseInfo().getStart(), leases.getLeaseInfo().getEndDate()) + " 개월</>에서 <>" + leaseRequest.getLeaseInfo().getPeriod() + " 일</>로 변경하였습니다.");

                }
            }
            if(bePresent(leaseRequest.getLeasePrice().getDeposit()) && !leaseRequest.getLeasePrice().getDeposit().equals(leases.getLeasePrice().getDeposit())){
                stringList.add("리스 담보금을 <>" + leases.getLeasePrice().getDeposit() + "</>에서 <>" + leaseRequest.getLeasePrice().getDeposit() + "</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getLeasePrice().getProfitFee()) && !leaseRequest.getLeasePrice().getProfitFee().equals(leases.getLeasePrice().getProfit())){
                stringList.add("리스 이익금을 <>" + leases.getLeasePrice().getProfit() + "</>에서 <>" + leaseRequest.getLeasePrice().getProfitFee() + "</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getLeasePrice().getTakeFee()) && !leaseRequest.getLeasePrice().getTakeFee().equals(leases.getLeasePrice().getTakeFee())){
                stringList.add("리스 인수비를 <>" + leases.getLeasePrice().getTakeFee() + "</>에서 <>" + leaseRequest.getLeasePrice().getTakeFee() + "</>으로 변경하였습니다.");
            }
            if(bePresent(leaseRequest.getLeasePrice().getRegisterFee()) && !leaseRequest.getLeasePrice().getRegisterFee().equals(leases.getLeasePrice().getRegisterFee())){
                stringList.add("리스 등록비를 <>" + leases.getLeasePrice().getRegisterFee() + "</>에서 <>" + leaseRequest.getLeasePrice().getRegisterFee() + "</>으로 변경하였습니다.");
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
        if(lease.getStatus() != LeaseStatusTypes.PENDING) withException("850-022");
        lease.setStatus(LeaseStatusTypes.DECLINE);
        leaseRepository.save(lease);
        bikeUserTodoService.addTodo(BikeUserTodoTypes.LEASE_REJECT, session.getUserNo(), lease.getSubmittedUserNo(), lease.getLeaseNo().toString(), lease.getLeaseId());
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_APPROVE_REJECTED, session.getUserNo(), lease.getLeaseNo().toString()));
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteLease(BikeSessionRequest request){
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        if(lease.getStatus() != LeaseStatusTypes.IN_PROGRESS) withException("850-023");
        LeaseInfo leaseInfo = lease.getLeaseInfo();
        List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
        List<LeaseFine> leaseFines = leaseFineRepository.findAllByLease_LeaseId(lease.getLeaseId());
        LeasePrice leasePrice = lease.getLeasePrice();
        List<LeaseExtras> extras = leaseExtraRepository.findAllByLease_LeaseId(lease.getLeaseId());
        leaseInfoRepository.delete(leaseInfo);
        leasePriceRepository.delete(leasePrice);
        for(LeasePayments lp : payments){
            leasePaymentsRepository.delete(lp);
        }
        for(LeaseFine lf : leaseFines){
            leaseFineRepository.delete(lf);
        }
        for(LeaseExtras le : extras){
            leaseExtraRepository.delete(le);
        }
        leaseRepository.delete(lease);
        return request;
    }

}
