package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum PayerTypes {

    COMPANY("COMPANY", "고객사"), RIDER("RIDER", "라이더");

    private String status;
    private String statusName;

    PayerTypes(String status, String statusName) {
        this.status = status;
        this.statusName = statusName;
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
