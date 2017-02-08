package av.VRP.rt.map;

import av.VRP.rt.Utils.Log;
import av.VRP.rt.Utils.MapUtils;
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
    private Cluster cluster;

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

    public void constructCluster(Trips trips) {
        Log.p("start constructCluster");

        List<Trip> points = trips.getAll();
        cluster = new Cluster();

        int i = 0;
        for (Trip point : points) {
            cluster.add(point.getStartPoint());

            if (i++ > 10) {
                Log.e("cluster count: ", cluster.getClusters());
                break;
            }
        }

        Log.p("end constructCluster");
    }

    public void showAllPoints(Trips trips) {
        Log.p("start showAllPoints");
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
                    Log.p("marker clicked: ", point.getStr());

                    if (infoWindow != null) {
                        infoWindow.close();

                    }

                    infoWindow = new InfoWindow(getMap());
                    infoWindow.setContent(MapUtils.getHash(point.getLatLngStart()));
                    infoWindow.open(getMap(), marker);

                    // showBounds();
                }
            });

            if (i++ > 100) {
                break;
            }
        }
        Log.p("end showAllPoints");
    }

    private void showBounds() {
        Marker marker = new Marker(getMap());
        LatLngBounds bounds = getMap().getBounds();
        marker.setPosition(bounds.getNorthEast());
        marker.setIcon("https://habrahabr.ru/images/favicons/apple-touch-icon-57x57.png");

        Marker marker1 = new Marker(getMap());
        marker1.setPosition(bounds.getSouthWest());
        marker1.setIcon("https://fossies.org/warix/comments.gif");


    }

    public void setZoom(int zoom) {
        getMap().setZoom(zoom);
    }
}