package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ExpenseTypes {
    BIKE("0", "오토바이 비용"), BOX("1", "배달 박스 비용"), HOLDER("2", "거치대 비용"), CARRIER("3", "캐리어 비용"), WAGE("4", "장착 공임비"), INSURANCE("5", "보험료"), REGISTER("6", "취등록세"), DELIVERY("7", "탁송비");

    private String type;
    private String typeName;

    ExpenseTypes(String type, String typeName) {
        this.type = type;
        this.typeName = typeName;
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
