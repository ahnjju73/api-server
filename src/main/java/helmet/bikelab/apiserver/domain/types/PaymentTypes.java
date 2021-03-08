package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum PaymentTypes {
    MONTHLY("502-001"), DAILY("502-002");

    private String paymentType;

    PaymentTypes(String paymentType) {
        this.paymentType = paymentType;
    }

    public static PaymentTypes getPaymentType(String type){
        if(type ==null){
            return null;
        }
        for(PaymentTypes pt: PaymentTypes.values()){
            if(type.equals(pt.getPaymentType())){
                return pt;
            }
        }
        return null;
    }
}
