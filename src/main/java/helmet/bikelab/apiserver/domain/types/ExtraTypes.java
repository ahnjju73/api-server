package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ExtraTypes {
    ALTERATION("503-001"), INCREMENT("503-002"), ETC("503-100") ;

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

    public String getReason(){
        switch (getExtraType(extra)){
            case ALTERATION:
                return "보험료 변경";
            case INCREMENT:
                return "보험료 인상";
            default:
                return "기타";
        }
    }
}
