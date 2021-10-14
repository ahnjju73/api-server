package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum PaidTypes {
    BANK("BANK"), RIDER("RIDER"), CLIENT("CLIENT"), BM("BM");

    private String status;

    PaidTypes(String status) {
        this.status = status;
    }

    public static PaidTypes getStatus(String status){
        if(status == null){
            return null;
        }
        for(PaidTypes pt : PaidTypes.values()){
            if(pt.getStatus().equals(status)){
                return pt;
            }
        }
        return null;
    }

}
