package av.VRP.rt.substance;

import av.VRP.rt.Utils.Utils;
import av.VRP.rt.map.MapUtils;
import com.teamdev.jxmaps.LatLng;

/**
 * Created by Artem on 09.04.2016.
 */

public class Point {
    private double lat = 0d;
    private double lng = 0d;
    private String hash;

    public boolean isStartPoint = true; //FIXME

    public int x = 0;
    public int y = 0;//FIXME remove


    public Point(double la, double ln) {
        lat = la;
        lng = ln;

        hash = MapUtils.getHash(lat, lng);
    }

    public Point(int la, int ln) {
        x = la;
        y = ln;

        hash = MapUtils.getHash(lat, lng);
    }

    public Point(String la, String ln) {
        // lat = Double.parseDouble(la);
        // lng = Double.parseDouble(ln);
        lat = Double.parseDouble(ln);
        lng = Double.parseDouble(la);

        hash = MapUtils.getHash(lat, lng);
    }

    public static Point construct(String s) {
        String[] elements = Utils.strToArray(s, ",");
        //FIXME if size < 2
        return new Point(elements[1], elements[2]);
    }

    public LatLng toLatLng() {
        return new LatLng(lat, lng);
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getHash() {
        return hash;
    }

    public String getStrLatLng() {
        return getLat() + "," + getLng();
    }

    public boolean equals(Point p) {//FIXME for float
        return (p.lat == this.lat) && (p.lng == this.lng);
    }

    @Override
    public String toString() {
        return "Point{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    public static Point nearby(PointWithTime startPoint) {
        Point point = new Point(
                startPoint.getLat() - 0.01, startPoint.getLng() - 0.01);
        return point;
    }

    public void plusLat(double step) {
        lat += step;
    }

    public void plusLng(double step) {
        lng += step;
    }
    public void minusLat(double step) {
        lat = step;
    }

    public void  minusLng(double step) {
        lng -= step;
    }
}