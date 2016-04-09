package av.VRP.rt.substance;

/**
 * Created by Artem on 09.04.2016.
 */
public class PointWithTime extends Point {
    String dateTime = "";

    public PointWithTime(double la, double ln) {
        super(la, ln);
    }

    public PointWithTime(double la, double ln, String dt) {
        super(la, ln);
        dateTime = dt;
    }
}