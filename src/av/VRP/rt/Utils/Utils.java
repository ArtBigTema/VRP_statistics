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

    public static String[] getTitleForTable(boolean isShort) {
        return isShort ? Constant.TABLE_TITLES_FOR_UBER : Constant.TABLE_TITLES_FOR_GY;
    }

    public static String getTitle(String url) {
        if (url.contains(Constant.GREEN)) {
            return Constant.GREEN;
        }
        if (url.contains(Constant.YELLOW)) {
            return Constant.YELLOW;
        }
        return Constant.UBER;
    }
}
