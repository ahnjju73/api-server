package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.bike.BikeRidersBak;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.domain.riders.*;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.objects.*;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.BikeModelDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.AddUpdateLeaseRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeaseInfoDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeasePriceDto;
import helmet.bikelab.apiserver.objects.requests.AddUpdateRiderRequest;
import helmet.bikelab.apiserver.objects.responses.FetchRiderDetailResponse;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@Service
@RequiredArgsConstructor
public class RiderWorker extends SessService {

    private final BikesRepository bikesRepository;
    private final BikeUserTodoRepository bikeUserTodoRepository;
    private final BikeRiderBakRepository bikeRiderBakRepository;
    private final ClientsRepository clientsRepository;
    private final RiderRepository riderRepository;
    private final RiderInfoRepository riderInfoRepository;
    private final RiderPasswordRepository riderPasswordRepository;
    private final RiderAccountsRepository riderAccountsRepository;
    private final RiderAddressRepository riderAddressRepository;
    private final RiderDemandLeaseRepository riderDemandLeaseRepository;
    private final SystemParameterRepository systemParameterRepository;
    private final ActivitiesRepository activitiesRepository;
    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final LeaseInfoRepository leaseInfoRepository;
    private final LeasePriceRepository leasePriceRepository;
    private final LeaseExtraRepository leaseExtraRepository;
    private final LeaseExpenseRepository expenseRepository;
    private final InsurancesRepository insurancesRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final RiderDemandLeaseTermsRepository riderDemandLeaseTermsRepository;
    private final RiderDemandLeaseAttachmentsRepository riderDemandLeaseAttachmentsRepository;
    private final AutoKey autoKey;

    public Bikes getBikeByRiderIdAndBikeId(String riderId, String bikeId){
        Bikes byBikeIdAndRiders_riderId = bikesRepository.findByBikeIdAndRiders_RiderId(bikeId, riderId);
        if(!bePresent(byBikeIdAndRiders_riderId)) withException("2006-001");
        return byBikeIdAndRiders_riderId;
    }

    public Riders getRiderById(String riderId){
        Riders byRiderId = riderRepository.findByRiderId(riderId);
        if(!bePresent(byRiderId)) withException("510-001");
        return byRiderId;
    }

    public void addNewRider(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        AddUpdateRiderRequest addUpdateRiderRequest = map(param, AddUpdateRiderRequest.class);
        addUpdateRiderRequest.checkValidation();
        if(bePresent(riderRepository.findByPhone(addUpdateRiderRequest.getPhone())))
            withException("950-007");
        if(bePresent(riderRepository.findByEmail(addUpdateRiderRequest.getEmail())))
            withException("950-008");
        String riderId = autoKey.makeGetKey("rider");
        Riders riders = new Riders();
        if(addUpdateRiderRequest.getSsn() != null) {
            String front = addUpdateRiderRequest.getSsn().substring(0, addUpdateRiderRequest.getSsn().indexOf("-"));
            String back = addUpdateRiderRequest.getSsn().substring(addUpdateRiderRequest.getSsn().indexOf("-") + 1);
            riders.setFrontSsn(front);
            riders.setBackSsn(back);
        }
        riders.setRiderId(riderId);
        riders.setCreatedAt(LocalDateTime.now());
        riders.setEmail(addUpdateRiderRequest.getEmail());
        riders.setPhone(addUpdateRiderRequest.getPhone());
        riders.setStatus(RiderStatusTypes.PENDING);
        riders.setEdpId(addUpdateRiderRequest.getEdpId());
        riderRepository.save(riders);

        if(addUpdateRiderRequest.getRealAddress() != null) {
            RiderAddresses real = new RiderAddresses();
            real.setRiderNo(riders.getRiderNo());
            real.setRiderAddressTypes(RiderAddressTypes.REAL_RESIDENCE);
            real.setModelAddress(addUpdateRiderRequest.getRealAddress());
            riderAddressRepository.save(real);
        }
        if(addUpdateRiderRequest.getPaperAddress() != null) {
            RiderAddresses paper = new RiderAddresses();
            paper.setRiderNo(riders.getRiderNo());
            paper.setRiderAddressTypes(RiderAddressTypes.ON_PAPER);
            paper.setModelAddress(addUpdateRiderRequest.getPaperAddress());
            riderAddressRepository.save(paper);
        }

        RiderInfo riderInfo = new RiderInfo();
        riderInfo.setRiderNo(riders.getRiderNo());
        riderInfo.setRider(riders);
        riderInfo.setName(addUpdateRiderRequest.getName());

        RiderPassword riderPassword = new RiderPassword();
        String password = generateNewPassword();
        riderPassword.setRider(riders);
        riderPassword.setRiderNo(riders.getRiderNo());
        riderPassword.newPassword(password);

        RiderAccounts riderAccount = new RiderAccounts();
        riderAccount.setRider(riders);
        riderAccount.setRiderNo(riders.getRiderNo());
        riderAccount.setAccountType(AccountTypes.EMAIL);

        riderInfoRepository.save(riderInfo);
        riderPasswordRepository.save(riderPassword);
        riderAccountsRepository.save(riderAccount);

        Activities activities = new Activities();
        activities.setActivityType(ActivityTypes.RIDER_SIGN_UP);
        activities.setRiderNo(riders.getRiderNo());
        activitiesRepository.save(activities);

        response.put("rider_id", riderId);
        response.put("password", password);

        request.setResponse(response);
    }

