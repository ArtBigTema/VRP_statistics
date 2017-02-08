package av.VRP.rt.map;

import av.VRP.rt.Utils.Log;
import av.VRP.rt.substance.Trip;
import av.VRP.rt.substance.Trips;
import com.teamdev.jxmaps.*;
import com.teamdev.jxmaps.swing.MapView;

import java.util.List;

/**
 * Created by Artem on 07.02.2017.
 */
public class MapExample extends MapView {
    private InfoWindow infoWindow;

    public MapExample() {

        setOnMapReadyHandler(new MapReadyHandler() {
            @Override
            public void onMapReady(MapStatus status) {
                // Check if the map is loaded correctly
                if (status == MapStatus.MAP_STATUS_OK) {
                    // Getting the associated map object
                    final Map map = getMap();
                    // Creating a map options object
                    MapOptions options = new MapOptions();
                    // Creating a map type control options object
                    MapTypeControlOptions controlOptions = new MapTypeControlOptions();
                    // Changing position of the map type control
                    controlOptions.setPosition(ControlPosition.BOTTOM_CENTER);
                    // Setting map type control options
                    options.setMapTypeControlOptions(controlOptions);
                    // Setting map options
                    map.setOptions(options);
                    // Setting the map center
                    map.setCenter(new LatLng(41.3850639, 2.1734034999999494));
                    // Setting initial zoom value
                    map.setZoom(12.0);
                }
            }
        });
    }

    public void showPoints(Trips trips) {
        Log.p("start showPoints");
        List<Trip> points = trips.getAll();

        Map map = getMap();

        map.setCenter(points.get(0).getLatLngStart());

        int i = 0;
        for (Trip point : points) {
            Marker marker = new Marker(map);
            marker.setPosition(point.getLatLngStart());
            marker.setClickable(true);
          //  marker.setIcon("https://habrahabr.ru/images/favicons/apple-touch-icon-57x57.png");

            marker.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(MouseEvent mouseEvent) {
                    Log.p("marker clicked", point.getTimeStr());

                    if (infoWindow != null) {
                        infoWindow.close();
                    }

                    infoWindow = new InfoWindow(getMap());
                    infoWindow.setContent(point.getStr());
                    infoWindow.open(getMap(), marker);
                }
            });

            if (i++ > 100) {
                break;
            }
        }
        Log.p("end showPoints");
    }

    public void setZoom(int zoom) {
        getMap().setZoom(zoom);
    }
}