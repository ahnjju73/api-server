package helmet.bikelab.apiserver.schedulers;

import helmet.bikelab.apiserver.domain.types.BikeTypes;
import helmet.bikelab.apiserver.schedulers.internal.WorkspaceQuartz;
import helmet.bikelab.apiserver.utils.PushComponent;
import helmet.bikelab.apiserver.utils.Senders;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class UnPaidLeaseSchedulerService extends WorkspaceQuartz {

    private final Senders senders;
    private final PushComponent pushComponent;


    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) {
        daysAgoToClient(7);
        daysAgoToClient(3);
        daysAgoToRider(7);
        daysAgoToRider(3);
    }

    public void daysAgoToClient(Integer days){
        Map param = new HashMap<>();
        param.put("days_ago", days);
        List<Map> list = getList("quartz.leases.fetchUnPaidLeasesByDaysAgo", param);
        list.forEach(elm -> {
            List<Map> leases = (List)elm.get("leases");
            if(bePresent(leases)){
                MessageObject messageObject = new MessageObject();
                int total = leases.size();
                int sumFee = leases.stream().mapToInt(item -> {
                    int leaseFee = (int)item.get("lease_fee");
                    int paidFee = (int)item.get("paid_fee");
                    return leaseFee - paidFee;
                }).sum();
                Map lastObject = leases.get(leases.size() - 1);
                messageObject.setDays(days);
                messageObject.setLastLease(lastObject);
                messageObject.setToName((String)elm.get("client_name"));
                messageObject.setToPhone((String)elm.get("phone"));
                messageObject.setToEmail((String)elm.get("manager_email"));
                messageObject.setTotal(total);
                messageObject.setSumFee(sumFee);
                String phoneMessage = messageTo(messageObject);
                senders.withPhoneMessage(phoneMessage, messageObject.getToPhone());
            }
        });
    }

    public void daysAgoToRider(Integer days){
        Map param = new HashMap<>();
        param.put("days_ago", days);
        List<Map> list = getList("quartz.leases.fetchUnPaidLeasesToRiderByDaysAgo", param);
        list.forEach(elm -> {
            List<Map> leases = (List)elm.get("leases");
            if(bePresent(leases)){
                MessageObject messageObject = new MessageObject();
                int total = leases.size();
                int sumFee = leases.stream().mapToInt(item -> {
                    int leaseFee = (int)item.get("lease_fee");
                    int paidFee = (int)item.get("paid_fee");
                    return leaseFee - paidFee;
                }).sum();
                Map lastObject = leases.get(leases.size() - 1);
                messageObject.setDays(days);
                messageObject.setLastLease(lastObject);
                messageObject.setToName((String)elm.get("rider_name"));
                messageObject.setToPhone((String)elm.get("rider_phone"));
                messageObject.setToEmail((String)elm.get("rider_email"));
                messageObject.setNotificationToken((String)elm.get("notification_token"));
                messageObject.setTotal(total);
                messageObject.setSumFee(sumFee);
                String message = messageTo(messageObject);
                pushComponent.pushNotification(messageObject.getNotificationToken(), "리스료 납부 " + days + "전 입니다.", message);
            }
        });
    }

    public String messageTo(MessageObject messageObject){
        return messageObject.getToName() + "님! 안녕하세요. 한국모터사이클서비스입니다. " +
                (messageObject.total <= 1 ? "리스번호 [" + messageObject.lastLeaseId + "] "  :
                        "리스번호 [" + messageObject.lastLeaseId + "] 외 " + (messageObject.total - 1) + "개의 ") +
                "리스료 납부일이 " + messageObject.getDays() +
                "일후로 다가왔습니다.";
    }

    @Data
    public class MessageObject{
        private Integer days;
        private String toName;
        private String notificationToken;
        private String toPhone;
        private String toEmail;
        private Integer total = 0;
        private Integer sumFee = 0;
        private String lastLeaseId;
        private String lastLeasePaymentIndex;
        private String lastBikeNumber;
        private String lastBikeVimNum;
        private BikeTypes lastBikeType;
        private String lastVolume;
        private String lastCarModel;
        private String lastManufacturer;

        public void setLastLease(Map param){
            String bikeNumber = (String)param.get("number");
            String bikeVimNum = (String)param.get("vim_num");
            String carModel = (String)param.get("car_model");
            String bikeTypeCode = (String)param.get("bike_type_code");
            String volume = param.get("volume").toString();
            String manufacturer = (String)param.get("manuf");
            String idx = param.get("idx").toString();
            String leaseId = (String)param.get("lease_id");
            this.lastBikeNumber = bikeNumber;
            this.lastBikeVimNum = bikeVimNum;
            this.lastCarModel = carModel;
            this.lastVolume = volume;
            this.lastManufacturer = manufacturer;
            this.lastLeasePaymentIndex = idx;
            this.lastLeaseId = leaseId;
            this.lastBikeType = BikeTypes.getType(bikeTypeCode);
        }

    }

}
