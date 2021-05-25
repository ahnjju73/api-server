package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.types.ContractTypes;
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
    private final LeaseExtraRepository leaseExtraRepository;
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
                List<LeasePayments> leasePaymentsList = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
                leaseInfoDto.setPeriod(leasePaymentsList.size());
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
        List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
        if(lease == null) withException("850-002");
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
        if(lease.getLeasePrice() != null){
            LeasePriceDto leasePriceDto = new LeasePriceDto();
            leasePriceDto.setLeasePrice(lease.getLeasePrice());
            fetchLeasesResponse.setLeasePrice(leasePriceDto);
        }
        if(lease.getLeaseInfo()!=null){
            LeaseInfoDto leaseInfoDto = new LeaseInfoDto();
            leaseInfoDto.setLeaseInfo(lease.getLeaseInfo());
            leaseInfoDto.setPeriod(payments.size());
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
        //exception
        if(addUpdateLeaseRequest.getBikeId() == null)withException("850-010");
        if(addUpdateLeaseRequest.getClientId() == null)withException("850-012");
        if(addUpdateLeaseRequest.getInsuranceId() == null)withException("850-013");
        if(addUpdateLeaseRequest.getLeasePrice().getPaymentType() == null)withException("850-014");
        if(addUpdateLeaseRequest.getLeaseInfo().getContractDt() == null)withException("850-016");
        if(addUpdateLeaseRequest. getLeasePrice().getPaymentDay() == null)withException("850-018");
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
        //release
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
        leaseRepository.save(lease);

        //lease info
        LeaseInfoDto leaseInfoDto = addUpdateLeaseRequest.getLeaseInfo();
        LeaseInfo leaseInfo = new LeaseInfo();
        leaseInfo.setLeaseNo(lease.getLeaseNo());
        if(leaseInfoDto.getStartDt()!=null) {
            leaseInfo.setStart(LocalDate.parse(leaseInfoDto.getStartDt()));//payment시작
            leaseInfo.setEndDate(leaseInfo.getStart().plusMonths(addUpdateLeaseRequest.getLeaseInfo().getPeriod()));
        }
        leaseInfo.setContractDate(LocalDate.parse(leaseInfoDto.getContractDt()));
        leaseInfo.setNote(leaseInfoDto.getNote());
        leaseInfoRepository.save(leaseInfo);

        LeasePrice leasePrice = new LeasePrice();
        leasePrice.setLeaseNo(lease.getLeaseNo());
        leasePrice.setProfit(addUpdateLeaseRequest.getLeasePrice().getProfitFee());
        leasePrice.setType(PaymentTypes.getPaymentType(addUpdateLeaseRequest.getLeasePrice().getPaymentType()));
        leasePrice.setRegisterFee(addUpdateLeaseRequest.getLeasePrice().getRegisterFee());
        leasePrice.setTakeFee(addUpdateLeaseRequest.getLeasePrice().getTakeFee());
        if(addUpdateLeaseRequest.getLeasePrice().getPrePayment()!= null)
            leasePrice.setPrepayment(addUpdateLeaseRequest.getLeasePrice().getPrePayment());
        leasePrice.setPaymentDay(addUpdateLeaseRequest.getLeasePrice().getPaymentDay());
        leasePrice.setTakeFee(addUpdateLeaseRequest.getLeasePrice().getTakeFee());
        leasePriceRepository.save(leasePrice);

        List<LeasePayments> leasePaymentsList = new ArrayList<>();
        BikeUser session = request.getSessionUser();
        for(int i = 0; i < addUpdateLeaseRequest.getLeaseInfo().getPeriod(); i++){
            LeasePayments leasePayment = new LeasePayments();
            String paymentId = autoKey.makeGetKey("payment");
            leasePayment.setPaymentId(paymentId);
            leasePayment.setLeaseNo(lease.getLeaseNo());
            leasePayment.setIndex(i+1);
            leasePayment.setPaymentDate(leaseInfo.getStart().plusMonths(i));
            leasePayment.setInsertedUserNo(session.getUserNo());
            if(leasePrice.getType() == PaymentTypes.MONTHLY){
                leasePayment.setLeaseFee(addUpdateLeaseRequest.getLeasePrice().getLeaseFee());
            }else{
                int days = (int)(ChronoUnit.DAYS.between(leasePayment.getPaymentDate(), leasePayment.getPaymentDate().plusMonths(1)));
                leasePayment.setLeaseFee(days * addUpdateLeaseRequest.getLeasePrice().getLeaseFee());
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
        if(leaseInfoDto.getStartDt()!=null) {
            leaseInfo.setStart(LocalDate.parse(leaseInfoDto.getStartDt()));
            leaseInfo.setEndDate(leaseInfo.getStart().plusMonths(addUpdateLeaseRequest.getLeasePayments().size()));
        }
        leaseInfo.setContractDate(LocalDate.parse(leaseInfoDto.getContractDt()));
        leaseInfo.setNote(leaseInfoDto.getNote());
        leaseInfoRepository.save(leaseInfo);

        //lease price
        PaymentTypes paymentType = leasePriceRepository.findByLease_LeaseId(lease.getLeaseId()).getType();
        LeasePriceDto leasePriceDto = addUpdateLeaseRequest.getLeasePrice();
        LeasePrice leasePrice = lease.getLeasePrice();
        leasePrice.setLeaseNo(lease.getLeaseNo());
        leasePrice.setType(PaymentTypes.getPaymentType(leasePriceDto.getPaymentType()));
        leasePrice.setPaymentDay(leasePriceDto.getPaymentDay());
        leasePrice.setDeposit(leasePriceDto.getDeposit());
        if(leasePriceDto.getPrePayment()!= null)
            leasePrice.setPrepayment(leasePriceDto.getPrePayment());
        leasePrice.setProfit(leasePriceDto.getProfitFee());
        leasePrice.setTakeFee(leasePriceDto.getTakeFee());
        leasePrice.setRegisterFee(leasePriceDto.getRegisterFee());
        leasePriceRepository.save(leasePrice);

        List<LeasePayments> leasePaymentsList = leasePaymentsRepository.findAllByLease_LeaseId(addUpdateLeaseRequest.getLeaseId());
        List<LeasePayments> newPaymentList = new ArrayList<>();
        List<LeasePaymentDto> dtosList = addUpdateLeaseRequest.getLeasePayments();
        BikeUser session = request.getSessionUser();
        if(leasePrice.getType() != paymentType){
            for(int i = 0; i < addUpdateLeaseRequest.getLeaseInfo().getPeriod(); i++){
                LeasePayments leasePayment = new LeasePayments();
                String paymentId = autoKey.makeGetKey("payment");
                leasePayment.setPaymentId(paymentId);
                leasePayment.setLeaseNo(lease.getLeaseNo());
                leasePayment.setIndex(i+1);
                leasePayment.setPaymentDate(leaseInfo.getStart().plusMonths(i));
                leasePayment.setInsertedUserNo(session.getUserNo());
                if(leasePrice.getType() == PaymentTypes.MONTHLY){
                    leasePayment.setLeaseFee(addUpdateLeaseRequest.getLeasePrice().getLeaseFee());
                }else{
                    int days = (int)(ChronoUnit.DAYS.between(leasePayment.getPaymentDate(), leasePayment.getPaymentDate().plusMonths(1)));
                    leasePayment.setLeaseFee(days * addUpdateLeaseRequest.getLeasePrice().getLeaseFee());
                }
                newPaymentList.add(leasePayment);
            }
            for(LeasePayments lp : leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId())) {
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
    public BikeSessionRequest confirmLease (BikeSessionRequest request){
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        if(!lease.getStatus().getStatus().equals("550-002")) withException("850-009");
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
        if(!bePresent(leaseInfo.getStart())) withException("850-006");
        if(!bePresent(leasePrice.getPaymentDay())||!bePresent(leasePrice.getDeposit())||!bePresent(leasePrice.getPrepayment())||!bePresent(leasePrice.getProfit())||!bePresent(leasePrice.getTakeFee())||!bePresent(leasePrice.getRegisterFee())) withException("850-007");
        lease.setStatus(LeaseStatusTypes.PENDING);
        leaseRepository.save(lease);
        return request;
    }

    @Transactional BikeSessionRequest rejectLease(BikeSessionRequest request){
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        if(!lease.getStatus().getStatus().equals("550-002")) withException("850-020");
        lease.setStatus(LeaseStatusTypes.DECLINE);
        leaseRepository.save(lease);
        return request;
    }
}