    public FetchRiderDetailResponse getRiderDetail(String riderId){
        FetchRiderDetailResponse fetchRiderDetailResponse = new FetchRiderDetailResponse();
        Riders rider = riderRepository.findByRiderId(riderId);
        RiderInfo riderInfo = rider.getRiderInfo();
        RiderAddresses paper = riderAddressRepository.findByRider_RiderIdAndRiderAddressTypes(rider.getRiderId(), RiderAddressTypes.ON_PAPER);
        RiderAddresses real = riderAddressRepository.findByRider_RiderIdAndRiderAddressTypes(rider.getRiderId(), RiderAddressTypes.REAL_RESIDENCE);
        fetchRiderDetailResponse.setRiderId(riderId);
        fetchRiderDetailResponse.setRiderNo(rider.getRiderNo());
        fetchRiderDetailResponse.setCreatedAt(rider.getCreatedAt());
        fetchRiderDetailResponse.setRiderVerifiedStatus(rider.getVerifiedType().getVerifiedType());

        fetchRiderDetailResponse.setVerifiedType(rider.getVerifiedType());
        fetchRiderDetailResponse.setVerifiedAt(rider.getVerifiedAt());
        fetchRiderDetailResponse.setVerifiedRequestAt(rider.getVerifiedRequestAt());
        fetchRiderDetailResponse.setVerifiedRejectMessage(rider.getVerifiedRejectMessage());

        fetchRiderDetailResponse.setEdpId(rider.getEdpId());
        fetchRiderDetailResponse.setDescription(rider.getDescription());
        if(rider.getFrontSsn() != null) {
            fetchRiderDetailResponse.setSsn(rider.getFrontSsn() + "-" + rider.getBackSsn());
        }
        fetchRiderDetailResponse.setRealAddress(real == null ? null : real.getModelAddress());
        fetchRiderDetailResponse.setPaperAddress(paper == null ? null : paper.getModelAddress());

        RiderInfoDto riderInfoDto = new RiderInfoDto();
        riderInfoDto.setRiderEmail(rider.getEmail());
        riderInfoDto.setRiderName(riderInfo.getName());
        riderInfoDto.setRiderStatus(rider.getStatus().getRiderStatusType());
        riderInfoDto.setRiderPhone(rider.getPhone());
        fetchRiderDetailResponse.setRiderInfo(riderInfoDto);

        if(riderDemandLeaseRepository.existsByRiderNo(rider.getRiderNo())){
            RiderDemandLease riderDemandLease = riderDemandLeaseRepository.findByRiderNo(rider.getRiderNo());
            Leases lease = riderDemandLease.getLease();
            CommonBikes carModel = riderDemandLease.getCarModel();
            RiderDemandLeasesDto riderDemandLeasesDto = new RiderDemandLeasesDto();
            riderDemandLeasesDto.setRiderId(rider.getRiderId());
            riderDemandLeasesDto.setLeaseId(lease == null ? null : lease.getLeaseId());
            riderDemandLeasesDto.setDemandLeaseStatus(riderDemandLease.getDemandLeaseStatusTypes().getStatusName());
            riderDemandLeasesDto.setManagementType(riderDemandLease.getManagementType() != null ? riderDemandLease.getManagementType().getStatus() : null);
            riderDemandLeasesDto.setExpireType(riderDemandLease.getExpireTypes() != null ? riderDemandLease.getExpireTypes().getStatusName() : null);
            riderDemandLeasesDto.setPrepayment(riderDemandLease.getPrepayment());
            riderDemandLeasesDto.setInsuranceType(riderDemandLease.getInsuranceType() != null ? riderDemandLease.getInsuranceType().getLeaseTypeName() : null);
            riderDemandLeasesDto.setPaymentType(riderDemandLease.getPaymentType() != null ? riderDemandLease.getPaymentType().getPaymentType() : null);
            riderDemandLeasesDto.setIsMaintenance(riderDemandLease.getIsMaintenance());
            if(riderDemandLease.getRejectedAt() != null){
                riderDemandLeasesDto.setRejectedAt(riderDemandLease.getRejectedAt());
                riderDemandLeasesDto.setRejectMessage(riderDemandLease.getRejectMessage());
            }
            riderDemandLeasesDto.setCreatedAt(riderDemandLease.getCreatedAt());
            if(lease != null) {
                LeaseInfoDto leaseInfoDto = new LeaseInfoDto();
                leaseInfoDto.setPeriod(lease.getLeaseInfo().getPeriod());
                leaseInfoDto.setStartDt(lease.getLeaseInfo().getStart().toString());
                leaseInfoDto.setEndDt(lease.getLeaseInfo().getEndDate().toString());
                riderDemandLeasesDto.setLeaseInfo(leaseInfoDto);
            }
            if(carModel != null){
                BikeModelDto modelDto = new BikeModelDto();
                modelDto.setModel(carModel.getModel());
                modelDto.setBikeType(carModel.getBikeType());
                modelDto.setBikeTypeCode(carModel.getBikeTypeCode());
                modelDto.setVolume(carModel.getVolume());
                riderDemandLeasesDto.setBike(modelDto);
            }
            List<RiderDemandLeaseAttachmentDto> attachmentDtos = new ArrayList<>();
            List<RiderDemandLeaseAttachments> allByRiderNo = riderDemandLeaseAttachmentsRepository.findAllByRiderNo(rider.getRiderNo());
            for(RiderDemandLeaseAttachments attachments : allByRiderNo){
                RiderDemandLeaseAttachmentDto riderDemandLeaseAttachmentDto = new RiderDemandLeaseAttachmentDto();
                riderDemandLeaseAttachmentDto.setDomain(attachments.getDomain());
                riderDemandLeaseAttachmentDto.setFileKey(attachments.getFileKey());
                riderDemandLeaseAttachmentDto.setFileName(attachments.getFileName());
                attachmentDtos.add(riderDemandLeaseAttachmentDto);
            }
            riderDemandLeasesDto.setRiderDemandLeaseAttachments(attachmentDtos);
            List<String> terms = new ArrayList<>();
            List<RiderDemandLeaseSpecialTerms> termsList = riderDemandLeaseTermsRepository.findAllByRiderNo(rider.getRiderNo());
            for(RiderDemandLeaseSpecialTerms term : termsList){
                terms.add(term.getSpecialTerms().getStermName());
            }
            riderDemandLeasesDto.setRiderDemandLeaseSpecialTerms(terms);
            fetchRiderDetailResponse.setRiderDemandLease(riderDemandLeasesDto);
        }

        List<BikeDto> leasingBikes = new ArrayList<>();
        List<Bikes> allByRiderNo = bikesRepository.findAllByRiderNo(rider.getRiderNo());
        for(Bikes bike : allByRiderNo){
            BikeDto bikeDto = new BikeDto();
            bikeDto.setBikeId(bike.getBikeId());
            bikeDto.setBikeNum(bike.getCarNum());
            bikeDto.setVimNum(bike.getVimNum());
            bikeDto.setBikeModel(bike.getCarModel().getModel());
            bikeDto.setBikeVolume(bike.getCarModel().getVolume());
            bikeDto.setBikeType(bike.getCarModel().getBikeType().getType());
            leasingBikes.add(bikeDto);
        }
        fetchRiderDetailResponse.setLeasingBikes(leasingBikes);
        return fetchRiderDetailResponse;
    }

