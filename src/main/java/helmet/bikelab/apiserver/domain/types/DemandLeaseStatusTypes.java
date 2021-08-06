package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum DemandLeaseStatusTypes {

    IN_PROGRESS("0", "작성중"), PENDING("1", "대기중"), REJECTED("2", "반려"), COMPLETED("3", "리스신청 완료");

    private String status;
    private String statusName;

    DemandLeaseStatusTypes(String status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public static DemandLeaseStatusTypes getDemandLeaseStatusTypes(String status){
        if(status == null){
            return null;
        }
        for(DemandLeaseStatusTypes ct: DemandLeaseStatusTypes.values()){
            if(status.equals(ct.getStatus())){
                return ct;
            }
        }
        return null;
    }
}
