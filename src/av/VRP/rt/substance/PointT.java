package av.VRP.rt.substance;

/**
 * Created by Artem on 09.04.2016.
 */
public class PointT {
    public Point startPoint;//FIXME remove public
    public Point endPoint;

    public int delay;
    public int dis;

    public PointT(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.startPoint.isStartPoint = true;//FIXME
        this.endPoint = endPoint;
        this.startPoint.isStartPoint = false;//FIXME
    }

    public PointT(Point startPoint) {
        this.startPoint = startPoint;
    }

    public static PointT constructPointT(String s) {
        //FIXME

        return null;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setEndPoint(int x, int y) {
        endPoint = new Point(x, y);
    }
}