    public void updateRider(AddUpdateRiderRequest addUpdateRiderRequest){
        addUpdateRiderRequest.checkValidation();
        Riders riders = riderRepository.findByRiderId(addUpdateRiderRequest.getRiderId());
        if(!bePresent(riders))
            withException("950-004");
        if(bePresent(riderRepository.findByPhone(addUpdateRiderRequest.getPhone())) && !riderRepository.findByPhone(addUpdateRiderRequest.getPhone()).equals(riders))
            withException("950-007");
        if(bePresent(riderRepository.findByEmail(addUpdateRiderRequest.getEmail())) && !riderRepository.findByEmail(addUpdateRiderRequest.getEmail()).equals(riders))
            withException("950-008");
        if(bePresent(riderRepository.findByEdpId(addUpdateRiderRequest.getEdpId())) && !riderRepository.findByEdpId(addUpdateRiderRequest.getEdpId()).equals(riders))
            withException("950-009");

        riders.setEmail(addUpdateRiderRequest.getEmail());
        riders.setPhone(addUpdateRiderRequest.getPhone());
        riders.setDescription(addUpdateRiderRequest.getDescription());
        riders.setEdpId(addUpdateRiderRequest.getEdpId());
        if(addUpdateRiderRequest.getSsn() != null) {
            String front = addUpdateRiderRequest.getSsn().substring(0, addUpdateRiderRequest.getSsn().indexOf("-"));
            String back = addUpdateRiderRequest.getSsn().substring(addUpdateRiderRequest.getSsn().indexOf("-") + 1);
            riders.setFrontSsn(front);
            riders.setBackSsn(back);
        }
        riderRepository.save(riders);
        RiderAddresses real = riderAddressRepository.findByRider_RiderIdAndRiderAddressTypes(riders.getRiderId(), RiderAddressTypes.REAL_RESIDENCE);
        if(addUpdateRiderRequest.getRealAddress() != null){
            if(real == null){
                real = new RiderAddresses();
            }
            real.setModelAddress(addUpdateRiderRequest.getRealAddress());
            riderAddressRepository.save(real);
        }
        else{
            if(real != null)
                riderAddressRepository.delete(real);
        }

        RiderAddresses paper = riderAddressRepository.findByRider_RiderIdAndRiderAddressTypes(riders.getRiderId(), RiderAddressTypes.ON_PAPER);
        if(addUpdateRiderRequest.getPaperAddress() != null){
            if(paper == null){
                paper = new RiderAddresses();
            }
            paper.setModelAddress(addUpdateRiderRequest.getPaperAddress());
            riderAddressRepository.save(paper);
        }
        else{
            if(paper != null)
                riderAddressRepository.delete(paper);
        }
        RiderInfo riderInfo = new RiderInfo();
        riderInfo.setRiderNo(riders.getRiderNo());
        riderInfo.setRider(riders);
        riderInfo.setName(addUpdateRiderRequest.getName());
        riderInfoRepository.save(riderInfo);
    }

