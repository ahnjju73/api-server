package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum LeaseStopStatusTypes {

    CONTINUE("506-001", "이용중"), STOP_CONTINUE("506-002", "중도해지"), FINISH("506-003", "계약끝"), ETC("506-004", "기타");

    private String status;
    private String statusName;

    LeaseStopStatusTypes(String status, String statusName){
        this.status = status;
        this.statusName = statusName;
    }

    public static LeaseStopStatusTypes getLeaseStopStatus(String status){
        if(status == null){
            return null;
        }
        for(LeaseStopStatusTypes lsst: LeaseStopStatusTypes.values()){
            if(status.equals(lsst.getStatus())){
                return lsst;
            }
        }
        return null;
    }
}
