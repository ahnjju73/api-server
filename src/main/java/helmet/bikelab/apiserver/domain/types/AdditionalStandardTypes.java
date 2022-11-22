package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum AdditionalStandardTypes {
    FIFTY("50"), HUNDRED("100"), ONEHALF("150"), TWO_HUNDRED("200");

    private String price;

    AdditionalStandardTypes(String i) {
        price = i;
    }

    public static AdditionalStandardTypes getType(String price){
        for(AdditionalStandardTypes ast : AdditionalStandardTypes.values()){
            if(ast.getPrice().equals(price)){
                return ast;
            }
        }
        return null;
    }
}
