package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum EstimateStatusTypes {
    IN_PROGRESS("0", "진행중"),
    REQUESTED("1", "1차 승인요청"),
    DENIED("2", "1차 승인거절"),
    CONFIRMED("3", "1차 승인완료"),
    PAYMENT("4", "결제요청"),
    DECLINED_PAYMENT("5", "결제요청거절"),
    PAID("6", "결제완료"),
    COMPLETED("7", "수리완료"),
    ;

    private String status;
    private String statusName;

    EstimateStatusTypes(String status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public static EstimateStatusTypes getEstimateStatusTypes(String status) {
        if (status == null) {
            return null;
        }
        for (EstimateStatusTypes as : EstimateStatusTypes.values()) {
            if (status.equals(as.getStatus())) {
                return as;
            }
        }
        return null;
    }

}
