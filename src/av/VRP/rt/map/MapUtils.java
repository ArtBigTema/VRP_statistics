package av.VRP.rt.map;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.substance.Point;
import ch.hsr.geohash.GeoHash;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.LatLngBounds;

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

    public static LatLng lonLatToPixelXY(double longitude, double latitude,
                                         double zoomLevel, LatLngBounds bounds) {
        //  Log.p("SouWest",bounds.getSouthWest());
        //  Log.p("NorEast",bounds.getNorthEast());
        double _latitude = clip(latitude,
                bounds.getSouthWest().getLat(),
                bounds.getNorthEast().getLat());
        double _longitude = clip(longitude,
                bounds.getNorthEast().getLng(),
                bounds.getSouthWest().getLng());

        double x = (_longitude + 180) / 360;
        double sinLatitude = sin(_latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude))
                / (4 * Math.PI);

        int mapSize = 256 << (int) zoomLevel;// fixme
        int pixelX = (int) clip(x * mapSize + 0.5, 0, mapSize - 1);
        int pixelY = (int) clip(y * mapSize + 0.5, 0, mapSize - 1);

        return new LatLng(pixelX, pixelY);
    }


    public static LatLng pixelXYToLonLat(int pixelX, int pixelY, int zoomLevel) {
        double mapSize = mapSize(zoomLevel);
        double x = (clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        double y = 0.5 - (clip(pixelY, 0, mapSize - 1) / mapSize);

        double latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI))
                / Math.PI;
        double longitude = 360 * x;

        return new LatLng(longitude, latitude);
    }

    /**
     * <q>Determines the map width and height (in pixels) at a specified level
     * of detail.</q>
     *
     * @param zoomLevel Zoom level or "Level of detail", from 1 (lowest detail)
     *                  to 23 (highest detail)
     * @return the map size
     */
    public static int mapSize(int zoomLevel) {
        return DEFAULT_TILE_SIZE << zoomLevel;
    }

    /**
     * <q>Converts pixel XY coordinates into tile XY coordinates of the tile
     * containing the specified pixel.</q>
     *
     * @param pixelX Pixel X coordinate.
     * @param pixelY Pixel Y coordinate.
     * @return
     */
    public static int[] pixelXYToTileXY(int pixelX, int pixelY) {
        int tileX = pixelX / DEFAULT_TILE_SIZE;
        int tileY = pixelY / DEFAULT_TILE_SIZE;

        return new int[]{tileX, tileY};
    }

    /**
     * <q>Converts tile XY coordinates into a QuadKey at a specified level of
     * detail.</q>
     *
     * @param tileX     Tile X coordinate.
     * @param tileY     Tile Y coordinate.
     * @param zoomLevel Zoom level or "Level of detail", from 1 (lowest detail)
     *                  to 23 (highest detail)
     * @return A string containing the QuadKey.
     */
    public static String tileXYToQuadKey(int tileX, int tileY, int zoomLevel) {
        StringBuilder quadKey = new StringBuilder();
        for (int i = zoomLevel; i > 0; i--) {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0) {
                digit++;
            }
            if ((tileY & mask) != 0) {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }

        return quadKey.toString();
    }

    /**
     * <q>Clips a number to the specified minimum and maximum values.</q>
     *
     * @param n        The number to clip.
     * @param minValue Minimum allowable value.
     * @param maxValue Maximum allowable value.
     * @return The clipped value.
     */
    private static double clip(double n, double minValue, double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    /**
     * Finds the quadkey of a tile for a given pair of coordinates and at a
     * given zoom level.
     *
     * @param lon       The longitude
     * @param lat       The latitude
     * @param zoomLevel The zoom level
     * @return A string denoting the quadkey of the tile.
     */
    public static String lonLatToQuadKey(double lon, double lat, int zoomLevel) {
        LatLng latLng = lonLatToPixelXY(lon, lat, zoomLevel, null);
        int[] pixelXY = {(int) latLng.getLat(), (int) latLng.getLng()};
        int[] tileXY = pixelXYToTileXY(pixelXY[0], pixelXY[1]);

        return tileXYToQuadKey(tileXY[0], tileXY[1], zoomLevel);
    }
}