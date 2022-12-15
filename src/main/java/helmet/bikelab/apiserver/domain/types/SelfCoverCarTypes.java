package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum SelfCoverCarTypes {

    NO_SELF_COVER("0", "자차보험(무)"), YES_SELF_COVER("1", "자차보험(유)");

    private String coverType;
    private String coverTypeName;

    SelfCoverCarTypes(String coverType, String coverTypeName) {
        this.coverType = coverType;
        this.coverTypeName = coverTypeName;
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
