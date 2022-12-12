package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum SelfCoverCarTypes {

    NO_SELF_COVER("0"), YES_SELF_COVER("1");

    private String coverType;

    SelfCoverCarTypes(String coverType) {
        this.coverType = coverType;
    }

    public static SelfCoverCarTypes getSelfCoverCarTypes(String status) {
        if (status == null) {
            return null;
        }

        for (SelfCoverCarTypes yn : SelfCoverCarTypes.values()) {
            if (status.equals(yn.getCoverType())) {
                return yn;
            }
        }

        return null;
    }


}
