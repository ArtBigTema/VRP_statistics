package av.VRP.rt.substance;

import com.teamdev.jxmaps.LatLng;

/**
 * Created by Artem on 08.02.2017.
 */
public class PointWithMessage extends Point implements Comparable<PointWithMessage> {
    private Integer clust;
    private int count;
    private double part;
    private int coming;

    private double la;
    private double ln;

    private String message;

    public PointWithMessage(Point point, String msg) {
        super(point.getLat(), point.getLng());

        la = point.getLat();
        ln = point.getLng();

        clust = 1; //fixme why 1 Edivbyzero
        count = 1; //fixme why 1 Edivbyzero
        part = 1d;

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
        coming++;
    }

    public void decCount() {
        coming--;
        count--;
    }

    public void incComing() {
        coming++;
    }

    public int getComing() {
        return coming;
    }

    public boolean getComingMore() {
        return coming <= getPart();
    }


    public void decComing() {
        coming--;
    }

    public int getCount() {
        return count;
    }

    public boolean needShuffle() {
        return coming >= getPart();//count > clust;
    }

    public Double getPart() {
        return part * clust;
    }

    public void setPart(double part) {
        this.part = Math.max(part, 1);
    }

    public LatLng getLatLng() {
        return new LatLng(
                la / (clust),
                ln / (clust));
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