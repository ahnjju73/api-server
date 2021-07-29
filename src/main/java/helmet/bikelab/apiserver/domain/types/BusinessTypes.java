package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BusinessTypes {
    PERSONAL("0", "개인사업자"), CORPORATE("1", "법인사업자");

    private String businessType;
    private String business;

    BusinessTypes(String businessType, String business) {
        this.businessType = businessType;
        this.business = business;
    }

    public static BusinessTypes getBusinessTypes(String businessType) {
        if (businessType == null) {
            return null;
        }

        for (BusinessTypes as : BusinessTypes.values()) {
            if (businessType.equals(as.getBusinessType())) {
                return as;
            }
        }

        return null;
    }

}
