package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BikeStatusTypes {

    PENDING("0"), RIDING("1"), FOR_SALE("2"), JUNK("3");

    private String type;

    BikeStatusTypes(String type){
        this.type = type;
    }

    public static BikeStatusTypes getBikeStatusTypes(String type){
        if(type == null){
            return null;
        }
        for(BikeStatusTypes bt : BikeStatusTypes.values()){
            if(type.equals(bt.getType())){
                return bt;
            }
        }
        return null;
    }

}
