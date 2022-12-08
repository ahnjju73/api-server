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
    LEASE_UPDATED("150-012"),
    LEASE_APPROVE_REQUESTED("150-013"),
    LEASE_APPROVE_COMPLETED("150-015"),
    LEASE_APPROVE_REJECTED("150-014"),
    LEASE_PAYMENT("150-016"),
    LEASE_EXTRA_PAYMENT("150-017"),
    LEASE_PAYMENT_READ("150-018"),
    LEASE_OVERPAY("150-019"),
    COMM_CLIENT_OVERPAY("150-020"),
    LEASE_EXTRA_READ("150-021"),
    COMM_SHOP_ADDED("150-030"),
    COMM_SHOP_UPDATED("150-031"),
    LEASE_EXTENSION("150-032"),
    BIKE_INSURANCE_ADD("201-001"),
    BIKE_INSURANCE_REMOVE("201-002"),
    BIKE_INSURANCE_UPDATE("201-003"),
    BIKE_INSURANCE_SET("201-004"),
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
