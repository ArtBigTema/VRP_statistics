package av.VRP.rt.map;

import av.VRP.rt.Main;
import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.substance.Point;
import av.VRP.rt.substance.*;
import com.teamdev.jxmaps.*;
import com.teamdev.jxmaps.Icon;
import com.teamdev.jxmaps.swing.MapView;

import javax.swing.*;
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

    private InfoWindow infoWindow;

    private List<Marker> clusterMarkers;
    private List<Marker> passageMarkersStart;
    private List<Marker> passageMarkersEnd;
    private List<Marker> vehicleMarkers;
    private List<Marker> passageMarkersFailed;


    public MapExample() {
        vehicleMarkers = new ArrayList<>();
        clusterMarkers = new ArrayList<>();
        passageMarkersStart = new ArrayList<>();
        passageMarkersEnd = new ArrayList<>();
        passageMarkersFailed = new ArrayList<>();

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
                    map.setCenter(new LatLng(40.703116, -74.016860));
                    // Setting initial zoom value
                    map.setZoom(13.0);

                    map.addEventListener("zoom_changed", new MapEvent() {
                        @Override
                        public void onEvent() {
                            Main.getInstance().showCluster(MapExample.this);
                        }
                    });
                    map.addEventListener("click", new MapMouseEvent() {
                        @Override
                        public void onEvent(MouseEvent mouseEvent) {
                            // Closing initially created info window
                            closeAllInfoWindow();
                        }
                    });
                }
            }
        });
    }

    public void showVehicle(Vehicle vehicle) {
        Map map = getMap();

        Marker marker = new Marker(map);

        Icon icon = new Icon();
        File file = MapUtils.getVehicleIcon(vehicle.getFileIcon());
        icon.loadFromFile(file);
        marker.setIcon(icon);
        marker.setTitle(vehicle.getTitle());

        marker.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                if (infoWindow != null) {
                    infoWindow.close();
                }
                infoWindow = new InfoWindow(getMap());
                infoWindow.setContent(marker.getTitle());
                infoWindow.open(getMap(), marker);
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

            marker.setTitle(vehicle.getTitle());
            marker.setClickable(true);
            marker.setPosition(vehicle.getCurrPoint().toLatLng());

            marker.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(MouseEvent mouseEvent) {
                    if (infoWindow != null) {
                        infoWindow.close();
                    }
                    infoWindow = new InfoWindow(getMap());
                    infoWindow.setContent(marker.getTitle());
                    infoWindow.open(getMap(), marker);
                }
            });

            vehicleMarkers.add(marker);
        }
    }

    public void showCluster(Cluster cluster) {
        for (Marker clusterMarker : clusterMarkers) {
            clusterMarker.remove();
        }
        clusterMarkers.clear();

        Map map = getMap();

        for (PointWithMessage point : cluster.getClusters()) {
            Marker marker = new Marker(map);

            Icon icon = new Icon();
            File file = MapUtils.getIcon(point.getClust());
            icon.loadFromFile(file);
            marker.setIcon(icon);

            marker.setClickable(true);
            marker.setTitle(point + ":" + point.getMsg());
            marker.setPosition(point.getLatLng());

            marker.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(MouseEvent mouseEvent) {
                    if (infoWindow != null) {
                        infoWindow.close();
                    }
                    infoWindow = new InfoWindow(getMap());
                    infoWindow.setContent(marker.getTitle());
                    infoWindow.open(getMap(), marker);
                }
            });

            clusterMarkers.add(marker);
        }
    }

    public void showAllPoints(Trips trips, boolean visible) {
        Log.p("start showAllPoints");
        List<Trip> points = trips.getSubAll();

        Map map = getMap();
        boolean bigSize = points.size() > Constant.BIG_SIZE;
        boolean middleSize = points.size() > Constant.MIDDLE_SIZE;

        for (Trip point : points) {
            Marker marker = new Marker(map);
            marker.setPosition(point.getLatLngStart());
            marker.setClickable(true);

            //  marker.setIcon("https://habrahabr.ru/images/favicons/apple-touch-icon-57x57.png");

            marker.setVisible(visible);

            Marker markerEnd = new Marker(map);
            markerEnd.setPosition(point.getLatLngEnd());
            markerEnd.setVisible(visible);

            markerEnd.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(MouseEvent mouseEvent) {
                    closeAllInfoWindowForClients();
                    infoWindowClientEnd = new InfoWindow(getMap());
                    infoWindowClientEnd.setContent("#" + points.indexOf(point));
                    infoWindowClientEnd.open(getMap(), markerEnd);
                    infoWindowClientStart = new InfoWindow(getMap());
                    infoWindowClientStart.setContent("#" + points.indexOf(point));
                    infoWindowClientStart.open(getMap(), marker);
                }
            });
            marker.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(MouseEvent mouseEvent) {
                    closeAllInfoWindowForClients();
                    infoWindowClientStart = new InfoWindow(getMap());
                    infoWindowClientStart.setContent("#" + points.indexOf(point));
                    infoWindowClientStart.open(getMap(), marker);
                    infoWindowClientEnd = new InfoWindow(getMap());
                    infoWindowClientEnd.setContent("#" + points.indexOf(point));
                    infoWindowClientEnd.open(getMap(), markerEnd);
                }
            });

            if (!bigSize) {
                Icon icon = new Icon();
                File file = visible ? MapUtils.getIconSmallStart() : MapUtils.getIconBigStart();
                icon.loadFromFile(file);
                markerEnd.setIcon(icon);

                Icon icon1 = new Icon();
                File file1 = visible ? MapUtils.getIconSmallEnd() : MapUtils.getIconBigEnd();
                icon1.loadFromFile(file1);
                marker.setIcon(icon1);
            }

            passageMarkersStart.add(marker);
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
        for (Marker vehicleMarker : passageMarkersFailed) {
            vehicleMarker.remove();
        }
        passageMarkersEnd.clear();
        vehicleMarkers.clear();
        passageMarkersFailed.clear();
    }

    public void toggleVehicle(int index, int i) {
        Marker marker = vehicleMarkers.get(index);
        marker.setVisible(true);

        if (infoWindowTaxi != null) {
            infoWindowTaxi.close();
        }
        infoWindowTaxi = new InfoWindow(getMap());
        infoWindowTaxi.setContent("#" + index + " Я Ближайший к #" + i);
        infoWindowTaxi.open(getMap(), marker);

        Log.p(marker.getPosition(), index);
    }

    public void togglePasseger(int indexPassage, int indexVehicle, boolean wait) {
        Marker marker = passageMarkersStart.get(indexPassage);
        Marker markerEnd = passageMarkersEnd.get(indexPassage);
        // marker.remove();
        Log.p(marker.getPosition(), indexPassage);

        if (!wait) {
            if (infoWindow != null) {
                infoWindow.close();
            }
            infoWindow = new InfoWindow(getMap());
            infoWindow.setPosition(marker.getPosition());
            infoWindow.setContent("Жду");
            infoWindow.open(getMap(), marker);
            return;
        }

        closeAllInfoWindowForClients();

        infoWindowClientStart = new InfoWindow(getMap());
        infoWindowClientEnd = new InfoWindow(getMap());

        infoWindowClientStart.setContent("#" + indexPassage + " Нашлась такси: " + indexVehicle);
        infoWindowClientEnd.setContent("Едем сюда");

        infoWindowClientStart.open(getMap(), marker);
        infoWindowClientEnd.open(getMap(), markerEnd);

        marker.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                closeAllInfoWindowForClients();
                infoWindowClientEnd = new InfoWindow(getMap());
                infoWindowClientEnd.setContent("#" + indexPassage);
                infoWindowClientEnd.open(getMap(), markerEnd);
                infoWindowClientStart = new InfoWindow(getMap());
                infoWindowClientStart.setContent("#" + indexPassage + " Нашлась такси: #" + (indexVehicle));
                infoWindowClientStart.open(getMap(), marker);
            }
        });
        markerEnd.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                closeAllInfoWindowForClients();
                infoWindowClientStart = new InfoWindow(getMap());
                infoWindowClientStart.setContent("#" + indexPassage + " Нашлась такси: #" + (indexVehicle));
                infoWindowClientStart.open(getMap(), marker);
                infoWindowClientEnd = new InfoWindow(getMap());
                infoWindowClientEnd.setContent("#" + indexPassage);
                infoWindowClientEnd.open(getMap(), markerEnd);
            }
        });
        vehicleMarkers.get(indexVehicle).addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                closeAllInfoWindowForClients();
                infoWindowClientStart = new InfoWindow(getMap());
                infoWindowClientStart.setContent("#" + indexPassage + " Нашлась такси: #" + (indexVehicle));
                infoWindowClientStart.open(getMap(), marker);
                infoWindowClientEnd = new InfoWindow(getMap());
                infoWindowClientEnd.setContent("#" + indexPassage);
                infoWindowClientEnd.open(getMap(), markerEnd);
            }
        });
    }

    public void showPasseger(Integer i) {
        passageMarkersStart.get(i).setVisible(true);
        passageMarkersEnd.get(i).setVisible(true);
    }

    public void showPoints(List<Trip> trips, boolean isStart) {
        Map map = getMap();

        boolean bigSize = trips.size() > Constant.BIG_SIZE;
        boolean middleSize = trips.size() > Constant.MIDDLE_SIZE;

        for (Trip point : trips) {
            Marker marker = new Marker(map);
            marker.setPosition(isStart
                    ? point.getLatLngStart() : point.getLatLngEnd());
            marker.setTitle("#" + trips.indexOf(point));

            marker.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(MouseEvent mouseEvent) {
                    if (infoWindow != null) {
                        infoWindow.close();
                    }
                    infoWindow = new InfoWindow(getMap());
                    infoWindow.setContent(marker.getTitle());
                    infoWindow.open(getMap(), marker);
                }
            });

            if (!bigSize) {
                Icon icon = new Icon();
                //fixme h
                File file = middleSize ? isStart ? MapUtils.getIconSmallStart() : MapUtils.getIconSmallEnd() : isStart ? MapUtils.getIconBigStart() : MapUtils.getIconBigEnd();
                icon.loadFromFile(file);
                marker.setIcon(icon);
           }
            passageMarkersStart.add(marker);
        }
    }

    public void moveVehicle(int i, Point currPoint) {
        vehicleMarkers.get(i).setPosition(currPoint.toLatLng());
    }

    public void removeClientMarkers(int indexOfTrip) {
        passageMarkersStart.get(indexOfTrip).setVisible(false);
        passageMarkersEnd.get(indexOfTrip).setVisible(false);

        // Log.pp(indexOfTrip,passageMarkersStart.get(indexOfTrip).getVisible());
        // Log.pp(indexOfTrip,passageMarkersEnd.get(indexOfTrip).getVisible());
    }

    public synchronized void showMessVehicleComplete(int i) {
        Marker marker = vehicleMarkers.get(i);
        if (infoWindow != null) {
            infoWindow.close();
        }

        marker.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                if (infoWindowTaxi != null) {
                    infoWindowTaxi.close();
                }
                if (infoWindow != null) {
                    infoWindow.close();
                }
                infoWindowTaxi = new InfoWindow(getMap());
                infoWindowTaxi.setContent(marker.getTitle());
                infoWindowTaxi.open(getMap(), marker);
            }
        });
        infoWindow = new InfoWindow(getMap());
        infoWindow.setContent("Completed");
        infoWindow.open(getMap(), marker);
    }

    public void toggleFailPasseger(int i, LatLng latLng) {
        Marker markerFail = new Marker(getMap());
        markerFail.setPosition(latLng);

        passageMarkersStart.get(i).setVisible(false);
        passageMarkersEnd.get(i).setVisible(false);

        Icon icon = new Icon();
        File file = MapUtils.getIconFail();
        icon.loadFromFile(file);
        markerFail.setIcon(icon);

        if (infoWindow != null) {
            infoWindow.close();
        }
        markerFail.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                InfoWindow infoWindow = new InfoWindow(getMap());
                infoWindow.setContent("#" + i + " Failed");
                infoWindow.open(getMap(), markerFail);
            }
        });
        infoWindow = new InfoWindow(getMap());
        infoWindow.setContent("Failed");
        infoWindow.open(getMap(), markerFail);

        passageMarkersFailed.add(markerFail);
    }

    public void clearFailedOrders(int i) {
        if (i % 20 == 0) {
            closeAllInfoWindow();
        }
        if (i % 10 != 0) {
            return;
        }
        for (Marker vehicleMarker : passageMarkersFailed) {
            vehicleMarker.remove();
        }
        passageMarkersFailed.clear();

        closeAllInfoWindow();
    }

    public void closeAllInfoWindowForClients() {
        if (infoWindowClientStart != null) {
            infoWindowClientStart.close();
        }
        if (infoWindowClientEnd != null) {
            infoWindowClientEnd.close();
        }
    }

    public void closeAllInfoWindow() {
        if (infoWindow != null) {
            infoWindow.close();
        }
        if (infoWindowTaxi != null) {
            infoWindowTaxi.close();
        }
        closeAllInfoWindowForClients();
    }

    public void showMsgFinish(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showPoint(Trip trip, int i){
        Marker marker = new Marker(getMap());
        marker.setPosition(trip.getLatLngStart());
        marker.setTitle("#" + i);
        Icon icon = new Icon();
        File file =  MapUtils.getIconSmallEnd();
        icon.loadFromFile(file);
        marker.setIcon(icon);
    }
}