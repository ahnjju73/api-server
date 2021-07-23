package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BikeRiderStatusTypes {
    NONE("0"), PENDING("1"), TAKEN("2");

    private String riderStatus;

    BikeRiderStatusTypes(String riderStatus) {
        this.riderStatus = riderStatus;
    }

    public static BikeRiderStatusTypes getBikeRiderStatusTypes(String riderStatus) {
        if (riderStatus == null) {
            return null;
        }

        for (BikeRiderStatusTypes statusTypes : BikeRiderStatusTypes.values()) {
            if (riderStatus.equals(statusTypes.getRiderStatus())) {
                return statusTypes;
            }
        }

        return null;
    }

}
