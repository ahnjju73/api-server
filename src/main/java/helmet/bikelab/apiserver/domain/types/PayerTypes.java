package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum PayerTypes {

    COMPANY("COMPANY"), RIDER("RIDER");

    private String status;

    PayerTypes(String status) {
        this.status = status;
    }

    public static PayerTypes getPayerTypes(String status){
        if(status == null){
            return null;
        }
        for(PayerTypes ct: PayerTypes.values()){
            if(status.equals(ct.getStatus())){
                return ct;
            }
        }
        return null;
    }
}
