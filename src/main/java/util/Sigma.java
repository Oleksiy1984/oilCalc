package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sigma {

     public static List<Double> compute (Map<Double, List<Double>> map) {
         List<Double> list = new ArrayList<>();
        ArrayList<Double> keys = new ArrayList<Double>(map.keySet());
        double result;
        for (int keyIndex = 0; keyIndex < keys.size(); keyIndex++) {

            for (int valueIndex = 0; valueIndex < map.get(keys.get(keyIndex)).size(); valueIndex++) {

                result = keys.get(0) * Math.log10(map.get(keys.get(keyIndex)).get(valueIndex));

                if (keyIndex > 0) {
                    for (int i = 0; i < keyIndex; i++) {
                        result += (keys.get(keyIndex - i) - keys.get(keyIndex - i - 1)) * Math.log10(map.get(keys.get(keyIndex)).get(valueIndex)
                                - map.get(keys.get(keyIndex - i - 1)).get(map.get(keys.get(keyIndex - i - 1)).size() - 1));

                    }
                }
                System.out.println(keys.get(keyIndex) + "-" + 1 / keys.get(keyIndex) * result);
                list.add(1 / keys.get(keyIndex) * result);
            }
        }
         return list;
     }
}
