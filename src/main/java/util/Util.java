package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Alexey on 17.01.2018.
 */
public class Util {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
