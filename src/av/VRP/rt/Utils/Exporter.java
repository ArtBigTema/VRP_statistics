package av.VRP.rt.Utils;

import java.io.*;

/**
 * Created by Artem on 26.01.2017.
 */
public class Exporter {
    private static PrintWriter writer;

    public static void init(String title){
        createDirectory();
        try {
            writer = getWriter(title);
        } catch (IOException e) {
            Log.e(e.getMessage());
            e.printStackTrace();//fixme
        }
    }

    public static void export(String[] strings) {
        for (String string : strings) {
            writer.append(string);
            writer.append('\n');
        }
        writer.close();
    }

    public static void createDirectory() {
        new File(Constant.DIR_FOR_SCALA).mkdir();
    }

    public static PrintWriter getWriter(String title) throws FileNotFoundException, UnsupportedEncodingException {
        return new PrintWriter(Constant.DIR_FOR_SCALA + "/"
                + Utils.getTitle(title)  + Constant.FILE_FORMAT_FOR_SCALA,
                Constant.FILE_ENCODING);
    }
}