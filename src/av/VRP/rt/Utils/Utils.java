package av.VRP.rt.Utils;

import java.util.List;

/**
 * Created by Artem on 10.04.2016.
 */
public class Utils {

    public static String[] strToArray(String from, String divider) {
        return from.split(divider);
    }

    public static String[][] listToTable(List list, String divider) {
        String[][] result = new String[list.size()][];

        int i = 0;
        for (Object o : list) {
            result[i++] = strToArray(o.toString(), divider);
        }
        return result;
    }
}