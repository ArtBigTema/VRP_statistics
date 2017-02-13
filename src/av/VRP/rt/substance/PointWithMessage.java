package av.VRP.rt.substance;

import av.VRP.rt.Utils.Log;
import com.teamdev.jxmaps.LatLng;

/**
 * Created by Artem on 08.02.2017.
 */
public class PointWithMessage extends Point implements Comparable<PointWithMessage> {
    private Integer clust;
    private int countPoint;

    private Double part;

    private int comingVehicle;
    private int depoVehicle;

    private double la;
    private double ln;

    private String message;

    public PointWithMessage(Point point, String msg) {
        super(point.getLat(), point.getLng());

        la = point.getLat();
        ln = point.getLng();

        clust = 1; //fixme why 1 Edivbyzero
        part = 1d;
        countPoint = 0; //fixme why 1
        comingVehicle = 0;

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

    public void incCountVehicle() {
        depoVehicle++;
        //   comingVehicle++;// пришли сами
    }

    public void decCountVehicle() {
        depoVehicle--;
        if (depoVehicle < 0) {
            Log.e("errrrrrrrrrrrrrrrrrrrrrrr");
        }
    }

    public int getDepoVehicle() {
        return depoVehicle;
    }

    public void incComingVehicle() {
        comingVehicle++;
    }

    public int getComingVehicle() {
        return comingVehicle;
    }

    public boolean getComingMore() {
        return comingVehicle <= getPart().intValue();
    }


    public void decComingVehicle() {
        comingVehicle--;
        if (comingVehicle < 0) {
            Log.e("errrrrrrrrrrrrrrrrrrrrrrr");
        }
    }

    public int getCountPoint() {
        return countPoint;
    }

    public boolean needShuffle() {
        return comingVehicle >= getPart().intValue();//countPoint > clust;
    }

    public Double getPart() {
        return part;// * clust;
    }

    public void setPart(double p) {
        part = Math.max(p, 1);
        // depoVehicle = part.intValue();
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
                ", maxV=" + getPart().intValue() +
                ", comingVehicle=" + comingVehicle +
                ", curr=" + depoVehicle +
                '}';
    }

    @Override
    public int compareTo(PointWithMessage o) {
        return o.clust.compareTo(clust);
    }
}