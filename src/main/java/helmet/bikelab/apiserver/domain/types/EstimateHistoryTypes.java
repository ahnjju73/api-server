package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum EstimateHistoryTypes {

    CREATED("0", "생성완료"),
    REQUESTED("1", "1차 승인요청"), DENIED("2", "1차 승인거절"), CONFIRMED("3", "1차 승인완료"),
    PAYMENT("4", "결제요청"), DECLINED_PAYMENT("5", "결제요청거절"), PAID("6", "결제완료"),
    COMPLETED("7", "수리완료"), RELEASED("8", "출고완료")
    ;

    private String historyType;
    private String historyTypeName;

    EstimateHistoryTypes(String historyType, String historyTypeName) {
        this.historyType = historyType;
        this.historyTypeName = historyTypeName;
    }

    public static EstimateHistoryTypes getEstimateHistoryTypes(String status) {
        if (status == null) {
            return null;
        }

        for (EstimateHistoryTypes yn : EstimateHistoryTypes.values()) {
            if (status.equals(yn.getHistoryType())) {
                return yn;
            }
        }

        return null;
    }


}
