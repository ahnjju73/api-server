package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum InsRangeTypes {
    ONLY_ONE("0", "1인 적용"), FAMILY("1", "가족 한정"), ANYBODY("2", "누구나 적용");

    private String type;
    private String range;

    InsRangeTypes(String type, String range) {
        this.type = type;
        this.range = range;
    }

    public static InsRangeTypes getType(String type){
        if(type == null){
            return null;
        }
        for(InsRangeTypes irt : InsRangeTypes.values()){
            if(type.equals(irt.getType())){
                return irt;
            }
        }
        return null;
    }

    public static InsRangeTypes getRange(String range){
        if(range == null){
            return null;
        }
        for(InsRangeTypes irt : InsRangeTypes.values()){
            if(range.equals(irt.getRange())){
                return irt;
            }
        }
        return null;
    }
}
