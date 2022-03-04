package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.lease.LeaseExtras;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.ExtraTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStopStatusTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.AddUpdateExtraRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.FetchLeaseExtraRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.FetchLeaseExtraResponse;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeasePaymentDto;
import helmet.bikelab.apiserver.repositories.BikeUserLogRepository;
import helmet.bikelab.apiserver.repositories.LeaseExtraRepository;
import helmet.bikelab.apiserver.repositories.LeasePaymentsRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@RequiredArgsConstructor
@Service
public class LeaseExtraService extends SessService {
    private final LeaseExtraRepository leaseExtraRepository;
    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final AutoKey autoKey;

    @Transactional
    public BikeSessionRequest addLeaseExtra(BikeSessionRequest request){
        ArrayList<String> logList = new ArrayList<>();
        Map param = request.getParam();
        AddUpdateExtraRequest addUpdateExtraRequest = map(param, AddUpdateExtraRequest.class);
        LeaseExtras leaseExtras = new LeaseExtras();
        String extraId = autoKey.makeGetKey("lease_extra");
        Leases lease = leaseRepository.findByLeaseId(addUpdateExtraRequest.getLeaseId());
        if(!lease.getLeaseStopStatus().equals(LeaseStopStatusTypes.CONTINUE)) withException("860-001");
        addUpdateExtraRequest.checkValidation();
        LeasePayments leasePayment = leasePaymentsRepository.findByPaymentId(addUpdateExtraRequest.getPaymentId());
        leaseExtras.setExtraId(extraId);
        leaseExtras.setLeaseNo(lease.getLeaseNo());
        leaseExtras.setExtraFee(addUpdateExtraRequest.getExtraFee());
        leaseExtras.setPaymentNo(leasePayment.getPaymentNo());
        leaseExtras.setExtraTypes(ExtraTypes.getExtraType(addUpdateExtraRequest.getExtraType()));
        leaseExtras.setPaidFee(addUpdateExtraRequest.getPaidFee());
        leaseExtras.setDescription(addUpdateExtraRequest.getDescription());
        String log = leasePayment.getIndex() + "회차에 " + leaseExtras.getExtraTypes().getReason() + "으로 " + Utils.getCurrencyFormat(leaseExtras.getExtraFee()) + "원의 추가금이 발생하였습니다.";
        logList.add(log);
        leaseExtraRepository.save(leaseExtras);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), logList));
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
            fetchLeaseExtraResponse.setExtraId(le.getExtraId());
            fetchLeaseExtraResponse.setExtraFee(le.getExtraFee());
            fetchLeaseExtraResponse.setExtraType(le.getExtraTypes().getExtra());
            fetchLeaseExtraResponse.setDescription(le.getDescription());
            fetchLeaseExtraResponses.add(fetchLeaseExtraResponse);
        }
        response.put("extras", fetchLeaseExtraResponses);
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchExceedLeaseExtras(BikeSessionRequest request){
        Map response = new HashMap();
        List<LeaseExtras> leaseExtras = leaseExtraRepository.findAll();
        List<FetchLeaseExtraResponse> fetchLeaseExtraResponses = new ArrayList<>();
        for(LeaseExtras le : leaseExtras){
            if(le.getExtraFee()<0){
                LeasePayments payment = leasePaymentsRepository.findByPaymentId(le.getPayment().getPaymentId());
                FetchLeaseExtraResponse fetchLeaseExtraResponse = new FetchLeaseExtraResponse();
                LeasePaymentDto dto = new LeasePaymentDto();
                dto.setIdx(payment.getIndex());
                dto.setPaymentId(payment.getPaymentId());
                dto.setPaymentDate(payment.getPaymentDate());
                fetchLeaseExtraResponse.setPayment(dto);
                fetchLeaseExtraResponse.setExtraId(le.getExtraId());
                fetchLeaseExtraResponse.setExtraFee(le.getExtraFee());
                fetchLeaseExtraResponse.setExtraType(le.getExtraTypes().getExtra());
                fetchLeaseExtraResponse.setDescription(le.getDescription());
                fetchLeaseExtraResponses.add(fetchLeaseExtraResponse);
            }
        }
        response.put("extras_to_refund", fetchLeaseExtraResponses);
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

    public BikeSessionRequest fetchDetailExtra(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        FetchLeaseExtraRequest fetchLeaseExtraRequest = map(param, FetchLeaseExtraRequest.class);
        LeaseExtras extra = leaseExtraRepository.findByExtraId(fetchLeaseExtraRequest.getExtraId());
        LeasePayments payment = leasePaymentsRepository.findByPaymentId(extra.getPayment().getPaymentId());
        FetchLeaseExtraResponse fetchLeaseExtraResponse = new FetchLeaseExtraResponse();
        LeasePaymentDto dto = new LeasePaymentDto();
        dto.setIdx(payment.getIndex());
        dto.setPaymentId(payment.getPaymentId());
        dto.setPaymentDate(payment.getPaymentDate());
        fetchLeaseExtraResponse.setExtraId(extra.getExtraId());
        fetchLeaseExtraResponse.setPayment(dto);
        fetchLeaseExtraResponse.setExtraFee(extra.getExtraFee());
        fetchLeaseExtraResponse.setPaidFee(extra.getPaidFee());
        fetchLeaseExtraResponse.setExtraType(extra.getExtraTypes().getExtra());
        fetchLeaseExtraResponse.setDescription(extra.getDescription());
        response.put("extras", fetchLeaseExtraResponse);
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
            LeasePayments payment = le.getPayment();
            FetchLeaseExtraResponse fetchLeaseExtraResponse = new FetchLeaseExtraResponse();
            LeasePaymentDto dto = new LeasePaymentDto();
            dto.setIdx(payment.getIndex());
            dto.setPaymentId(payment.getPaymentId());
            dto.setPaymentDate(payment.getPaymentDate());
            dto.setPaidFee(payment.getPaidFee());
            dto.setLeaseFee(payment.getLeaseFee());
            fetchLeaseExtraResponse.setPayment(dto);
            fetchLeaseExtraResponse.setPaidFee(le.getPaidFee());
            fetchLeaseExtraResponse.setExtraId(le.getExtraId());
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
        ArrayList<String> logList = new ArrayList<>();
        Map param = request.getParam();
        AddUpdateExtraRequest addUpdateExtraRequest = map(param, AddUpdateExtraRequest.class);
        LeaseExtras extra = leaseExtraRepository.findByExtraId(addUpdateExtraRequest.getExtraId());
        LeasePayments payment = leasePaymentsRepository.findByPaymentId(addUpdateExtraRequest.getPaymentId());
        Leases lease = payment.getLease();
        if(!lease.getLeaseStopStatus().equals(LeaseStopStatusTypes.CONTINUE)) withException("860-001");
        addUpdateExtraRequest.checkValidation();
        if(!addUpdateExtraRequest.getExtraType().equals(extra.getExtraTypes().getExtra())){
            logList.add(request.getSessionUser().getBikeUserInfo().getName() + "님께서 " + payment.getIndex() + "회차에 추가금 종류를 <>\"" + extra.getExtraTypes().getReason() + "\"</>에서 <>\"" + ExtraTypes.getExtraType(addUpdateExtraRequest.getExtraType()).getReason() +"\"</>으로 변경 했습니다.");
        }
        if(!addUpdateExtraRequest.getExtraFee().equals(extra.getExtraFee())){
            logList.add(request.getSessionUser().getBikeUserInfo().getName() + "님께서 " + payment.getIndex() + "회차에 추가금을 <>" + Utils.getCurrencyFormat(extra.getExtraFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(addUpdateExtraRequest.getExtraFee()) +"원</>으로 변경 했습니다.");
        }
        if(!addUpdateExtraRequest.getPaidFee().equals(extra.getPaidFee())){
            logList.add(request.getSessionUser().getBikeUserInfo().getName() + "님께서 " + payment.getIndex() + "회차에 납부된 추가금을 <>" + Utils.getCurrencyFormat(extra.getPaidFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(addUpdateExtraRequest.getPaidFee()) +"원</>으로 변경 했습니다.");
        }
        if(addUpdateExtraRequest.getDescription() != null && extra.getDescription() == null){
            logList.add(request.getSessionUser().getBikeUserInfo().getName() + "님께서 " + payment.getIndex() + "회차에 추가금 설명을 <> \"" + addUpdateExtraRequest.getDescription() +"\"</>추가 했습니다.");
        }
        else if(extra.getDescription() != null && !addUpdateExtraRequest.getDescription().equals(extra.getDescription())){
            logList.add(request.getSessionUser().getBikeUserInfo().getName() + "님께서 " + payment.getIndex() + "회차에 추가금 설명을 <> \"" + extra.getDescription() + "\"</>에서 <>\"" + addUpdateExtraRequest.getDescription() +"\"</>으로 변경 했습니다.");
        }
        extra.setExtraFee(addUpdateExtraRequest.getExtraFee());
        extra.setExtraTypes(ExtraTypes.getExtraType(addUpdateExtraRequest.getExtraType()));
        extra.setPaidFee(addUpdateExtraRequest.getPaidFee());
        extra.setDescription(addUpdateExtraRequest.getDescription());
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), logList));
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

    private void updateExtraLog(AddUpdateExtraRequest request, BikeUser user, LeasePayments payment, Integer extraFee, ExtraTypes extraTypes, Integer paidFee, String description, BikeUser bikeUser){
        ArrayList<String> logList = new ArrayList<>();
        String head = "<>" + user.getBikeUserInfo().getName() + "</>님께서 ";
        LeasePayments payments = leasePaymentsRepository.findByPaymentId(request.getPaymentId());
        if(bePresent(payments) && !payments.getPaymentId().equals(payment.getPaymentId())){
            logList.add(head + "<>" + payment.getIndex()+"</>회차에서 <>" + payments.getIndex() + "</>회차로 변경하였습니다.");
        }
        if(bePresent(request.getExtraFee()) && request.getExtraFee() != extraFee){
            logList.add(head + "추가금을 <>" + extraFee+ "</>에서 <>" + request.getExtraFee() + "</>로 변경하였습니다.");
        }
        if(bePresent(request.getExtraType()) && request.getExtraType().equals(extraTypes.getExtra())){
            logList.add(head + "추가금 타입을 <>" + extraTypes.getReason() + "</>에서 <>" + ExtraTypes.getExtraType(request.getExtraType()).getReason() + "</>으로 변경하였습니다.");
        }
        if(bePresent(request.getPaidFee()) && request.getPaidFee() != paidFee){
            logList.add(head + "납부금을 <>" + paidFee + "</>에서 <>" + request.getPaidFee() + "<>로 변경하였습니다.");
        }
        if(bePresent(request.getDescription()) && request.getDescription().equals(description)){
            logList.add(head + "설명을 <>\"" + description + "\"</>에서 <>\"" + request.getDescription() + "\"</>로 변경하였습니다.");
        }
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, user.getUserNo(), payment.getPaymentNo().toString(), logList));
    }


}
