package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum InsuranceCompanyStatusTypes {

    COMPLETED("101-001"), PENDING("101-002"), IN_PROGRESS("101-003"), DELETED("101-004");

    private String status;

    InsuranceCompanyStatusTypes(String status) {
        this.status = status;
    }

    public static InsuranceCompanyStatusTypes getInsuranceStatus(String status) {
        if (status == null) {
            return null;
        }

        for (InsuranceCompanyStatusTypes yn : InsuranceCompanyStatusTypes.values()) {
            if (status.equals(yn.getStatus())) {
                return yn;
            }
        }

        return null;
    }
}
