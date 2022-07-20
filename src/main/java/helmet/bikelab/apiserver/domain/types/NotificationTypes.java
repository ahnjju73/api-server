package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum NotificationTypes {
    CLIENTS("509-001"), GROUPS("509-002"), REPAIRSHOPS("509-003"), INSURANCE("509-004"), ALL("509-005");

    private String type;

    NotificationTypes(String type) {
        this.type = type;
    }

    public static NotificationTypes getType (String type){
        if(type == null)
            return null;
        for(NotificationTypes nt : NotificationTypes.values()){
            if(nt.getType().equals(type)){
                return nt;
            }
        }
        return null;
    }
}
