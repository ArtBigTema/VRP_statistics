package av.VRP.rt.map;

import av.VRP.rt.Main;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.substance.*;
import com.teamdev.jxmaps.*;
import com.teamdev.jxmaps.swing.MapView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artem on 07.02.2017.
 */
public class MapExample extends MapView {
    private InfoWindow infoWindow;
    private Cluster cluster;

    private List<Marker> clusterMarkers;
    private List<Marker> passageMarkers;
    private List<Marker> vehicleMarkers;


    public MapExample() {
        vehicleMarkers = new ArrayList<>();
        clusterMarkers = new ArrayList<>();
        passageMarkers = new ArrayList<>();

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

                    map.addEventListener("zoom_changed", new MapEvent() {
                        @Override
                        public void onEvent() {
                            Main.getInstance().zoom(map.getZoom());
                        }
                    });
                }
            }
        });
    }

    public void constructCluster(Trips trips, Vehicles vehicles) {
        Log.p("start constructCluster");

        List<Trip> points = trips.getSubAll();
        cluster = new Cluster();

        for (Trip point : points) {
            cluster.add(point.getStartPoint());
        }

        Log.p("end constructCluster");

        showCluster(points.get(0).getLatLngStart());
        showVehicles(vehicles);
    }

    public void showVehicle(Vehicle vehicle) {
        Map map = getMap();

        Marker marker = new Marker(map);

        Icon icon = new Icon();
        File file = MapUtils.getVehicleIcon(vehicle.getFileIcon());
        icon.loadFromFile(file);
        marker.setIcon(icon);

        marker.setClickable(true);
        marker.setPosition(vehicle.getCurrPoint().toLatLng());

        vehicleMarkers.add(marker);
    }

    public void showVehicles(Vehicles vehicles) {
        Map map = getMap();

        for (Vehicle vehicle : vehicles.getVehicles()) {
            Marker marker = new Marker(map);

            Icon icon = new Icon();
            File file = MapUtils.getVehicleIcon(vehicle.getFileIcon());
            icon.loadFromFile(file);
            marker.setIcon(icon);

            marker.setClickable(true);
            marker.setPosition(vehicle.getCurrPoint().toLatLng());

            vehicleMarkers.add(marker);
        }
    }

    private void showCluster(LatLng center) {
        Map map = getMap();

        map.setCenter(center);

        for (PointWithMessage point : cluster.getClusters()) {
            Marker marker = new Marker(map);

            Icon icon = new Icon();
            File file = MapUtils.getIcon(point.getClust());
            icon.loadFromFile(file);
            marker.setIcon(icon);

            marker.setClickable(true);
            marker.setPosition(point.toLatLng());
            // marker.set
            marker.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(MouseEvent mouseEvent) {
                    Log.p("marker clicked: ", MapUtils.getHash(marker.getPosition()));

                    if (infoWindow != null) {
                        infoWindow.close();
                        infoWindow = null;
                        marker.remove();
                    } else {
                        infoWindow = new InfoWindow(getMap());
                        infoWindow.setContent(point.getMsg());
                        infoWindow.open(getMap(), marker);
                    }
                    // showBounds();
                }
            });

            clusterMarkers.add(marker);
        }
    }

    public void showAllPoints(Trips trips) {
        showAllPoints(trips, true);
    }

    public void showAllPoints(Trips trips, boolean visible) {
        Log.p("start showAllPoints");
        List<Trip> points = trips.getSubAll();

        Map map = getMap();

        map.setCenter(points.get(0).getLatLngStart());

        for (Trip point : points) {
            Marker marker = new Marker(map);
            marker.setPosition(point.getLatLngStart());
            marker.setClickable(true);

            //  marker.setIcon("https://habrahabr.ru/images/favicons/apple-touch-icon-57x57.png");

            marker.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(MouseEvent mouseEvent) {
                    Log.p("marker clicked: ", point.getStr());
                    Log.p("marker clicked: ", MapUtils.getHash(point.getLatLngStart()));

                    if (infoWindow != null) {
                        infoWindow.close();
                        infoWindow = null;
                        marker.remove();
                    } else {
                        infoWindow = new InfoWindow(getMap());
                        infoWindow.setContent(MapUtils.getHash(point.getLatLngStart()));
                        infoWindow.open(getMap(), marker);
                    }
                    // showBounds();
                }
            });
            marker.setVisible(visible);
            passageMarkers.add(marker);
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

    public void clear() {
        for (Marker clusterMarker : clusterMarkers) {
            clusterMarker.remove();
        }
        for (Marker passageMarker : passageMarkers) {
            passageMarker.remove();
        }
    }

    public void clearAll() {
        clear();
        for (Marker vehicleMarker : vehicleMarkers) {
            vehicleMarker.remove();
        }
    }

    public void toggleVehicle(int index) {
        Marker marker = vehicleMarkers.get(index);

        if (infoWindow != null) {
            infoWindow.close();
        }
        infoWindow = new InfoWindow(getMap());
        infoWindow.setContent("Ближайшее");
        infoWindow.open(getMap(), marker);
    }

    public void togglePasseger(boolean find, int index) {
        Marker marker = passageMarkers.get(index);

        InfoWindow infoWindow = new InfoWindow(getMap());
        infoWindow.setContent(find ? "Нашлась" : "Не найдено");
        infoWindow.open(getMap(), marker);
    }

    public void showPasseger(Integer i) {
        passageMarkers.get(i).setVisible(true);
    }

    public void showPoints(List<Trip> trips) {
        Map map = getMap();

        for (Trip point : trips) {
            Marker marker = new Marker(map);
            marker.setPosition(point.getLatLngStart());

            Icon icon = new Icon();
            File file = MapUtils.getIconFu();
            icon.loadFromFile(file);
            marker.setIcon(icon);
        }
    }
}