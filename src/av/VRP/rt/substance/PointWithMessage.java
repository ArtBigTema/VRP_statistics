package av.VRP.rt.substance;

/**
 * Created by Artem on 08.02.2017.
 */
public class PointWithMessage extends Point {
    private int clust;

    public PointWithMessage(double la, double ln) {
        super(la, ln);
    }

    public PointWithMessage(Point point) {
        super(point.getLat(), point.getLng());
        clust = 1;
    }

    public void incClust() {
        clust++;
    }

    public String getMsg() {
        return String.valueOf(clust);
    }

    public int getClust() {
        return clust;
    }

    public void setClust(int clust) {
        this.clust = clust;
    }
}