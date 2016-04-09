package av.VRP.rt.substance;

/**
 * Created by Artem on 09.04.2016.
 */

public class Point {
    private double lat = 0d;
    private double lng = 0d;

    public int x = 0;
    public int y = 0;//FIXME remove

    public Point(double la, double ln) {
        lat = la;
        lng = ln;
    }

    public Point(int la, int ln) {
        x = la;
        y = ln;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getStrLatLng() {
        return getLat() + "," + getLng();
    }

    public boolean equals(Point p) {//FIXME for float
        return (p.lat == this.lat) && (p.lng == this.lng);
    }

    @Override
    public String toString() {
        return "Point [lat=" + lat + ", lng=" + lng + "]";
    }
}