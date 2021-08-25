package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum UnitTypes {

    EA("EA");

    private String status;

    UnitTypes(String status) {
        this.status = status;
    }

    public static UnitTypes getUnitTypes(String status){
        if(status == null){
            return null;
        }
        for(UnitTypes ut : UnitTypes.values()){
            if(status.equals(ut.getStatus())){
                return ut;
            }
        }
        return null;
    }
}
