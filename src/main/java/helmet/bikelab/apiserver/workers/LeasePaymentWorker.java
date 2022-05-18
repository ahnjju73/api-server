package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.LeaseExtras;
import helmet.bikelab.apiserver.domain.lease.LeaseInfo;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.objects.bikelabs.leases.AddUpdateLeaseRequest;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@Service
@RequiredArgsConstructor
public class LeasePaymentWorker extends SessService {

    private final LeasePaymentsRepository leasePaymentsRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final LeaseExtraRepository leaseExtraRepository;
    private final ClientsRepository clientsRepository;
    private final LeaseRepository leaseRepository;
    private final AutoKey autoKey;

    public void payLeaseExtraFeeByExtraId(String extraId, BikeUser session) {
        LeaseExtras leaseExtras = leaseExtraRepository.findByExtraId(extraId);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        if (!bePresent(leaseExtras)) withException("901-001");
        Leases leases = leaseExtras.getLease();
        LeasePayments payment = leaseExtras.getPayment();
        checkLeaseIsConfirmed(leases);
        Integer prevPaidFee = leaseExtras.getPaidFee();
        leaseExtras.setPaidFee(leaseExtras.getExtraFee());
        leaseExtraRepository.save(leaseExtras);
        String content = "<>" + payment.getIndex() + "회차 (" + payment.getPaymentDate().format(dateTimeFormatter) + " - " + leaseExtras.getExtraId() + ")</> 추가 납부료 미납금 <>" + Utils.getCurrencyFormat(leaseExtras.getExtraFee() - prevPaidFee) + "원</>을 완납하였습니다. (<>" + Utils.getCurrencyFormat(prevPaidFee) + "원</> -> <>" + Utils.getCurrencyFormat(leaseExtras.getExtraFee()) + "원</>)";
        List<String> strings = new ArrayList<>();
        strings.add(content);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_EXTRA_PAYMENT, session.getUserNo(), leases.getLeaseNo().toString(), strings));
    }

    public void readLeaseExtraFeeByExtraId(String extraId, BikeUser session) {
        LeaseExtras leaseExtras = leaseExtraRepository.findByExtraId(extraId);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        if (!bePresent(leaseExtras)) withException("901-001");
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

    public void checkLeaseIsConfirmed(Leases leases) {
        if (!LeaseStatusTypes.CONFIRM.equals(leases.getStatus())) withException("901-002");
    }

    public void payLeaseFeeByPaymentId(BikeUser session, Map param) {
        String paymentId = (String) param.get("payment_id");
        Integer payFee = (Integer) param.get("pay_fee");
        String paidType = (String) param.get("paid_type");
        String description = (String) param.get("description");
        List<String> strings = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        LeasePayments byPaymentId = leasePaymentsRepository.findByPaymentId(paymentId);
        Riders rider = byPaymentId.getLease().getBike().getRiders();
        if (!bePresent(byPaymentId)) withException("901-001");
        if (bePresent(rider))
            byPaymentId.setRiderNo(rider.getRiderNo());
        byPaymentId.setPaidType(PaidTypes.getStatus(paidType));
        Leases leases = byPaymentId.getLease();
        checkLeaseIsConfirmed(leases);
        Integer prevPaidFee = byPaymentId.getPaidFee();
        String content = "";
        if ((byPaymentId.getDescription() == null && description != null) || (byPaymentId.getDescription() != null && !byPaymentId.getDescription().equals(byPaymentId.getDescription()))) {
            String log = "";
            if (byPaymentId.getDescription() == null);
            else {
                log = "<>" + byPaymentId.getIndex() + "회차</> 설명이 " + byPaymentId.getDescription() + "에서 " + description + "로 변경되었습니다.";
                byPaymentId.setDescription(description);
                strings.add(log);
            }
        }
        if (payFee <= byPaymentId.getLeaseFee() - byPaymentId.getPaidFee()) {
            byPaymentId.setPaidFee(payFee);
            leasePaymentsRepository.save(byPaymentId);
            content = "<>" + byPaymentId.getIndex() + "회차 (" + byPaymentId.getPaymentDate().format(dateTimeFormatter) + ")</> 납부료 미납금 중 <>" + Utils.getCurrencyFormat(payFee) + "원</>을 납부하였습니다. (<>" + Utils.getCurrencyFormat(prevPaidFee) + "원</> -> <>" + Utils.getCurrencyFormat(byPaymentId.getPaidFee()) + "원</> " + (byPaymentId.getPaidType() == PaidTypes.BANK ? "계좌이체 하였습니다.)" : "라이더가 어플 결제 하였습니다.)");
        } else {
            byPaymentId.setPaidFee(byPaymentId.getLeaseFee());
            leasePaymentsRepository.save(byPaymentId);
            content = "<>" + byPaymentId.getIndex() + "회차 (" + byPaymentId.getPaymentDate().format(dateTimeFormatter) + ")</> 납부료 미납금을 완납하였습니다. (<>" + Utils.getCurrencyFormat(prevPaidFee) + "원</> -> <>" + Utils.getCurrencyFormat(byPaymentId.getPaidFee()) + "원</> " + (byPaymentId.getPaidType() == PaidTypes.BANK ? "계좌이체 하였습니다.)" : "라이더가 어플 결제 하였습니다.)");
        }
        strings.add(content);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, session.getUserNo(), leases.getLeaseNo().toString(), strings));
    }

    public void readLeaseFeeByPaymentId(String paymentId, BikeUser session) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        LeasePayments byPaymentId = leasePaymentsRepository.findByPaymentId(paymentId);
        if (!bePresent(byPaymentId)) withException("901-001");
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

    public void payLeaseFeeMulti(BikeUser session, Map param) {
        List<String> paymentIds = (List<String>) param.get("payments");
        String paidType = (String) param.get("paid_type");
        List<String> strings = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        Leases leases = null;
        for(String paymentId : paymentIds) {
            LeasePayments byPaymentId = leasePaymentsRepository.findByPaymentId(paymentId);
            Riders rider = byPaymentId.getLease().getBike().getRiders();
            if (!bePresent(byPaymentId)) withException("901-001");
            if (bePresent(rider))
                byPaymentId.setRiderNo(rider.getRiderNo());
            byPaymentId.setPaidType(PaidTypes.getStatus(paidType));
            leases = byPaymentId.getLease();
            checkLeaseIsConfirmed(leases);
            Integer prevPaidFee = byPaymentId.getPaidFee();
            byPaymentId.setPaidFee(byPaymentId.getLeaseFee());
            leasePaymentsRepository.save(byPaymentId);
            String content = "<>" + byPaymentId.getIndex() + "회차 (" + byPaymentId.getPaymentDate().format(dateTimeFormatter) + ")</> 납부료 미납금을 완납하였습니다. (<>" + Utils.getCurrencyFormat(prevPaidFee) + "원</> -> <>" + Utils.getCurrencyFormat(byPaymentId.getPaidFee()) + "원</> " + (byPaymentId.getPaidType() == PaidTypes.BANK ? "계좌이체 하였습니다.)" : "라이더가 어플 결제 하였습니다.)");
            strings.add(content);
        }
        if(leases != null)
            bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, session.getUserNo(), leases.getLeaseNo().toString(), strings));
    }

    public void changeClient(BikeUser session, String clientId, String paymentId) {
        LeasePayments byPaymentId = leasePaymentsRepository.findByPaymentId(paymentId);
        Clients preClient = byPaymentId.getClient();
        Clients byClientId = clientsRepository.findByClientId(clientId);
        byPaymentId.setClientNo(byClientId.getClientNo());
        leasePaymentsRepository.save(byPaymentId);
        if(preClient != null) {
            String content = "<>" + byPaymentId.getIndex() + "회차</> 납부 고객정보를 <>" + preClient.getClientInfo().getName() + "</>에서 <>" + byClientId.getClientInfo().getName() + "</>으로 수정하였습니다.";
            bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, session.getUserNo(), byPaymentId.getLeaseNo().toString(), content));
        }else{
            String content = "<>" + byPaymentId.getIndex() + "회차</> 납부 고객정보를 <>" + byClientId.getClientInfo().getName() + "</>으로 추가하였습니다.";
            bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, session.getUserNo(), byPaymentId.getLeaseNo().toString(), content));
        }

    }

    public void doLeasePayment(AddUpdateLeaseRequest addUpdateLeaseRequest, Leases lease, Clients client, LeaseInfo leaseInfo, BikeUser session, List<LeasePayments> newPaymentList) {
        if(ContractTypes.MANAGEMENT.equals(lease.getContractTypes())){
            setPaymentByRentLease(addUpdateLeaseRequest, session, lease, client, leaseInfo, newPaymentList);
        }else {
            setPaymentByRentLeaseNot(addUpdateLeaseRequest, session, lease, client, leaseInfo, newPaymentList);
        }
        leaseInfo.setEndDate(newPaymentList.get(newPaymentList.size() - 1).getPaymentEndDate());
    }

    private void setPaymentByRentLease(AddUpdateLeaseRequest addUpdateLeaseRequest, BikeUser session, Leases lease, Clients client, LeaseInfo leaseInfo, List<LeasePayments> leasePaymentsList) {
        Integer afterDay = 30;
        for (int i = 0; i < addUpdateLeaseRequest.getLeaseInfo().getPeriod(); i++) {
            LeasePayments leasePayment = makePayment(addUpdateLeaseRequest, session, lease, client, leaseInfo, i, leaseInfo.getContractDate().plusDays(i * afterDay), leaseInfo.getContractDate().plusDays((i + 1) * afterDay).minusDays(1));
            leasePaymentsList.add(leasePayment);
        }
    }
    private void setPaymentByRentLeaseNot(AddUpdateLeaseRequest addUpdateLeaseRequest, BikeUser session, Leases lease, Clients client, LeaseInfo leaseInfo, List<LeasePayments> leasePaymentsList) {
        if(PaymentTypes.MONTHLY.equals(PaymentTypes.getPaymentType(addUpdateLeaseRequest.getLeasePrice().getPaymentType()))) {
            for (int i = 0; i < addUpdateLeaseRequest.getLeaseInfo().getPeriod(); i++) {
                LeasePayments leasePayment = makePayment(addUpdateLeaseRequest, session, lease, client, leaseInfo, i, leaseInfo.getContractDate().plusMonths(i), leaseInfo.getContractDate().plusMonths(i + 1).minusDays(1));
                leasePaymentsList.add(leasePayment);
            }
        }else{
            int days = (int)(ChronoUnit.DAYS.between(leaseInfo.getStart(), leaseInfo.getStart().plusMonths(addUpdateLeaseRequest.getLeaseInfo().getPeriod())));
            for(int i = 0 ; i < days; i++){
                LeasePayments leasePayment = makePayment(addUpdateLeaseRequest, session, lease, client, leaseInfo, i, leaseInfo.getContractDate().plusDays(i), leaseInfo.getContractDate().plusDays(i));
                leasePaymentsList.add(leasePayment);
            }
        }
    }

    private LeasePayments makePayment(AddUpdateLeaseRequest addUpdateLeaseRequest, BikeUser session, Leases lease, Clients client, LeaseInfo leaseInfo, int i, LocalDate date, LocalDate endDate) {
        LeasePayments leasePayment = new LeasePayments();
        String paymentId = autoKey.makeGetKey("payment");
        leasePayment.setPaymentId(paymentId);
        leasePayment.setLeaseNo(lease.getLeaseNo());
        leasePayment.setClientNo(client.getClientNo());
        leasePayment.setIndex(i + 1);
        leasePayment.setPaymentDate(date);
        leasePayment.setPaymentEndDate(endDate);
        leasePayment.setInsertedUserNo(session.getUserNo());
        leasePayment.setLeaseFee(addUpdateLeaseRequest.getLeasePrice().getLeaseFee());
        return leasePayment;
    }

    public void changeByStopLease(String leaseId, LocalDateTime date){
        List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(leaseId);
        Leases lease = leaseRepository.findByLeaseId(leaseId);
        PaymentTypes type = lease.getLeasePrice().getType();
        for(int i = 0; i < payments.size(); i++){
            LeasePayments leasePayments = payments.get(i);
            if(leasePayments.getPaymentDate().isAfter(date.toLocalDate())) {
                if (type == PaymentTypes.MONTHLY) {
                    int diffDays = getDiffDays(date.toLocalDate(), leasePayments.getPaymentDate());
                    if (diffDays <= 31) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(leasePayments.getPaymentDate().getYear(), leasePayments.getPaymentDate().getMonthValue() - 2 % 12, 1);
                        int numDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        payments.get(i).setLeaseFee(payments.get(i).getLeaseFee() * diffDays / numDays);
                    } else
                        payments.get(i).setLeaseFee(0);
                } else {
                    payments.get(i).setLeaseFee(0);
                }
            }
            leasePaymentsRepository.save(payments.get(i));
        }
    }

    private int getDiffDays(LocalDate start, LocalDate end){
        Long dayDiffL = ChronoUnit.DAYS.between(start,end);
        int dayDiff = dayDiffL.intValue();
        return dayDiff;
    }

}
