package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ActivityTypes {
    RIDER_SIGN_UP("0"),
    RIDER_REQUEST_DECLINE("1"),
    RIDER_ASSIGNED("2"),
    ESTIMATE_APPROVE_FIRST("3"),
    ESTIMATE_APPROVE_DECLINE1("4"),
    ESTIMATE_APPROVE_DECLINE2("5"),
    RIDER_VERIFIED_REQUEST("6"), RIDER_VERIFIED_REJECTED("7"), RIDER_VERIFIED_COMPLETED("8"),
    RIDER_DEMAND_LEASE_CHECKED("10"), RIDER_DEMAND_LEASE_REJECTED("11"), RIDER_DEMAND_LEASE_COMPLETED("12"), RIDER_DEMAND_LEASE_REQUESTED("13")
    ;

    private String activityType;

    ActivityTypes(String activityType) {
        this.activityType = activityType;
    }

    public static ActivityTypes getRiderActivityTypes(String status) {
        if (status == null) {
            return null;
        }

        for (ActivityTypes ls : ActivityTypes.values()) {
            if (status.equals(ls.getActivityType())) {
                return ls;
            }
        }

        return null;
    }
}
