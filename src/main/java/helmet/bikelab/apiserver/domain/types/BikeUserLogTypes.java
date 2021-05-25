package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BikeUserLogTypes {

    COMM_GROUP_ADDED("150-001"),
    COMM_GROUP_UPDATED("150-002"),
    COMM_CLIENT_ADDED("150-003"),
    COMM_CLIENT_UPDATED("150-004"),
    COMM_BIKE_ADDED("150-005"),
    COMM_BIKE_UPDATED("150-006"),
    COMM_INSURANCE_ADDED("150-007"),
    COMM_INSURANCE_UPDATED("150-008"),
    COMM_FINE_ADDED("150-009"),
    COMM_FINE_UPDATED("150-010"),
    LEASE_ADDED("150-011"),
    LEASE_UPDATED("150-002"),
    LEASE_APPROVE_REQUESTED("150-013"),
    LEASE_APPROVE_COMPLETED("150-015"),
    LEASE_APPROVE_REJECTED("150-014"),
    LEASE_PAYMENT("150-016"),
    ;

    private String status;

    BikeUserLogTypes(String status) {
        this.status = status;
    }

    public static BikeUserLogTypes getBikeUserStatus(String status) {
        if (status == null) {
            return null;
        }

        for (BikeUserLogTypes yn : BikeUserLogTypes.values()) {
            if (status.equals(yn.getStatus())) {
                return yn;
            }
        }

        return null;
    }


}
