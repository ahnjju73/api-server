package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BikeStatusTypes {

    PENDING("0", "보관중"), RIDING("1", "운영중"), FOR_SALE("2", "판매완료"), JUNK("3", "폐차");

    private String type;
    private String typeName;

    BikeStatusTypes(String type, String typeName) {
        this.type = type;
        this.typeName = typeName;
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
