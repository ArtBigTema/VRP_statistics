package av.VRP.rt.map;

import av.VRP.rt.Main;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.substance.Point;
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
    private InfoWindow infoWindowTaxi;
    private InfoWindow infoWindowClientStart;
    private InfoWindow infoWindowClientEnd;

    private Cluster cluster;

    private List<Marker> clusterMarkers;
    private List<Marker> passageMarkersStart;
    private List<Marker> passageMarkersEnd;
    private List<Marker> vehicleMarkers;


    public MapExample() {
        vehicleMarkers = new ArrayList<>();
        clusterMarkers = new ArrayList<>();
        passageMarkersStart = new ArrayList<>();
        passageMarkersEnd = new ArrayList<>();

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
                    map.setZoom(13.0);

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
        marker.setTitle(file.getName());

        marker.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                InfoWindow infoWindow = new InfoWindow(getMap());
                infoWindow.setContent(marker.getTitle());
                infoWindow.open(getMap(), marker);
                // showBounds();
            }
        });

        marker.setClickable(true);
        marker.setPosition(vehicle.getCurrPoint().toLatLng());

        marker.setVisible(true);

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

            marker.setTitle(file.getName());
            marker.setClickable(true);
            marker.setPosition(vehicle.getCurrPoint().toLatLng());

            marker.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(MouseEvent mouseEvent) {
                    InfoWindow infoWindow = new InfoWindow(getMap());
                    infoWindow.setContent(marker.getTitle());
                    infoWindow.open(getMap(), marker);
                    // showBounds();
                }
            });

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

                    InfoWindow infoWindow = new InfoWindow(getMap());
                    infoWindow.setContent(MapUtils.getHash(point.getLatLngStart()));
                    infoWindow.open(getMap(), marker);
                }
            });
            marker.setVisible(visible);
            passageMarkersStart.add(marker);

            Marker markerEnd = new Marker(map);
            markerEnd.setPosition(point.getLatLngEnd());
            markerEnd.setVisible(visible);

            Icon icon = new Icon();
            File file = MapUtils.getIconFu();
            icon.loadFromFile(file);
            markerEnd.setIcon(icon);

            passageMarkersEnd.add(markerEnd);
        }

        Log.p("end showAllPoints");
    }

    public void setZoom(int zoom) {
        getMap().setZoom(zoom);
    }

    public void clear() {
        for (Marker clusterMarker : clusterMarkers) {
            clusterMarker.remove();
        }
        clusterMarkers.clear();
        for (Marker passageMarker : passageMarkersStart) {
            passageMarker.remove();
        }
        passageMarkersStart.clear();
    }

    public void clearAll() {
        clear();
        for (Marker vehicleMarker : vehicleMarkers) {
            vehicleMarker.remove();
        }
        for (Marker vehicleMarker : passageMarkersEnd) {
            vehicleMarker.remove();
        }
        passageMarkersEnd.clear();
        vehicleMarkers.clear();
    }

    public void toggleVehicle(int index) {
        Marker marker = vehicleMarkers.get(index);
        marker.setVisible(true);

        if (infoWindowTaxi != null) {
            infoWindowTaxi.close();
        }
        infoWindowTaxi = new InfoWindow(getMap());
        infoWindowTaxi.setContent("#" + (index + 1) + " Ближайшее" + marker.getTitle());
        // infoWindowTaxi.open(getMap(), marker);

        Log.p(marker.getPosition(), index);
    }

    public void togglePasseger(boolean find, int indexPassage, int indexVehicle) {
        Marker marker = passageMarkersStart.get(indexPassage);
        Marker markerEnd = passageMarkersEnd.get(indexPassage);
        // marker.remove();
        Log.p(marker.getPosition(), indexPassage);

        if (infoWindowClientStart != null) {
            infoWindowClientStart.close();
        }
        if (infoWindowClientEnd != null) {
            infoWindowClientEnd.close();
        }

        infoWindowClientStart = new InfoWindow(getMap());
        infoWindowClientEnd = new InfoWindow(getMap());

        if (find) {
            infoWindowClientStart.setContent("#" + indexPassage + " Нашлась такси: " + (indexVehicle + 1));
            infoWindowClientEnd.setContent("Едем сюда");
        } else {
            infoWindowClientStart.setContent("Не найдено");
            infoWindowClientEnd.setContent("Хотим сюда");
        }

        //   infoWindowClientStart.open(getMap(), marker);
        //   infoWindowClientEnd.open(getMap(), markerEnd);
    }

    public void showPasseger(Integer i) {
        passageMarkersStart.get(i).setVisible(true);
        passageMarkersEnd.get(i).setVisible(true);
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

    public void moveVehicle(int i, Point currPoint) {
        vehicleMarkers.get(i).setPosition(currPoint.toLatLng());
    }

    public void removeClientMarkers(int indexOfTrip) {
        passageMarkersStart.get(indexOfTrip).setVisible(false);
        passageMarkersEnd.get(indexOfTrip).setVisible(false);
          passageMarkersStart.get(indexOfTrip).remove();
          passageMarkersEnd.get(indexOfTrip).remove();

        passageMarkersStart.get(indexOfTrip).setDraggable(true);
        passageMarkersEnd.get(indexOfTrip).setDraggable(true);

       // Log.pp(indexOfTrip,passageMarkersStart.get(indexOfTrip).getVisible());
       // Log.pp(indexOfTrip,passageMarkersEnd.get(indexOfTrip).getVisible());
    }

    public void showMessVehicleComplete(int i) {
        Marker marker = vehicleMarkers.get(i);
        InfoWindow infoWindow = new InfoWindow(getMap());
        infoWindow.setContent("Completed");
        //   infoWindow.open(getMap(), marker);
    }
}