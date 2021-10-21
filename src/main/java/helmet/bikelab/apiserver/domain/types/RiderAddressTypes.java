package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum RiderAddressTypes {
    ON_PAPER("700-001"), REAL_RESIDENCE("700-002");

    private String type;

    RiderAddressTypes(String type) {
        this.type = type;
    }

    public static RiderAddressTypes getType(String type) {
        if (type == null) {
            return null;
        }
        for (RiderAddressTypes rat : RiderAddressTypes.values()) {
            if (type.equals(rat.getType())) {
                return rat;
            }
        }
        return null;
    }

}
