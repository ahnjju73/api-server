package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.BikeUserLogRepository;
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
    public void payLeaseFeeByPaymentId(String paymentId, BikeUser session){
        LeasePayments byPaymentId = leasePaymentsRepository.findByPaymentId(paymentId);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        if(!bePresent(byPaymentId)) withException("901-001");
        Leases leases = byPaymentId.getLease();
        if(!LeaseStatusTypes.CONFIRM.equals(leases.getStatus())) withException("901-002");

        Integer prevPaidFee = byPaymentId.getPaidFee();
        byPaymentId.setPaidFee(byPaymentId.getLeaseFee());
        leasePaymentsRepository.save(byPaymentId);
        String content = "<>" + byPaymentId.getIndex() + "</>회차 <>(" + byPaymentId.getPaymentDate().format(dateTimeFormatter) + ")</> 납부료 미납금 <>" + Utils.getCurrencyFormat(byPaymentId.getLeaseFee() - prevPaidFee) + "원</>을 완납하였습니다. (<>" + Utils.getCurrencyFormat(prevPaidFee) + "원</> -> <>" + Utils.getCurrencyFormat(byPaymentId.getLeaseFee())+ "원</>)";
        List<String> strings = new ArrayList<>();
        strings.add(content);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, session.getUserNo(), leases.getLeaseNo().toString(), strings));
    }

}
