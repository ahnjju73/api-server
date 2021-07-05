package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum InsuranceTypes {
    PERSONAL("504-001"), COMPANY ("504-002");

    private String type;

    InsuranceTypes(String type){
        this.type = type;
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
