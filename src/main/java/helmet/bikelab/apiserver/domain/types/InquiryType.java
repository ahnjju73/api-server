package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum InquiryType {
    LEASE("123-001"), REPAIR("123-002"), PARTNERSHIP("123-003");

    private String type;

    InquiryType(String type) {
        this.type = type;
    }

    public static InquiryType getType(String type){
        if(type == null)
            return null;
        for (InquiryType is : InquiryType.values()){
            if(is.getType().equals(type)){
                return is;
            }
        }
        return null;
    }

}
