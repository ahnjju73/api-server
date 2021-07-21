package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum LeaseStopStatusTypes {
    CONTINUE("506-001"), STOP_CONTINUE("506-002"), FINSISH("506-003"), ETC("506-004");

    String status;

    LeaseStopStatusTypes(String status){
        this.status = status;
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