    public void changeStatus(String riderId, String status){
        Riders rider = riderRepository.findByRiderId(riderId);
        rider.setStatus(RiderStatusTypes.getRiderStatusTypes(status));
        riderRepository.save(rider);
    }

    public String resetPassword(String riderId){
        Riders rider = riderRepository.findByRiderId(riderId);
        if(rider.getStatus() != RiderStatusTypes.ACTIVATE && rider.getStatus() != RiderStatusTypes.PENDING)
            withException("950-005");
        RiderPassword riderPassword = riderPasswordRepository.findByRider_Email(rider.getEmail());

        String randomPassword = generateNewPassword();
        riderPassword.newPassword(randomPassword);
        return randomPassword;
    }

    public List<RiderBikeDto> getRiderBikes(String riderId){
        List<BikeRidersBak> bikes = bikeRiderBakRepository.findAllByRider_RiderId(riderId);
        List<RiderBikeDto> riderBikes = new ArrayList<>();
        for(BikeRidersBak bRider : bikes){
            Bikes bike = bRider.getBike();
            CommonBikes carModel = bike.getCarModel();
            RiderBikeDto rBike = new RiderBikeDto();
            rBike.setBikeNo(bike.getBikeNo());
            rBike.setBikeId(bike.getBikeId());
            rBike.setBikeNum(bike.getCarNum());
            rBike.setVimNum(bike.getVimNum());
            rBike.setBikeModel(carModel.getModel());
            rBike.setBikeVolume(carModel.getVolume());
            rBike.setBikeType(carModel.getBikeType().getType());
            rBike.setRiderStartAt(bRider.getRiderStartAt());
            rBike.setRiderEndAt(bRider.getRiderEndAt());
            riderBikes.add(rBike);
        }
        return riderBikes;
    }

