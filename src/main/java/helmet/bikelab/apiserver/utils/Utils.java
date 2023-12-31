package helmet.bikelab.apiserver.utils;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Date;

public class Utils {
    public static <T> String getCurrencyFormat(T number){
        if(number == null) return "0";
        DecimalFormat format = new DecimalFormat("###,###");
        format.format(number);
        return format.format(number);
    }

    public static String randomPassword(int size) {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '@', '#', '$', '%', '&' };
        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());
        int idx = 0;
        int len = charSet.length;
        for (int i = 0; i<size; i++) {
            idx = sr.nextInt(len);
            sb.append(charSet[idx]);
        }
        return sb.toString();

    }
}
