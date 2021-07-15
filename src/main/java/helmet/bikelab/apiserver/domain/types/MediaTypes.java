package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum MediaTypes {
    IMAGE("IMAGE"), VIDEO("VIDEO");

    private String status;

    MediaTypes(String status) {
        this.status = status;
    }

    public static MediaTypes getMediaTypes(String status){
        if(status == null){
            return null;
        }
        for(MediaTypes mt : MediaTypes.values()){
            if(status.equals(mt.getStatus())){
                return mt;
            }
        }
        return null;
    }
}
