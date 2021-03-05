package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ContractTypes {
    OPERATING("500-001"), EXTEND("500-002"), FINISH("500-003"), CANCEL("500-004");

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
