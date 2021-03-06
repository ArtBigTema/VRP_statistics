package av.VRP.rt.Utils;

/**
 * Created by Artem on 20.04.2016.
 */
public class Log {//FIXME log to file

    //add Tag
    public static void p(Object s) {
        System.out.println(s.toString());
    }

    public static void pp(Object s) {
        System.out.println(s.toString());
    }

    public static void p(Object... s) {
        for (Object o : s) {
            System.out.print(o.toString() + ':');
        }
        p("");
    }

    public static void p(Object s, Object ss) {
        p(s.toString() + ss.toString());
    }

    public static void p(Object s, String divider, String ss) {
        p(s + divider + ss);
    }

    public static void e(Object s) {
        System.err.println(s.toString());
    }

    public static void e(Exception e) {
        e(e.toString());
        e.printStackTrace();
    }

    public static void e(Object s, Object ss) {
        e(s.toString() + ss.toString());
    }

    public static void d(Object s) {
        e(s.toString());
    }

    public static void d(Object s, Object ss) {
        d(s.toString() + " " + ss.toString());
    }
}