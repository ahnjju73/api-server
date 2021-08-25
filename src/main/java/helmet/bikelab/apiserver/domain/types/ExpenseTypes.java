package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ExpenseTypes {
    BIKE("0"), BOX("1"), HOLDER("2"), CARRIER("3"), WAGE("4"), INSURANCE("5"), REGISTER("6");

    private String type;

    ExpenseTypes(String type) {
        this.type = type;
    }

    public static ExpenseTypes getType(String type){
        if(type == null){
            return null;
        }
        for(ExpenseTypes et: ExpenseTypes.values()){
            if(et.getType().equals(type)){
                return et;
            }
        }
        return null;
    }
}
