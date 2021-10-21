package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum RiderLeaseRequestedTypes {

    NOT("0"), REQUEST("1");

    private String leaseRequested;

    RiderLeaseRequestedTypes(String leaseRequested) {
        this.leaseRequested = leaseRequested;
    }

    public static RiderLeaseRequestedTypes getRiderLeaseRequestedTypes(String riderStatus) {
        if (riderStatus == null) {
            return RiderLeaseRequestedTypes.NOT;
        }

        for (RiderLeaseRequestedTypes statusTypes : RiderLeaseRequestedTypes.values()) {
            if (riderStatus.equals(statusTypes.getLeaseRequested())) {
                return statusTypes;
            }
        }

        return null;
    }

}
