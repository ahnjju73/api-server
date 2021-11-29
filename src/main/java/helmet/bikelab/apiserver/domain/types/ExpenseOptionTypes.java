package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ExpenseOptionTypes {
    OFF("1"), ON("2");
    private String type;

    ExpenseOptionTypes(String type) {
        this.type = type;
    }

    public static ExpenseOptionTypes getType(String type){
        if(type == null){
            return null;
        }
        for(ExpenseOptionTypes eot : ExpenseOptionTypes.values()){
            if(eot.getType().equals(type)){
                return eot;
            }
        }
        return null;
    }
}
