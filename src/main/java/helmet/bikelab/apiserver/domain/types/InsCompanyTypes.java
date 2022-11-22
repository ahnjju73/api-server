package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum InsCompanyTypes {
    KB("0", "KB 손보"), DB("1", "DB 손보"), HYUNDAI("2", "현대 해상"), SAMSUNG("3", "삼성 화재"), MARITZ("4", "메리츠 화재"), LOTTE("5", "롯데 손보");

    private String type;
    private String name;

    InsCompanyTypes(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static InsCompanyTypes getCompanyType(String type){
        if(type == null)
            return null;
        for(InsCompanyTypes ict : InsCompanyTypes.values()){
            if(ict.getType().equals(type))
                return ict;
        }
        return null;
    }

}
