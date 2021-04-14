package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum InsuranceTypes {
    DUTY("500-001"), ADDITIONAL("500-002");

    private String type;

    InsuranceTypes(String type) {
        this.type = type;
    }

    public static InsuranceTypes getInsuranceType(String type){
        if(type == null){
            return null;
        }
        for(InsuranceTypes insuranceType : InsuranceTypes.values()){
            if(insuranceType.getType().equals(type)){
                return insuranceType;
            }
        }
        return null;
    }
}
