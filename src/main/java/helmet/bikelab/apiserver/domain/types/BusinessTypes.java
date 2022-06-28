package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BusinessTypes {

    PERSONAL_CORPORATION("0", "개인사업자(일반)", 1.1),
    SIMPLE_PERSONAL_CORPORATION("01", "개인사업자(간이)", 1.0),
    CORPORATE("1", "법인사업자", 1.1),
    PERSONAL("2", "개인", 3.3);

    private String businessType;
    private String business;
    private Double taxRate;

    BusinessTypes(String businessType, String business, Double taxRate) {
        this.businessType = businessType;
        this.business = business;
        this.taxRate = taxRate;
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
