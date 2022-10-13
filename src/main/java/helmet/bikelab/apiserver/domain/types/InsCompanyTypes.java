package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum InsCompanyTypes {
    KB("0", "KB보험");

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
