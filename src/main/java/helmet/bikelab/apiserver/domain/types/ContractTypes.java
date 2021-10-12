package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ContractTypes {

    MANAGEMENT("500-001"), LEASE("500-002");

    private String status;


    ContractTypes(String status) {
        this.status = status;
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
