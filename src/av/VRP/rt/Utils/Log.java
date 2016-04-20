package av.VRP.rt.Utils;

/**
 * Created by Artem on 20.04.2016.
 */
public class Log {//FIXME log to file

    //add Tag
    public static void p(Object s) {
        System.out.println(s.toString());
    }

    public static void e(Object s) {
        System.err.println(s.toString());
    }

    public static void d(Object s) {
        System.err.println(s.toString());
    }

    public static void d(Object s, Object ss) {
        System.err.println(s.toString() + " " + ss.toString());
    }
}