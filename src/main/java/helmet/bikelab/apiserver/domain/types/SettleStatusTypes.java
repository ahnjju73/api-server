package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum SettleStatusTypes {
     SCHEDULE("SCHEDULE", "0"), SETTLED("SETTLED", "1");

     private String schedule, status;

    SettleStatusTypes(String schedule, String status) {
        this.schedule = schedule;
        this.status = status;
    }

    public static SettleStatusTypes getStatusType(String status){
        if(status == null)
            return null;
        for(SettleStatusTypes sst : SettleStatusTypes.values()){
            if(sst.getStatus().equals(status))
                return sst;
        }
        return null;
    }
}
