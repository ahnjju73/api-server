package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;

@Getter
public enum TimeTypes {
    FIRST("0"), SECOND("1");

    private String time;

    TimeTypes(String time) {
        this.time = time;
    }

    public static TimeTypes getType(String type){
        if(type == null)
            return null;
        for(TimeTypes tt : TimeTypes.values()){
            if (tt.getTime().equals(type)){
                return tt;
            }
        }
        return null;
    }
}
