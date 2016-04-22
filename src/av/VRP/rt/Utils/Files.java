package av.VRP.rt.Utils;

import java.io.*;

/**
 * Created by Artem on 21.04.2016.
 */
public class Files {

    public static PrintWriter getWriter(String s, int n) throws FileNotFoundException, UnsupportedEncodingException {
        return new PrintWriter(Constant.DIR + "/" + Utils.getTitle(s) + n + Constant.FILE_FORMAT, Constant.FILE_ENCODING);
    }

    public static FileReader getReader(int i) throws IOException {
        return new FileReader(getFileInDir(Constant.DIR, i));
    }

    public static String getFileName(int i) throws IOException {
        return getFileNameInDir(Constant.DIR, i);
    }

    public static void createDirectory() {
        new File(Constant.DIR).mkdir();
    }

    public static boolean deleteDirectory() {
        return deleteDirectory(new File(Constant.DIR));
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
        return path.delete();
    }

    public static String getFileInDir(String dir, int i) throws IOException {
        return new File(Constant.DIR).listFiles()[i].getCanonicalPath();
    }

    public static String getFileNameInDir(String dir, int i) throws IOException {
        return new File(Constant.DIR).listFiles()[i].getName();
    }

    public static int getCountFilesInDir(String dir) {
        return new File(Constant.DIR).listFiles().length;
    }
}