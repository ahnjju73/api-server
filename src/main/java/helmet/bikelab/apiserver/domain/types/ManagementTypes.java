package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum ManagementTypes {
    FINANCIAL("501-001"), PRACTICAL("501-002"), INSURANCE("501-003");

    private String status;

    ManagementTypes(String status) {
        this.status = status;
    }

    public static ManagementTypes getManagementStatus(String status){
        if(status == null){
            return null;
        }
        for(ManagementTypes mt : ManagementTypes.values()){
            if(status.equals(mt.getStatus())){
                return mt;
            }
        }
        return null;
    }
}
