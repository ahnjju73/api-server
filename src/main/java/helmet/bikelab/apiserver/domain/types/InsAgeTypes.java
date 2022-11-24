package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum InsAgeTypes {
    NT("19"), TW("20"), TWO("21"), TWF("24"), TWS("26"), TTY("30"), TTYF("35"), FTH("43"), FE("48");

    private String age;

    InsAgeTypes(String i) {
        age = i;
    }

    public static InsAgeTypes getAge(String age){
        if(age == null)
            return null;
        for(InsAgeTypes iat : InsAgeTypes.values()){
            if(iat.getAge() == age){
                return iat;
            }
        }
        return null;
    }
}
