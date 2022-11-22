package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum UsageTypes {
    COST_DELIVERY("0"), FREE_DELIVERY("1"), HOME_LEISURE("2");

    private String type;

    UsageTypes(String type) {
        this.type = type;
    }

    public static UsageTypes getType(String type){
        if(type == null){
            return null;
        }
        for(UsageTypes ut : UsageTypes.values()){
            if(ut.getType().equals(type))
                return ut;
        }
        return null;
    }
}
