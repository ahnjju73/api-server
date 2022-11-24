package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum InsuranceBikeTypes {
    소형A("0"), 소형B("1"), 중형A("2"), 중형B("3"), 대형("4");

    private String type;

    InsuranceBikeTypes(String type) {
        this.type = type;
    }

    public static InsuranceBikeTypes getType(String type){
        if(type == null){
            return null;
        }
        for(InsuranceBikeTypes ibt : InsuranceBikeTypes.values()){
            if(ibt.getType().equals(type)){
                return ibt;
            }
        }
        return null;
    }

}
