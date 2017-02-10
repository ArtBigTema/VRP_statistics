package av.VRP.rt.map;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.substance.Point;
import ch.hsr.geohash.GeoHash;
import com.teamdev.jxmaps.LatLng;

import java.io.File;

import static java.lang.Math.*;
import static java.lang.StrictMath.atan2;

/**
 * Created by Artem on 08.02.2017.
 */
public class MapUtils {
    private static final short DEFAULT_TILE_SIZE = 256;

    public static String getHash(LatLng latLng) {
        return GeoHash.geoHashStringWithCharacterPrecision(latLng.getLat(), latLng.getLng(), Constant.CLUSTERS);
    }

    public static String getHash(double lat, double lng) {
        return GeoHash.geoHashStringWithCharacterPrecision(lat, lng, Constant.CLUSTERS);
    }

    public static File getIcon(int count) {
        File file = new File("icons/number_" + count + ".png");
        if (file == null || !file.exists()) {
            file = new File("icons/number_0.png");
        }
        return file;
    }

    public static File getVehicleIcon(String path) {
        File file = new File(path);
        if (file == null || !file.exists()) {
            file = new File("vi/0.png");
        }
        return file;
    }

    public static File getVehicleIcon(int count) {
        File file = new File("vi/" + count + ".png");
        if (file == null || !file.exists()) {
            file = new File("vi/0.png");
        }
        return file;
    }

    public static File getIconFu() {
        return new File("icons/fu.png");
    }
    public static File getIconFail() {
        return new File("icons/fail.png");
    }

    public static double deg2rad(final double degree) {
        return degree * (Math.PI / 180);
    }

    public static Double getDistance(Point subwayStationPoint, Point addressPoint) {
        final double EARTH_RADIUS = 6371d;

        final double dlng = deg2rad(subwayStationPoint.getLng() - addressPoint.getLng());
        final double dlat = deg2rad(subwayStationPoint.getLat() - addressPoint.getLat());
        final double a = sin(dlat / 2) * sin(dlat / 2) + cos(deg2rad(addressPoint.getLat()))
                * cos(deg2rad(subwayStationPoint.getLat())) * sin(dlng / 2) * sin(dlng / 2);
        final double c = 2 * atan2(sqrt(a), sqrt(1 - a));

        return c * EARTH_RADIUS;
    }
}