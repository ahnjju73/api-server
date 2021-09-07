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

    public static String getExpense(ExpenseTypes expenseTypes){
        switch (expenseTypes){
            case BIKE: return "오토바이 비용";
            case BOX: return "배달 박스 비용";
            case HOLDER: return "거치대 비용";
            case CARRIER: return "캐리어 비용";
            case WAGE: return "장착 공임비";
            case INSURANCE: return "보험료";
            default: return "취등록세";
        }
    }
}
