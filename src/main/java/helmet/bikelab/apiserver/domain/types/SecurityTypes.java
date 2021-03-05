package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

import javax.naming.directory.SearchResult;

@Getter
public enum SecurityTypes {
    PERSON("300-001"), CAR("300-002"), MYSELF("300-003"), MY_CAR("300-004");

    private String security;

    SecurityTypes(String security) {
        this.security = security;
    }

    public static SecurityTypes getSecurityType(String type){
        if(type == null){
            return null;
        }
        for(SecurityTypes st : SecurityTypes.values()){
            if(type.equals(st.getSecurity())){
                return st;
            }
        }
        return null;
    }
}
