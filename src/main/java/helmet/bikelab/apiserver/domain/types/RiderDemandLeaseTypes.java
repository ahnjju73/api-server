package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum RiderDemandLeaseTypes {

    LIABILITY("000-001", "책임보험"), COMPREHENSIVE("000-002", "종합보험");

    private String leaseType;
    private String leaseTypeName;

    RiderDemandLeaseTypes(String leaseType, String leaseTypeName) {
        this.leaseType = leaseType;
        this.leaseTypeName = leaseTypeName;
    }

    public static RiderDemandLeaseTypes getRiderDemandLeaseTypes(String status){
        if(status == null){
            return null;
        }
        for(RiderDemandLeaseTypes mt : RiderDemandLeaseTypes.values()){
            if(status.equals(mt.getLeaseType())){
                return mt;
            }
        }
        return null;
    }
}
