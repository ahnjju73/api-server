package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum FineStatusTypes {
    UNPAID("504-001"), PAID("504-002"), PROGRESS("504-003");

    private String status;

    FineStatusTypes(String status){
        this.status = status;
    }

    public static FineStatusTypes getFineStatus(String status){
        if(status == null){
            return null;
        }

        for(FineStatusTypes fineStatusTypes : FineStatusTypes.values()){
            if(fineStatusTypes.getStatus().equals(status)){
                return fineStatusTypes;
            }
        }
        return null;
    }

}
