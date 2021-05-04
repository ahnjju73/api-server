package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import helmet.bikelab.apiserver.domain.types.ManagementTypes;
import helmet.bikelab.apiserver.domain.types.PaymentTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.*;
import helmet.bikelab.apiserver.objects.bikelabs.release.ReleaseDto;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.InsuranceDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class LeasesService extends SessService {

    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final LeaseInfoRepository leaseInfoRepository;
    private final LeasePriceRepository leasePriceRepository;
    private final BikesRepository bikesRepository;
    private final ClientsRepository clientsRepository;
    private final ReleaseRepository releaseRepository;
    private final InsurancesRepository insurancesRepository;
    private final AutoKey autoKey;

    public BikeSessionRequest fetchLeases(BikeSessionRequest request){
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
                bikeDto.setBikeModel(lease.getBike().getCarModel().getCodeName());
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
        if(lease==null) withException("850-002");
        FetchLeasesResponse fetchLeasesResponse = new FetchLeasesResponse();
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
            bikeDto.setBikeModel(lease.getBike().getCarModel().getCodeName());
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
        if(lease.getLeasePrice()!=null){
            LeasePriceDto leasePriceDto = new LeasePriceDto();
            leasePriceDto.setLeasePrice(lease.getLeasePrice());
            fetchLeasesResponse.setLeasePrice(leasePriceDto);
        }
        if(lease.getLeaseInfo()!=null){
            LeaseInfoDto leaseInfoDto = new LeaseInfoDto();
            leaseInfoDto.setLeaseInfo(lease.getLeaseInfo());
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
        List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
        List<LeasePaymentDto> leasePayments = new ArrayList<>();
        for(LeasePayments lp : payments){
            LeasePaymentDto leasePaymentDto = new LeasePaymentDto();
            leasePaymentDto.setLeaseFee(lp.getLeaseFee());
            leasePaymentDto.setPaymentId(lp.getPaymentId());
            leasePaymentDto.setPaymentDate(lp.getPaymentDate());
            leasePaymentDto.setPaidFee(lp.getPaidFee());
            leasePaymentDto.setIdx(lp.getIndex());
            leasePayments.add(leasePaymentDto);
        }
        fetchLeasesResponse.setLeasePayments(leasePayments);
        response.put("lease", fetchLeasesResponse);
        request.setResponse(response);
        return request;
    }


    @Transactional
    public BikeSessionRequest addLease(BikeSessionRequest request){
        Map param = request.getParam();
        AddUpdateLeaseRequest addUpdateLeaseRequest = map(param, AddUpdateLeaseRequest.class);
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
        //release
        Releases release = releaseRepository.findByReleaseId(addUpdateLeaseRequest.getReleaseId());
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
        lease.setCreatedAt(LocalDateTime.now());
        leaseRepository.save(lease);

        //lease info
        LeaseInfoDto leaseInfoDto = addUpdateLeaseRequest.getLeaseInfo();
        LeaseInfo leaseInfo = new LeaseInfo();
        leaseInfo.setLeaseNo(lease.getLeaseNo());
        leaseInfo.setPeriod(leaseInfoDto.getPeriod());
        if(leaseInfoDto.getStartDt()!=null) {
            leaseInfo.setStart(LocalDate.parse(leaseInfoDto.getStartDt()));//payment시작
            leaseInfo.setEndDate(leaseInfo.getStart().plusMonths(leaseInfo.getPeriod()));
        }
        leaseInfo.setContractDate(LocalDate.parse(leaseInfoDto.getContractDt()));
        leaseInfo.setNote(leaseInfoDto.getNote());
        leaseInfoRepository.save(leaseInfo);

        LeasePrice leasePrice = new LeasePrice();
        leasePrice.setLeaseNo(lease.getLeaseNo());
        leasePrice.setTotalLeaseFee(addUpdateLeaseRequest.getLeasePrice().getTotalLeaseFee());
        leasePrice.setProfit(addUpdateLeaseRequest.getLeasePrice().getProfitFee());
        leasePrice.setType(PaymentTypes.getPaymentType(addUpdateLeaseRequest.getLeasePrice().getPaymentType()));
        leasePrice.setRegisterFee(addUpdateLeaseRequest.getLeasePrice().getRegisterFee());
        leasePrice.setTakeFee(addUpdateLeaseRequest.getLeasePrice().getTakeFee());
        leasePrice.setPrepayment(addUpdateLeaseRequest.getLeasePrice().getPrePayment());
        leasePrice.setPaymentDay(addUpdateLeaseRequest.getLeasePrice().getPaymentDay());
        leasePrice.setTakeFee(addUpdateLeaseRequest.getLeasePrice().getTakeFee());
        leasePriceRepository.save(leasePrice);

        List<LeasePayments> leasePaymentsList = new ArrayList<>();
        BikeUser session = request.getSessionUser();
        long totalDays = ChronoUnit.DAYS.between(leaseInfo.getStart(), leaseInfo.getEndDate());
        int count = 0;
        for(int i = 0; i< leaseInfo.getPeriod(); i++){
            LeasePayments leasePayment = new LeasePayments();
            String paymentId = autoKey.makeGetKey("payment");
            leasePayment.setPaymentId(paymentId);
            leasePayment.setLeaseNo(lease.getLeaseNo());
            leasePayment.setIndex(i+1);
            leasePayment.setPaymentDate(leaseInfo.getStart().plusMonths(i+1));
            leasePayment.setInsertedUserNo(session.getUserNo());
            if(leasePrice.getType()==PaymentTypes.MONTHLY){
                int monthly = Math.round((float)leasePrice.getTotalLeaseFee()/leaseInfo.getPeriod());
                leasePayment.setLeaseFee(monthly);
                if(i==leaseInfo.getPeriod()-1){
                    leasePayment.setLeaseFee(leasePrice.getTotalLeaseFee() - count);
                }
                count += monthly;
            }
            else{
                long numDays = ChronoUnit.DAYS.between(leasePayment.getPaymentDate(), leasePayment.getPaymentDate().plusMonths(1));
                leasePayment.setLeaseFee((int)(leasePrice.getTotalLeaseFee()*(numDays/totalDays)));
                if(i==leaseInfo.getPeriod()-1){
                    leasePayment.setLeaseFee(leasePrice.getTotalLeaseFee() - count);
                }
                count+= (int)(leasePrice.getTotalLeaseFee()*(numDays/totalDays));
            }

            leasePaymentsList.add(leasePayment);
        }
        leasePaymentsRepository.saveAll(leasePaymentsList);

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
        Integer exPeriod = lease.getLeaseInfo().getPeriod();
        if(lease.getStatus() != LeaseStatusTypes.IN_PROGRESS) withException("850-004");
        //bike
        Bikes bike = bikesRepository.findByBikeId(addUpdateLeaseRequest.getBikeId());
        if(bike!=null && bike.getLease() != null && !lease.equals(bike.getLease())) withException("850-003"); //이미 리스가 존재할때
        //client
        Clients client = clientsRepository.findByClientId(addUpdateLeaseRequest.getClientId());
        //insurance
        Insurances insurance = insurancesRepository.findByInsuranceId(addUpdateLeaseRequest.getInsuranceId());
        //release
        Releases release = releaseRepository.findByReleaseId(addUpdateLeaseRequest.getReleaseId());
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
        lease.setTakeLocation(addUpdateLeaseRequest.getTakeLoc());
        lease.setTakeAt(addUpdateLeaseRequest.getTakeAt());
        lease.setReleaseAt(addUpdateLeaseRequest.getReleaseAt());
        lease.setCreatedAt(addUpdateLeaseRequest.getCreatedAt());
        lease.setUpLesase(addUpdateLeaseRequest.getUpLeaseNo());
        leaseRepository.save(lease);

        //lease info
        LeaseInfoDto leaseInfoDto = addUpdateLeaseRequest.getLeaseInfo();
        LeaseInfo leaseInfo = new LeaseInfo();
        leaseInfo.setLeaseNo(lease.getLeaseNo());
        leaseInfo.setPeriod(leaseInfoDto.getPeriod());
        if(leaseInfoDto.getStartDt()!=null) {
            leaseInfo.setStart(LocalDate.parse(leaseInfoDto.getStartDt()));
            leaseInfo.setEndDate(leaseInfo.getStart().plusMonths(leaseInfo.getPeriod()));
        }
        leaseInfo.setContractDate(LocalDate.parse(leaseInfoDto.getContractDt()));
        leaseInfo.setNote(leaseInfoDto.getNote());
        leaseInfoRepository.save(leaseInfo);

        //lease price
        LeasePriceDto leasePriceDto = addUpdateLeaseRequest.getLeasePrice();
        LeasePrice leasePrice = lease.getLeasePrice();
        leasePrice.setLeaseNo(lease.getLeaseNo());
        leasePrice.setType(PaymentTypes.getPaymentType(leasePriceDto.getPaymentType()));
        leasePrice.setPaymentDay(leasePriceDto.getPaymentDay());
        leasePrice.setDeposit(leasePriceDto.getDeposit());
        leasePrice.setPrepayment(leasePriceDto.getPrePayment());
        leasePrice.setTotalLeaseFee(leasePriceDto.getTotalLeaseFee());
        leasePrice.setProfit(leasePriceDto.getProfitFee());
        leasePrice.setTakeFee(leasePriceDto.getTakeFee());
        leasePrice.setRegisterFee(leasePriceDto.getRegisterFee());
        leasePriceRepository.save(leasePrice);

        //lease payment
        if(addUpdateLeaseRequest.getLeaseInfo().getPeriod() != exPeriod){
            List<LeasePayments> leasePaymentsList = leasePaymentsRepository.findAllByLease_LeaseId(addUpdateLeaseRequest.getLeaseId());
            leasePaymentsRepository.deleteAll(leasePaymentsList);
            leasePaymentsList = new ArrayList<>();
            BikeUser session = request.getSessionUser();
            for(int i = 0; i< leaseInfo.getPeriod(); i++){
                LeasePayments leasePayment = new LeasePayments();
                String paymentId = autoKey.makeGetKey("payment");
                leasePayment.setPaymentId(paymentId);
                leasePayment.setLeaseNo(lease.getLeaseNo());
                leasePayment.setIndex(i+1);
                leasePayment.setPaymentDate(leaseInfo.getStart().plusMonths(i+1));
                leasePayment.setInsertedUserNo(session.getUserNo());
                leasePaymentsList.add(leasePayment);
            }
        }

        List<LeasePayments> payments = new ArrayList<>();
        for(LeasePaymentDto lp : addUpdateLeaseRequest.getLeasePayments()){
            LeasePayments leasePayment = leasePaymentsRepository.findByPaymentId(lp.getPaymentId());
            leasePayment.setIndex(lp.getIdx());
            leasePayment.setLeaseFee(lp.getLeaseFee());
            leasePayment.setPaidFee(lp.getPaidFee());
            payments.add(leasePayment);
        }
        leasePaymentsRepository.saveAll(payments);
        return request;
    }


    @Transactional
    public BikeSessionRequest confirmLease (BikeSessionRequest request){
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        if(!lease.getStatus().getStatus().equals("550-002")) withException("850-008");
        lease.setStatus(LeaseStatusTypes.CONFIRM);
        leaseRepository.save(lease);
        return request;
    }

    @Transactional
    public BikeSessionRequest pendingLease (BikeSessionRequest request){
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        if(!lease.getStatus().getStatus().equals("550-001")) withException("850-008");
        LeaseInfo leaseInfo = lease.getLeaseInfo();
        LeasePrice leasePrice = lease.getLeasePrice();
        if(!bePresent(lease.getClientNo())||!bePresent(lease.getReleaseNo())||!bePresent(lease.getBikeNo())||!bePresent(lease.getInsuranceNo())) withException("850-005");
        if(!bePresent(leaseInfo.getNote())||!bePresent(leaseInfo.getPeriod())||!bePresent(leaseInfo.getNote())||!bePresent(leaseInfo.getStart())||!bePresent(leaseInfo.getEndDate())) withException("850-006");
        if(!bePresent(leasePrice.getPaymentDay())||!bePresent(leasePrice.getDeposit())||!bePresent(leasePrice.getPrepayment())||!bePresent(leasePrice.getTotalLeaseFee())||!bePresent(leasePrice.getProfit())||!bePresent(leasePrice.getTakeFee())||!bePresent(leasePrice.getRegisterFee())) withException("850-007");
        lease.setStatus(LeaseStatusTypes.PENDING);
        leaseRepository.save(lease);
        return request;
    }

}
