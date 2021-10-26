package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.lease.LeaseExtras;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import helmet.bikelab.apiserver.domain.types.PaidTypes;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.BikeUserLogRepository;
import helmet.bikelab.apiserver.repositories.LeaseExtraRepository;
import helmet.bikelab.apiserver.repositories.LeasePaymentsRepository;
import helmet.bikelab.apiserver.repositories.RiderRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.Utils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@Service
@RequiredArgsConstructor
public class LeasePaymentWorker extends SessService {

    private final LeasePaymentsRepository leasePaymentsRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final LeaseExtraRepository leaseExtraRepository;
    private final RiderRepository riderRepository;

    public void payLeaseExtraFeeByExtraId(String extraId, BikeUser session){
        LeaseExtras leaseExtras = leaseExtraRepository.findByExtraId(extraId);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        if(!bePresent(leaseExtras)) withException("901-001");
        Leases leases = leaseExtras.getLease();
        LeasePayments payment = leaseExtras.getPayment();
        checkLeaseIsConfirmed(leases);
        Integer prevPaidFee = leaseExtras.getPaidFee();
        leaseExtras.setPaidFee(leaseExtras.getExtraFee());
        leaseExtraRepository.save(leaseExtras);
        String content = "<>" + payment.getIndex() + "회차 (" + payment.getPaymentDate().format(dateTimeFormatter) + " - " + leaseExtras.getExtraId() + ")</> 추가 납부료 미납금 <>" + Utils.getCurrencyFormat(leaseExtras.getExtraFee() - prevPaidFee) + "원</>을 완납하였습니다. (<>" + Utils.getCurrencyFormat(prevPaidFee) + "원</> -> <>" + Utils.getCurrencyFormat(leaseExtras.getExtraFee())+ "원</>)";
        List<String> strings = new ArrayList<>();
        strings.add(content);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_EXTRA_PAYMENT, session.getUserNo(), leases.getLeaseNo().toString(), strings));
    }

    public void readLeaseExtraFeeByExtraId(String extraId, BikeUser session){
        LeaseExtras leaseExtras = leaseExtraRepository.findByExtraId(extraId);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        if(!bePresent(leaseExtras)) withException("901-001");
        Leases leases = leaseExtras.getLease();
        LeasePayments payment = leaseExtras.getPayment();
        checkLeaseIsConfirmed(leases);
        leaseExtras.setRead(true);
        leaseExtras.setReadUserNo(session.getUserNo());
        leaseExtraRepository.save(leaseExtras);
        String content = "<>" + payment.getIndex() + "회차 (" + payment.getPaymentDate().format(dateTimeFormatter) + ")</> 납부료 추가미납금 <>납부확인처리</>를 하였습니다.";
        List<String> strings = new ArrayList<>();
        strings.add(content);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_EXTRA_PAYMENT, session.getUserNo(), leases.getLeaseNo().toString(), strings));
    }

    public void checkLeaseIsConfirmed(Leases leases){
        if(!LeaseStatusTypes.CONFIRM.equals(leases.getStatus())) withException("901-002");
    }

    public void payLeaseFeeByPaymentId(BikeUser session, Map param){
        String paymentId = (String)param.get("payment_id");
        Integer payFee = (Integer) param.get("pay_fee");
        String paidType = (String)param.get("paid_type");
        List<String> strings = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        LeasePayments byPaymentId = leasePaymentsRepository.findByPaymentId(paymentId);
        Riders rider = byPaymentId.getLease().getBike().getRiders();
        if(!bePresent(byPaymentId)) withException("901-001");
        if(!bePresent(rider)) withException("901-003");
        byPaymentId.setRiderNo(rider.getRiderNo());
        byPaymentId.setPaidType(PaidTypes.getStatus(paidType));
        Leases leases = byPaymentId.getLease();
        checkLeaseIsConfirmed(leases);
        Integer prevPaidFee = byPaymentId.getPaidFee();
        String content = "";
        if(payFee <= byPaymentId.getLeaseFee() - byPaymentId.getPaidFee()) {
            byPaymentId.setPaidFee(payFee);
            leasePaymentsRepository.save(byPaymentId);
            content = "<>" + byPaymentId.getIndex() + "회차 (" + byPaymentId.getPaymentDate().format(dateTimeFormatter) + ")</> 납부료 미납금 중 <>" + Utils.getCurrencyFormat(payFee) + "원</>을 납부하였습니다. (<>" + Utils.getCurrencyFormat(prevPaidFee) + "원</> -> <>" + Utils.getCurrencyFormat(byPaymentId.getPaidFee()) + "원</> " + (byPaymentId.getPaidType() == PaidTypes.BANK? "계좌이체 하였습니다.)":"<>" + rider.getRiderInfo().getName() + "</>님이 " +  "어플 결제 하였습니다.)");
        }
        else{
            byPaymentId.setPaidFee(byPaymentId.getLeaseFee());
            leasePaymentsRepository.save(byPaymentId);
            content = "<>" + byPaymentId.getIndex() + "회차 (" + byPaymentId.getPaymentDate().format(dateTimeFormatter) + ")</> 납부료 미납금을 완납하였습니다. (<>" + Utils.getCurrencyFormat(prevPaidFee) + "원</> -> <>" + Utils.getCurrencyFormat(byPaymentId.getPaidFee()) + "원</> " + (byPaymentId.getPaidType() == PaidTypes.BANK? "계좌이체 하였습니다.)":"<>" + rider.getRiderInfo().getName() + "</>님이 " +  "어플 결제 하였습니다.)");
        }
        strings.add(content);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, session.getUserNo(), leases.getLeaseNo().toString(), strings));
    }

    public void readLeaseFeeByPaymentId(String paymentId, BikeUser session){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        LeasePayments byPaymentId = leasePaymentsRepository.findByPaymentId(paymentId);
        if(!bePresent(byPaymentId)) withException("901-001");
        Leases leases = byPaymentId.getLease();
        checkLeaseIsConfirmed(leases);
        byPaymentId.setRead(Boolean.TRUE);
        byPaymentId.setReadUserNo(session.getUserNo());
        leasePaymentsRepository.save(byPaymentId);
        String content = "<>" + byPaymentId.getIndex() + "회차 (" + byPaymentId.getPaymentDate().format(dateTimeFormatter) + ")</> 납부료 미납금 <>납부확인처리</>를 하였습니다.";
        List<String> strings = new ArrayList<>();
        strings.add(content);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, session.getUserNo(), leases.getLeaseNo().toString(), strings));
    }

}
