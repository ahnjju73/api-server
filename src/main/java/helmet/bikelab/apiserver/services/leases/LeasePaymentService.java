package helmet.bikelab.apiserver.services.leases;

import com.amazonaws.services.dynamodbv2.xspec.B;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.LeaseExtras;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.ExtraTypes;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.FetchUnpaidLeasesResponse;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeasePaymentDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.PayLeaseRequest;
import helmet.bikelab.apiserver.repositories.LeaseExtraRepository;
import helmet.bikelab.apiserver.repositories.LeasePaymentsRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
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

@Service
@RequiredArgsConstructor
public class LeasePaymentService  extends SessService {
    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final LeaseExtraRepository leaseExtraRepository;
    private final AutoKey autoKey;

    public BikeSessionRequest fetchUnpaidLeases(BikeSessionRequest request){
        List<Leases> leases = leaseRepository.findAll();
        Map response = new HashMap();
        List<FetchUnpaidLeasesResponse> fetchUnpaidLeasesResponses = new ArrayList<>();
        for(Leases lease : leases){
            List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
            int index = 0;
            int unpaidFee = 0;
            int unpaidExtraFee = 0;
            while(index<payments.size()) {
                if(LocalDate.now().isAfter(payments.get(index).getPaymentDate())){
                    break;
                }
                index++;
            }
            for(int i = 0; i < index; i++){
                unpaidFee += payments.get(i).getLeaseFee() - payments.get(i).getPaidFee();
                for(LeaseExtras le : leaseExtraRepository.findAllByPayment_PaymentId(payments.get(i).getPaymentId())){
                    unpaidExtraFee += le.getExtraFee() - le.getPaidFee();
                }
            }
            if(unpaidFee>0){
                FetchUnpaidLeasesResponse fetchUnpaidLeasesResponse = new FetchUnpaidLeasesResponse();
                fetchUnpaidLeasesResponse.setUnpaidLeaseFee(unpaidFee);
                fetchUnpaidLeasesResponse.setLeaseId(lease.getLeaseId());
                fetchUnpaidLeasesResponse.setUnpaidExtraFee(unpaidExtraFee);
                Bikes bike = lease.getBike();
                Clients clients = lease.getClients();
                fetchUnpaidLeasesResponse.setBikeId(bike.getBikeId());
                fetchUnpaidLeasesResponse.setClientId(clients.getClientId());
                BikeDto bikeDto = new BikeDto();
                bikeDto.setBikeId(bike.getBikeId());
                bikeDto.setBikeNum(bike.getCarNum());
                bikeDto.setBikeModel(bike.getCarModelCode());
                ClientDto clientDto = new ClientDto();
                clientDto.setClientName(clients.getClientInfo().getName());
                clientDto.setClientId(clients.getClientId());
                fetchUnpaidLeasesResponse.setClient(clientDto);
                fetchUnpaidLeasesResponse.setBike(bikeDto);
                fetchUnpaidLeasesResponses.add(fetchUnpaidLeasesResponse);
            }
        }
        response.put("unpaid_leases", fetchUnpaidLeasesResponses);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest payLeaseFee(BikeSessionRequest request){
        Map param = request.getParam();
        PayLeaseRequest payLeaseRequest = map(param, PayLeaseRequest.class);
        int paidFee = payLeaseRequest.getPaidFee();
        List<LeasePayments> payments= leasePaymentsRepository.findAllByLease_LeaseId(payLeaseRequest.getLeaseId());
        for(int i = 0; i < payments.size() && paidFee > 0; i++){
            int unpaidFee = payments.get(i).getLeaseFee() - payments.get(i).getPaidFee();
            if (unpaidFee > 0) {
                if(paidFee > unpaidFee){
                    payments.get(i).setPaidFee(payments.get(i).getLeaseFee());
                    paidFee -= unpaidFee;
                    leasePaymentsRepository.save(payments.get(i));
                }else{
                    payments.get(i).setPaidFee(payments.get(i).getPaidFee()+paidFee);
                    leasePaymentsRepository.save(payments.get(i));
                    paidFee = 0;
                    break;
                }
            }

            List<LeaseExtras> extras = leaseExtraRepository.findAllByPayment_PaymentId(payments.get(i).getPaymentId());
            for(LeaseExtras le : extras){
                int unpaidExtra = le.getExtraFee() - le.getPaidFee();
                if(unpaidExtra > 0){
                    if(unpaidExtra < paidFee){
                        le.setPaidFee(le.getExtraFee());
                        paidFee -= unpaidExtra;
                        leaseExtraRepository.save(le);
                    }
                    else{
                        le.setPaidFee(le.getPaidFee() + paidFee);
                        leaseExtraRepository.save(le);
                        paidFee = 0;
                        break;
                    }
                }
            }
        }
        if(paidFee > 0){
            String extraId = autoKey.makeGetKey("lease_extra");
            LeaseExtras leaseExtra = new LeaseExtras();
            leaseExtra.setPaymentNo(payments.get(payments.size() - 1).getPaymentNo());
            leaseExtra.setLeaseNo(payments.get(0).getLeaseNo());
            leaseExtra.setExtraFee(-paidFee);
            leaseExtra.setExtraId(extraId);
            leaseExtra.setExtraTypes(ExtraTypes.ETC);
            leaseExtra.setDescription("초과 금액");
            leaseExtraRepository.save(leaseExtra);
        }
        return request;
    }
}
