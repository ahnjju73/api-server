package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum RoleTypes {
    ADMIN("0", "관리자"), STAFF("1", "스텝"), REWARD("2", "대물보상");

    private String type;
    private String role;

    RoleTypes(String type, String role) {
        this.type = type;
        this.role = role;
    }

    public static RoleTypes getType(String type){
        if(type == null)
            return null;
        for(RoleTypes rt : RoleTypes.values()){
            if(rt.getType().equals(type))
                return rt;
        }
        return null;
    }
    public static RoleTypes getRole(String type){
        if(type == null)
            return null;
        for(RoleTypes rt : RoleTypes.values()){
            if(rt.getRole().equals(type))
                return rt;
        }
        return null;
    }
}