    public void approveRiderDemandLease(String riderId, String clientId, BikeUser session){
        RiderDemandLease demandLease = riderDemandLeaseRepository.findByRider_RiderId(riderId);
        if(demandLease.getDemandLeaseStatusTypes() != DemandLeaseStatusTypes.PENDING) withException("");
        Clients client = clientsRepository.findByClientId(clientId);
        demandLease.setDemandLeaseStatusTypes(DemandLeaseStatusTypes.COMPLETED);
        Leases lease = new Leases();
        String leaseId = autoKey.makeGetKey("lease");
        String emptyBike = systemParameterRepository.findByRemark("공백바이크 ID").getValue();
        AddUpdateLeaseRequest addUpdateLeaseRequest = new AddUpdateLeaseRequest();
        addUpdateLeaseRequest.setLeaseId(leaseId);
        addUpdateLeaseRequest.setBikeId(emptyBike);
        addUpdateLeaseRequest.setClientId(clientId);
        LeaseInfoDto leaseInfoDto = new LeaseInfoDto();
        leaseInfoDto.setPeriod(demandLease.getPeriod());
        leaseInfoDto.setStartDt(LocalDate.now().toString());
        leaseInfoDto.setContractDt(LocalDate.now().toString());
        leaseInfoDto.setEndDt(LocalDate.now().plusMonths(demandLease.getPeriod()).toString());
        addUpdateLeaseRequest.setLeaseInfo(leaseInfoDto);

        LeasePriceDto leasePriceDto = new LeasePriceDto();
        leasePriceDto.setPaymentType(demandLease.getPaymentType().getPaymentType());
        leasePriceDto.setPrePayment(demandLease.getPrepayment());
        addUpdateLeaseRequest.setLeasePrice(leasePriceDto);
        addUpdateLeaseRequest.setManagementType(demandLease.getManagementType().getStatus());

        lease.setLeaseId(leaseId);
        //bike
        Bikes bike = bikesRepository.findByBikeId(addUpdateLeaseRequest.getBikeId());
        lease.setBikeNo(bike.getBikeNo());
        //insurance
        Insurances insurance = insurancesRepository.findByInsuranceId(systemParameterRepository.findByRemark("리스신청서 계약완료 기본 보험 ID").getValue());
        lease.setInsuranceNo(insurance.getInsuranceNo());
        if(client!=null)
            lease.setClientNo(client.getClientNo());
        if(addUpdateLeaseRequest.getManagementType() != null)
            lease.setType(ManagementTypes.getManagementStatus(addUpdateLeaseRequest.getManagementType()));
        lease.setCreatedAt(LocalDateTime.now());
        lease.setReleaseNo(1);
        lease.setCreatedUserNo(session.getUserNo());
        lease.setExpireTypes(demandLease.getExpireTypes());
        leaseRepository.save(lease);
        demandLease.setLeaseNo(lease.getLeaseNo());
        riderDemandLeaseRepository.save(demandLease);
        //lease info
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
                        leasePayment.setLeaseFee(0);
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
                        leasePayment.setLeaseFee(0);
                        leasePaymentsList.add(leasePayment);
                    }
                }
            }
        }
        leaseInfo.setContractDate(LocalDate.parse(leaseInfoDto.getContractDt()));
        leaseInfo.setNote(leaseInfoDto.getNote());
        leaseInfoRepository.save(leaseInfo);

        LeasePrice leasePrice = new LeasePrice();
        leasePrice.setLeaseNo(lease.getLeaseNo());
        leasePrice.setType(PaymentTypes.getPaymentType(addUpdateLeaseRequest.getLeasePrice().getPaymentType()));
        if(addUpdateLeaseRequest.getLeasePrice().getPrePayment()!= null)
            leasePrice.setPrepayment(addUpdateLeaseRequest.getLeasePrice().getPrePayment());
        leasePriceRepository.save(leasePrice);
        leasePaymentsRepository.saveAll(leasePaymentsList);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_ADDED, session.getUserNo(), lease.getLeaseNo().toString()));
    }

    public void rejectRiderDemandLease(String riderId, String reason) {
        RiderDemandLease demandLease = riderDemandLeaseRepository.findByRider_RiderId(riderId);
        demandLease.setDemandLeaseStatusTypes(DemandLeaseStatusTypes.DENIED);
        demandLease.setRejectedAt(LocalDateTime.now());
        demandLease.setRejectMessage(reason);
        riderDemandLeaseRepository.save(demandLease);
    }

    private String generateNewPassword(){
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for(int j = 0; j < 8; j++)
        {
            if(random.nextBoolean())
                sb.append((char)('a' + random.nextInt(26)));
            else
                sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}
