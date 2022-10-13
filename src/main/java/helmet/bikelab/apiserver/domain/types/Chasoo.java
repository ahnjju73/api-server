package helmet.bikelab.apiserver.domain.types;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Chasoo {
    ALWAYS("0", "상시"), FIRST("1", "1차"), SECOND("2", "2차");

    private String status;
    private String statusNm;

    Chasoo(String status, String statusNm) {
        this.status = status;
        this.statusNm = statusNm;
    }

    public static Chasoo getStatus(String status){
        if(status == null)
            return null;
        for (Chasoo cha : Chasoo.values()) {
            if(cha.getStatus().equals(status)){
                return cha;
            }
        }
        return null;
    }

    public static Chasoo getStatusNm(String status){
        if(status == null)
            return null;
        for (Chasoo cha : Chasoo.values()) {
            if(cha.getStatusNm().equals(status)){
                return cha;
            }
        }
        return null;
    }
}
