package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum RiderVerifiedTypes {

    NOT("NOT"), VERIFIED("VERIFIED"), REJECTED("REJECTED");

    private String verifiedType;

    RiderVerifiedTypes(String verifiedType) {
        this.verifiedType = verifiedType;
    }

    public static RiderVerifiedTypes getRiderVerifiedTypes(String businessType) {
        if (businessType == null) {
            return null;
        }

        for (RiderVerifiedTypes as : RiderVerifiedTypes.values()) {
            if (businessType.equals(as.getVerifiedType())) {
                return as;
            }
        }

        return null;
    }

}
