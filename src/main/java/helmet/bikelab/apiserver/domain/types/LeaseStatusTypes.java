package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum LeaseStatusTypes {
    IN_PROGRESS("550-001"), PENDING("550-002"), CONFIRM("550-003"), DECLINE("550-004");

    private String status;

    LeaseStatusTypes(String status) {
        this.status = status;
    }

    public static LeaseStatusTypes getLeaseStatus(String status) {
        if (status == null) {
            return null;
        }
        for (LeaseStatusTypes ls : LeaseStatusTypes.values()) {
            if (status.equals(ls.getStatus())) {
                return ls;
            }
        }
        return null;
    }
}
