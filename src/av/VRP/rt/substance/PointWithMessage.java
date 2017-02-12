package av.VRP.rt.substance;

import com.teamdev.jxmaps.LatLng;

/**
 * Created by Artem on 08.02.2017.
 */
public class PointWithMessage extends Point implements Comparable<PointWithMessage> {
    private Integer clust;
    private int count;
    private double la;
    private double ln;

    private String message;

    public PointWithMessage(Point point, String msg) {
        super(point.getLat(), point.getLng());

        la = point.getLat();
        ln = point.getLng();

        clust = 0; //fixme why 1 Edivbyzero
        message = msg;
    }

    public void incClust() {
        clust++;
    }

    public String getMsg() {
        return message;
    }

    public int getClust() {
        return clust;
    }

    public double getClustD() {
        return clust;
    }

    public void incLatLng(Point point) {
        la += point.getLat();
        ln += point.getLng();
    }

    public boolean freeDepo() {
        return count < clust;
    }

    public void incCount() {
        count++;
    }

    public void decCount() {
        count--;
    }

    public boolean needShuffle() {
        return count > clust;
    }

    public LatLng getLatLng() {
        return new LatLng(la / (clust+1), ln / (clust+1));
    }
    //fixme Edivbyzero

    @Override
    public String toString() {
        return "PointWithMessage{" +// getLatLng() +
                ", max=" + clust + ", curr=" + count + ":" + message +
                '}';
    }

    @Override
    public int compareTo(PointWithMessage o) {
        return o.clust.compareTo(clust);
    }
}