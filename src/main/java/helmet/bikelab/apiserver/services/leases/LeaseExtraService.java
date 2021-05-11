package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.lease.LeaseExtras;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.ExtraTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.AddUpdateExtraRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.FetchLeaseExtraRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.FetchLeaseExtraResponse;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeasePaymentDto;
import helmet.bikelab.apiserver.repositories.LeaseExtraRepository;
import helmet.bikelab.apiserver.repositories.LeasePaymentsRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class LeaseExtraService extends SessService {
    private final LeaseExtraRepository leaseExtraRepository;
    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final AutoKey autoKey;

    @Transactional
    public BikeSessionRequest addLeaseExtra(BikeSessionRequest request){
        Map param = request.getParam();
        AddUpdateExtraRequest addUpdateExtraRequest = map(param, AddUpdateExtraRequest.class);
        LeaseExtras leaseExtras = new LeaseExtras();
        String extraId = autoKey.makeGetKey("lease_extra");
        Leases lease = leaseRepository.findByLeaseId(addUpdateExtraRequest.getLeaseId());
        LeasePayments leasePayment = leasePaymentsRepository.findByPaymentId(addUpdateExtraRequest.getPaymentId());
        leaseExtras.setExtraId(extraId);
        leaseExtras.setLeaseNo(lease.getLeaseNo());
        leaseExtras.setExtraFee(addUpdateExtraRequest.getExtraFee());
        leaseExtras.setPaymentNo(leasePayment.getPaymentNo());
        leaseExtras.setExtraTypes(ExtraTypes.getExtraType(addUpdateExtraRequest.getExtraType()));
        leaseExtras.setPaidFee(addUpdateExtraRequest.getPaidFee());
        leaseExtras.setDescription(addUpdateExtraRequest.getDescription());
        leaseExtraRepository.save(leaseExtras);
        return request;
    }

    public BikeSessionRequest fetchLeaseExtras(BikeSessionRequest request){
        Map response = new HashMap();
        List<LeaseExtras> leaseExtras = leaseExtraRepository.findAll();
        List<FetchLeaseExtraResponse> fetchLeaseExtraResponses = new ArrayList<>();
        for(LeaseExtras le : leaseExtras){
            LeasePayments payment = leasePaymentsRepository.findByPaymentId(le.getPayment().getPaymentId());
            FetchLeaseExtraResponse fetchLeaseExtraResponse = new FetchLeaseExtraResponse();
            LeasePaymentDto dto = new LeasePaymentDto();
            dto.setIdx(payment.getIndex());
            dto.setPaymentId(payment.getPaymentId());
            dto.setPaymentDate(payment.getPaymentDate());
            fetchLeaseExtraResponse.setPayment(dto);
            fetchLeaseExtraResponse.setExtraFee(le.getExtraFee());
            fetchLeaseExtraResponse.setExtraType(le.getExtraTypes().getExtra());
            fetchLeaseExtraResponse.setDescription(le.getDescription());
            fetchLeaseExtraResponses.add(fetchLeaseExtraResponse);
        }
        response.put("extras", fetchLeaseExtraResponses);
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchLeaseExtrasByLeaseId(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        FetchLeaseExtraRequest fetchLeaseExtraRequest = map(param, FetchLeaseExtraRequest.class);
        List<LeaseExtras> extras = leaseExtraRepository.findAllByLease_LeaseId(fetchLeaseExtraRequest.getLeaseId());
        List<FetchLeaseExtraResponse> fetchLeaseExtraResponses = new ArrayList<>();
        for(LeaseExtras le : extras){
            LeasePayments payment = leasePaymentsRepository.findByPaymentId(le.getPayment().getPaymentId());
            FetchLeaseExtraResponse fetchLeaseExtraResponse = new FetchLeaseExtraResponse();
            LeasePaymentDto dto = new LeasePaymentDto();
            dto.setIdx(payment.getIndex());
            dto.setPaymentId(payment.getPaymentId());
            dto.setPaymentDate(payment.getPaymentDate());
            fetchLeaseExtraResponse.setPayment(dto);
            fetchLeaseExtraResponse.setExtraFee(le.getExtraFee());
            fetchLeaseExtraResponse.setExtraType(le.getExtraTypes().getExtra());
            fetchLeaseExtraResponse.setDescription(le.getDescription());
            fetchLeaseExtraResponses.add(fetchLeaseExtraResponse);
        }
        response.put("extras", fetchLeaseExtraResponses);
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchLeaseExtrasByPaymentId(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        FetchLeaseExtraRequest fetchLeaseExtraRequest = map(param, FetchLeaseExtraRequest.class);
        List<LeaseExtras> extras = leaseExtraRepository.findAllByPayment_PaymentId(fetchLeaseExtraRequest.getPaymentId());
        List<FetchLeaseExtraResponse> fetchLeaseExtraResponses = new ArrayList<>();
        for(LeaseExtras le : extras){
            LeasePayments payment = leasePaymentsRepository.findByPaymentId(le.getPayment().getPaymentId());
            FetchLeaseExtraResponse fetchLeaseExtraResponse = new FetchLeaseExtraResponse();
            LeasePaymentDto dto = new LeasePaymentDto();
            dto.setIdx(payment.getIndex());
            dto.setPaymentId(payment.getPaymentId());
            dto.setPaymentDate(payment.getPaymentDate());
            dto.setPaidFee(payment.getPaidFee());
            dto.setLeaseFee(payment.getLeaseFee());
            fetchLeaseExtraResponse.setPayment(dto);
            fetchLeaseExtraResponse.setExtraFee(le.getExtraFee());
            fetchLeaseExtraResponse.setExtraType(le.getExtraTypes().getExtra());fetchLeaseExtraResponse.setDescription(le.getDescription());
            fetchLeaseExtraResponses.add(fetchLeaseExtraResponse);
        }
        response.put("extras", fetchLeaseExtraResponses);
        request.setResponse(response);
        return request;
    }



    @Transactional
    public BikeSessionRequest updateLeaseExtra(BikeSessionRequest request){
        Map param = request.getParam();
        AddUpdateExtraRequest addUpdateExtraRequest = map(param, AddUpdateExtraRequest.class);
        LeaseExtras extra = leaseExtraRepository.findByExtraId(addUpdateExtraRequest.getExtraId());
        LeasePayments payment = leasePaymentsRepository.findByPaymentId(addUpdateExtraRequest.getPaymentId());
        extra.setPaymentNo(payment.getPaymentNo());
        extra.setLeaseNo(payment.getLeaseNo());
        extra.setExtraFee(addUpdateExtraRequest.getExtraFee());
        extra.setExtraTypes(ExtraTypes.getExtraType(addUpdateExtraRequest.getExtraType()));
        extra.setPaidFee(addUpdateExtraRequest.getPaidFee());
        extra.setDescription(addUpdateExtraRequest.getDescription());
        leaseExtraRepository.save(extra);
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteLeaseExtra(BikeSessionRequest request){
        Map param = request.getParam();
        FetchLeaseExtraRequest fetchLeaseExtraRequest = map(param, FetchLeaseExtraRequest.class);
        LeaseExtras extra = leaseExtraRepository.findByExtraId(fetchLeaseExtraRequest.getExtraId());
        if(extra != null)
            leaseExtraRepository.delete(extra);
        return request;
    }
}
