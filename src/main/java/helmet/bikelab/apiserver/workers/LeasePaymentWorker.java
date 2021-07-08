package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.lease.LeaseExtras;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.BikeUserLogRepository;
import helmet.bikelab.apiserver.repositories.LeaseExtraRepository;
import helmet.bikelab.apiserver.repositories.LeasePaymentsRepository;
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

    public void checkLeaseIsConfirmed(Leases leases){
        if(!LeaseStatusTypes.CONFIRM.equals(leases.getStatus())) withException("901-002");
    }

    public void payLeaseFeeByPaymentId(String paymentId, BikeUser session){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        LeasePayments byPaymentId = leasePaymentsRepository.findByPaymentId(paymentId);
        if(!bePresent(byPaymentId)) withException("901-001");
        Leases leases = byPaymentId.getLease();
        checkLeaseIsConfirmed(leases);

        Integer prevPaidFee = byPaymentId.getPaidFee();
        byPaymentId.setPaidFee(byPaymentId.getLeaseFee());
        leasePaymentsRepository.save(byPaymentId);
        String content = "<>" + byPaymentId.getIndex() + "회차 (" + byPaymentId.getPaymentDate().format(dateTimeFormatter) + ")</> 납부료 미납금 <>" + Utils.getCurrencyFormat(byPaymentId.getLeaseFee() - prevPaidFee) + "원</>을 완납하였습니다. (<>" + Utils.getCurrencyFormat(prevPaidFee) + "원</> -> <>" + Utils.getCurrencyFormat(byPaymentId.getLeaseFee())+ "원</>)";
        List<String> strings = new ArrayList<>();
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
