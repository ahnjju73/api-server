package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ActivityTypes {
    RIDER_SIGN_UP("0"), RIDER_REQUEST_DECLINE("1"), RIDER_ASSIGNED("2");

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
