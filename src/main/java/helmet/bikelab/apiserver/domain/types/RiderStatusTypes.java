package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum RiderStatusTypes {
    ACTIVATE("701-001"), PENDING("701-002"), DEACTIVATE("701-003");

    private String riderStatusType;

    RiderStatusTypes(String riderStatusType) {
        this.riderStatusType = riderStatusType;
    }

    public static RiderStatusTypes getRiderStatusTypes(String riderStatusType) {
        if (riderStatusType == null) {
            return null;
        }

        for (RiderStatusTypes as : RiderStatusTypes.values()) {
            if (riderStatusType.equals(as.getRiderStatusType())) {
                return as;
            }
        }

        return null;
    }

}
