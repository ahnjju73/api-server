package helmet.bikelab.apiserver.utils;

import java.text.DecimalFormat;

public class Utils {
    public static <T> String getCurrencyFormat(T number){
        if(number == null) return "0";
        DecimalFormat format = new DecimalFormat("###,###");
        format.format(number);
        return format.format(number);
    }
}
