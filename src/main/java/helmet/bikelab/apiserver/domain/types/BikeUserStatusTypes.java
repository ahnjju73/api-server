package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BikeUserStatusTypes {

    COMPLETED("101-001"), PENDING("101-002"), IN_PROGRESS("101-003"), DELETED("101-004");

    private String status;

    BikeUserStatusTypes(String status) {
        this.status = status;
    }

    public static BikeUserStatusTypes getBikeUserStatus(String status) {
        if (status == null) {
            return null;
        }

        for (BikeUserStatusTypes yn : BikeUserStatusTypes.values()) {
            if (status.equals(yn.getStatus())) {
                return yn;
            }
        }

        return null;
    }


}
