package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum InquiryStatusTypes {
    PENDING("0"), CONFIRMED("1");

    private String status;

    InquiryStatusTypes(String status) {
        this.status = status;
    }

    public static InquiryStatusTypes getInquiryStatusTypes(String status){
        if(status == null){
            return null;
        }
        for(InquiryStatusTypes mt : InquiryStatusTypes.values()){
            if(status.equals(mt.getStatus())){
                return mt;
            }
        }
        return null;
    }
}
