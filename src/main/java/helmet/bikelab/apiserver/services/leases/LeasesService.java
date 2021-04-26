package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.bike.BikeDto;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.domain.types.ContractTypes;
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
        lease.setContractTypes(ContractTypes.getContractType(addUpdateLeaseRequest.getContractType()));
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
        leaseInfo.setStart(LocalDate.parse(leaseInfoDto.getStartDt()));//payment시작
        leaseInfo.setEndDate(LocalDate.parse(leaseInfoDto.getEndDt()));
        leaseInfo.setNote(leaseInfoDto.getNote());
        leaseInfoRepository.save(leaseInfo);

        //lease price
        LeasePriceDto leasePriceDto = addUpdateLeaseRequest.getLeasePrice();
        LeasePrice leasePrice = new LeasePrice();
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

        return request;
    }

    @Transactional
    public BikeSessionRequest updateLease(BikeSessionRequest request){
        Map param = request.getParam();
        AddUpdateLeaseRequest addUpdateLeaseRequest = map(param, AddUpdateLeaseRequest.class);
        Leases lease = leaseRepository.findByLeaseId(addUpdateLeaseRequest.getLeaseId());

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
        lease.setContractTypes(ContractTypes.getContractType(addUpdateLeaseRequest.getContractType()));
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
        leaseInfo.setStart(LocalDate.parse(leaseInfoDto.getStartDt()));
        leaseInfo.setEndDate(LocalDate.parse(leaseInfoDto.getEndDt()));
        leaseInfo.setNote(leaseInfoDto.getNote());
        leaseInfoRepository.save(leaseInfo);

        //lease price
        LeasePriceDto leasePriceDto = addUpdateLeaseRequest.getLeasePrice();
        LeasePrice leasePrice = new LeasePrice();
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

        return request;
    }

    public BikeSessionRequest changeStatus (BikeSessionRequest request){

        return request;
    }

    @Transactional
    public BikeSessionRequest confirmLease (BikeSessionRequest request){

        return request;
    }

    @Transactional
    public BikeSessionRequest pendingLease (BikeSessionRequest request){
        Map param = request.getParam();
        LeasesDto leasesDto = map(param, LeasesDto.class);
        Leases lease = leaseRepository.findByLeaseId(leasesDto.getLeaseId());
        if(!bePresent(lease)) withException("");


        return request;
    }

}
