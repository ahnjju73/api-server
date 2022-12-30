package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BikeInfoTypes {
    REGISTRATION("0", "취등록세"), STAMP_DUTY("1", "인지세"), LICENSE_PLATE("2", "번호판대"), ETC("3", "기타");

    private String infoType;
    private String infoTypeName;

    BikeInfoTypes(String infoType, String infoTypeName) {
        this.infoType = infoType;
        this.infoTypeName = infoTypeName;
    }

    public static BikeInfoTypes getBikeInfoTypes(String type){
        if(type == null){
            return null;
        }
        for(BikeInfoTypes bt : BikeInfoTypes.values()){
            if(type.equals(bt.getInfoType())){
                return bt;
            }
        }
        return null;
    }

}
