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
        this.endPoint = endPoint;
    }

    public PointT(Point startPoint) {
        this.startPoint = startPoint;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setEndPoint(int x, int y) {
        endPoint = new Point(x, y);
    }
}