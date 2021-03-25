package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ExtraTypes {
    ALTERATION("503-001"),  ETC("503-100");

    private String extra;

    ExtraTypes(String extra) {
        this.extra = extra;
    }

    public static ExtraTypes getExtraType(String type){
        if(type == null)
            return null;

        for(ExtraTypes et: ExtraTypes.values()){
            if(type.equals(et.getExtra())){
                return et;
            }
        }
        return null;
    }
}
