package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BikeInsuranceTypes {
    COMPREHENSIVE("0"), PERSONAL("1");

    private String type;

    BikeInsuranceTypes(String type){
        this.type = type;
    }

    public static BikeInsuranceTypes getBikeInsuranceTypes(String type){
        if(type == null){
            return null;
        }
        for(BikeInsuranceTypes it : BikeInsuranceTypes.values()){
            if(type.equals(it.getType())){
                return it;
            }
        }
        return null;
    }
}
