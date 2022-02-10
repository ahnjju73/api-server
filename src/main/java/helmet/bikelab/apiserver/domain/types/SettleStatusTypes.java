package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum SettleStatusTypes {
    PENDING("0"), COMPLETED("1");

    private String status;

    SettleStatusTypes(String status) {
        this.status = status;
    }

    public static SettleStatusTypes getSettleStatusTypes(String status) {
        if (status == null) {
            return null;
        }

        for (SettleStatusTypes ls : SettleStatusTypes.values()) {
            if (status.equals(ls.getStatus())) {
                return ls;
            }
        }
        return null;
    }
}
