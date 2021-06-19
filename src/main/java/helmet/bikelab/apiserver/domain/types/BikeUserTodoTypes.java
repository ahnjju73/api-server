package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BikeUserTodoTypes {

    LEASE_APPROVAL("111-001"), LEASE_REJECT("111-002"), LEASE_CONFIRM("111-003");

    private String status;

    BikeUserTodoTypes(String status) {
        this.status = status;
    }

    public static BikeUserTodoTypes getBikeUserTodoTypes(String status) {
        if (status == null) {
            return null;
        }

        for (BikeUserTodoTypes ls : BikeUserTodoTypes.values()) {
            if (status.equals(ls.getStatus())) {
                return ls;
            }
        }

        return null;
    }
}
