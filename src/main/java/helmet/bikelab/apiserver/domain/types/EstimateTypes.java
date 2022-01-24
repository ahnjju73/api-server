package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum EstimateTypes {
    FIRST("0"), LAST("1");

    private String type;

    EstimateTypes(String type) {
        this.type = type;
    }

    public static EstimateTypes getType(String type){
        if(type == null){
            return null;
        }
        for(EstimateTypes et : EstimateTypes.values()){
            if(et.getType().equals(type)){
                return et;
            }
        }
        return null;
    }
}
