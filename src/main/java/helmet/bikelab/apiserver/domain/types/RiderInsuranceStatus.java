package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum RiderInsuranceStatus {
    PENDING("0"), CHECKING("1");

    private String status;

    RiderInsuranceStatus(String status) {
        this.status = status;
    }

    public static RiderInsuranceStatus getStatus(String status){
        if(status == null){
            return null;
        }
        for(RiderInsuranceStatus ris : RiderInsuranceStatus.values()){
            if(status.equals(ris.getStatus())){
                return ris;
            }
        }
        return null;
    }
}
