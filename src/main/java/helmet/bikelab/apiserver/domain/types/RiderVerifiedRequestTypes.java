package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum RiderVerifiedRequestTypes {

    REQUEST("REQUEST"), VERIFIED("VERIFIED");

    private String verifiedRequestType;

    RiderVerifiedRequestTypes(String verifiedRequestType) {
        this.verifiedRequestType = verifiedRequestType;
    }

    public static RiderVerifiedRequestTypes getRiderVerifiedRequestTypes(String type){
        if(type == null){
            return null;
        }
        for(RiderVerifiedRequestTypes at: RiderVerifiedRequestTypes.values()){
            if(type.equals(at.getVerifiedRequestType())){
                return at;
            }
        }
        return null;
    }

}
