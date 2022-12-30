package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum InsuranceTypes {
    PERSONAL("504-001", "개인"), COMPANY ("504-002", "법인"), NONE("504-003", "없음");

    private String type;
    private String typeName;

    InsuranceTypes(String type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public static InsuranceTypes getInsuranceType(String type){
        if(type == null){
            return null;
        }
        for(InsuranceTypes it : InsuranceTypes.values()){
            if(type.equals(it.getType())){
                return it;
            }
        }
        return null;
    }
}
