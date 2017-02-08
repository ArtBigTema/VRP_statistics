package av.VRP.rt.Utils;

import com.teamdev.jxmaps.LatLngBounds;

/**
 * Created by Artem on 08.02.2017.
 */
public class MapUtils {
    public static int[] lonLatToPixelXY(double longitude, double latitude,
                                        double zoomLevel, LatLngBounds bounds) {
        Log.p("SouWest",bounds.getSouthWest());
        Log.p("NorEast",bounds.getNorthEast());
        double _latitude = clip(latitude,
                bounds.getSouthWest().getLat(),
                bounds.getNorthEast().getLat());
        double _longitude = clip(longitude,
                bounds.getNorthEast().getLng(),
                bounds.getSouthWest().getLng());

        double x = (_longitude + 180) / 360;
        double sinLatitude = Math.sin(_latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude))
                / (4 * Math.PI);

        int mapSize = (int) zoomLevel;
        int pixelX = (int) clip(x * mapSize + 0.5, 0, mapSize - 1);
        int pixelY = (int) clip(y * mapSize + 0.5, 0, mapSize - 1);

        return new int[] { pixelX, pixelY };
    }
    public static double clip(double n, double minValue, double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }
}