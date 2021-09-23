package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum DemandLeaseContractTypes {

    PENDING("0", "계약서생성전"),
    CONTRACTED("1", "계약서생성완료"),
    CONTRACTING("2", "계약서생성중"),
    FAILED("3", "계약서생정 실패");

    private String contractType;
    private String contractTypeName;

    DemandLeaseContractTypes(String contractType, String contractTypeName) {
        this.contractType = contractType;
        this.contractTypeName = contractTypeName;
    }

    public static DemandLeaseContractTypes getDemandLeaseContractTypes(String contractType) {
        if (contractType == null) {
            return null;
        }

        for (DemandLeaseContractTypes as : DemandLeaseContractTypes.values()) {
            if (contractType.equals(as.getContractType())) {
                return as;
            }
        }

        return null;
    }

}
