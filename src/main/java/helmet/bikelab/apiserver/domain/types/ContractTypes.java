package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ContractTypes {

    LEASE("500-001", "리스계약"), MANAGEMENT("500-002", "관리계약"), COMPANY("500-003", "법인계약");

    private String status;
    private String statusName;

    ContractTypes(String status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public static ContractTypes getContractType(String status){
        if(status == null){
            return null;
        }
        for(ContractTypes ct: ContractTypes.values()){
            if(status.equals(ct.getStatus())){
                return ct;
            }
        }
        return null;
    }

}
