package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum BikeInsuranceTypes {
    PAID("0", "유상용"), UNPAID("1", "비유상용"), FOR_WORKS("2", "업무용");

    private String type;
    private String typeName;

    BikeInsuranceTypes(String type, String typeName) {
        this.type = type;
        this.typeName = typeName;
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
