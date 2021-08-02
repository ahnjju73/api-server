package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ExpireTypes {
    TAKE_OVER("505-001", "인수"), RETURN("505-002", "반납");

    private String status;
    private String statusName;

    ExpireTypes(String status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public static ExpireTypes getExpireTypes(String status){
        if(status == null){
            return null;
        }
        for(ExpireTypes mt : ExpireTypes.values()){
            if(status.equals(mt.getStatus())){
                return mt;
            }
        }
        return null;
    }
}
