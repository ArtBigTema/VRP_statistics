package av.VRP.rt.map;

import av.VRP.rt.Main;
import av.VRP.rt.Utils.Constant;
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

    private boolean withInfoWindow;
    private boolean infoWindowForClickOnly;
    private Marker marker;

    public MapExample() {
        infoWindowForClickOnly = true;
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
                            closeAllInfoWindow(true);
                        }
                    });
                    infoWindow = new InfoWindow(getMap());
                    infoWindowClientStart = new InfoWindow(getMap());
                    infoWindowClientEnd = new InfoWindow(getMap());
                    infoWindowTaxi = new InfoWindow(getMap());
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
                //  infoWindow = new InfoWindow(getMap());
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
                    //  infoWindow = new InfoWindow(getMap());
                    infoWindow.setContent(marker.getTitle());
                    infoWindow.open(getMap(), marker);
                }
            });

            vehicleMarkers.add(marker);
        }
    }

    public void showCluster(Cluster cluster) {
        showCluster(cluster, true);
    }

    public void showCluster(Cluster cluster, boolean visible) {
        for (Marker clusterMarker : clusterMarkers) {
            clusterMarker.remove();
        }
        clusterMarkers.clear();

        Map map = getMap();

        for (int i = 0; i < cluster.getClusters().size(); i++) {
            PointWithMessage point = cluster.getClusters().get(i);

            Marker marker = new Marker(map);

            Icon icon = new Icon();
            File file = MapUtils.getIcon(point.getClust());
            icon.loadFromFile(file);
            marker.setIcon(icon);

            marker.setClickable(true);
            marker.setTitle("#" + i +
                    " Max veh " + point.getPart().intValue() +
                    " Curr veh " + point.getDepoVehicle() +
                    " Coming " + point.getComingVehicle() +
                    " Max client " + point.getClust());
            marker.setPosition(point.getLatLng());
            marker.setVisible(visible);

            marker.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(MouseEvent mouseEvent) {
                    if (infoWindow != null) {
                        infoWindow.close();
                    }
                    //    infoWindow = new InfoWindow(getMap());
                    infoWindow.setContent(marker.getTitle());
                    infoWindow.open(getMap(), marker);
                }
            });

            clusterMarkers.add(marker);
        }
    }

    public void showAllPoints(Trips trips, boolean visible) {
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
                    //  infoWindowClientEnd = new InfoWindow(getMap());
                    infoWindowClientEnd.setContent("#" + points.indexOf(point));
                    infoWindowClientEnd.open(getMap(), markerEnd);
                    //  infoWindowClientStart = new InfoWindow(getMap());
                    infoWindowClientStart.setContent("#" + points.indexOf(point));
                    infoWindowClientStart.open(getMap(), marker);
                }
            });
            marker.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(MouseEvent mouseEvent) {
                    closeAllInfoWindowForClients();
                    // infoWindowClientStart = new InfoWindow(getMap());
                    infoWindowClientStart.setContent("#" + points.indexOf(point));
                    infoWindowClientStart.open(getMap(), marker);
                    // infoWindowClientEnd = new InfoWindow(getMap());
                    infoWindowClientEnd.setContent("#" + points.indexOf(point));
                    infoWindowClientEnd.open(getMap(), markerEnd);
                }
            });

            if (!bigSize) {
                Icon icon = new Icon();
                File file = visible ? MapUtils.getIconSmallEnd() : MapUtils.getIconBigEnd();
                icon.loadFromFile(file);
                markerEnd.setIcon(icon);

                Icon icon1 = new Icon();
                File file1 = visible ? MapUtils.getIconSmallStart() : MapUtils.getIconBigStart();
                icon1.loadFromFile(file1);
                marker.setIcon(icon1);
            }

            passageMarkersStart.add(marker);
            passageMarkersEnd.add(markerEnd);
        }
    }

    public void setZoom(int zoom) {
        getMap().setZoom(zoom);
    }

    public void setShowInfo(boolean b) {
        infoWindowForClickOnly = b;
    }

    public void clear() {
        for (Marker clusterMarker : clusterMarkers) {
            clusterMarker.remove();
        }
        clusterMarkers.clear();
        for (Marker passageMarker : passageMarkersStart) {
            if (passageMarker != null) {
                passageMarker.remove();
            }
        }
        passageMarkersStart.clear();
    }

    public void clearAll() {
        clear();
        for (Marker vehicleMarker : vehicleMarkers) {
            vehicleMarker.remove();
        }
        for (Marker vehicleMarker : passageMarkersEnd) {
            if (vehicleMarker != null) {
                vehicleMarker.remove();
            }
        }
        for (Marker vehicleMarker : passageMarkersFailed) {
            vehicleMarker.remove();
        }
        passageMarkersEnd.clear();
        vehicleMarkers.clear();
        passageMarkersFailed.clear();
    }

    public void removeCluster(int index) {
        Marker marker = clusterMarkers.get(index);
        marker.setVisible(false);
    }

    public void toggleCluster(int index, int i, int count) {
        Marker marker = clusterMarkers.get(index);
        marker.setVisible(false);

        Marker marker1 = new Marker(getMap());
        marker1.setPosition(marker.getPosition());
        marker1.setTitle(marker.getTitle());

        Icon icon = new Icon();
        File file = MapUtils.getIcon(count);
        icon.loadFromFile(file);
        marker1.setIcon(icon);

        clusterMarkers.set(index, marker1);

        closeAllInfoWindow();

        marker1.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                closeAllInfoWindowForClients();
                if (infoWindow != null) {
                    infoWindow.close();
                }
                //  infoWindow = new InfoWindow(getMap());
                infoWindow.setContent(marker1.getTitle());
                infoWindow.open(getMap(), marker1);

                if (infoWindowTaxi != null) {
                    infoWindowTaxi.close();
                }
                //  infoWindowTaxi = new InfoWindow(getMap());
                infoWindowTaxi.setContent("#" + index + " Ближайший #" + i + " Там " + count);
                //   infoWindowTaxi.setContent("#" + i + " Еду в депо #" + index);
                infoWindowTaxi.open(getMap(), vehicleMarkers.get(i));
            }
        });
    }

    public void toggleVehicle(int index, int i) {
        Marker marker = vehicleMarkers.get(index);
        marker.setVisible(true);

        if (infoWindowForClickOnly) {
            return;
        }
        if (infoWindowTaxi != null) {
            infoWindowTaxi.close();
        }
        //  infoWindowTaxi = new InfoWindow(getMap());
        infoWindowTaxi.setContent("#" + index + " Я Ближайший к #" + i);
        infoWindowTaxi.open(getMap(), marker);

    }

    public void togglePasseger(int indexPassage, int indexVehicle, boolean transfer) {
        Marker marker = passageMarkersStart.get(indexPassage);
        Marker markerEnd = passageMarkersEnd.get(indexPassage);
        // marker.remove();

        if (!transfer) {
            if (infoWindowForClickOnly) {
                return;
            }
            if (infoWindow != null) {
                infoWindow.close();
            }
            // infoWindow = new InfoWindow(getMap());
            infoWindow.setPosition(marker.getPosition());
            infoWindow.setContent("Жду");
            infoWindow.open(getMap(), marker);
            return;
        }
        markerEnd.setVisible(true);

        marker.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                closeAllInfoWindowForClients();
                // infoWindowClientEnd = new InfoWindow(getMap());
                infoWindowClientEnd.setContent("#" + indexPassage);
                infoWindowClientEnd.open(getMap(), markerEnd);
                //  infoWindowClientStart = new InfoWindow(getMap());
                infoWindowClientStart.setContent("#" + indexPassage + " Нашлась такси: #" + (indexVehicle));
                infoWindowClientStart.open(getMap(), marker);
            }
        });
        markerEnd.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                closeAllInfoWindowForClients();
                // infoWindowClientStart = new InfoWindow(getMap());
                infoWindowClientStart.setContent("#" + indexPassage + " Нашлась такси: #" + (indexVehicle));
                infoWindowClientStart.open(getMap(), marker);
                // infoWindowClientEnd = new InfoWindow(getMap());
                infoWindowClientEnd.setContent("#" + indexPassage);
                infoWindowClientEnd.open(getMap(), markerEnd);
            }
        });
        vehicleMarkers.get(indexVehicle).addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                closeAllInfoWindowForClients();
                // infoWindowClientStart = new InfoWindow(getMap());
                infoWindowClientStart.setContent("#" + indexPassage + " Нашлась такси: #" + (indexVehicle));
                infoWindowClientStart.open(getMap(), marker);
                //  infoWindowClientEnd = new InfoWindow(getMap());
                infoWindowClientEnd.setContent("#" + indexPassage);
                infoWindowClientEnd.open(getMap(), markerEnd);
            }
        });

        if (infoWindowForClickOnly) {
            return;
        }
        closeAllInfoWindowForClients();

        // infoWindowClientStart = new InfoWindow(getMap());
        // infoWindowClientEnd = new InfoWindow(getMap());

        infoWindowClientStart.setContent("#" + indexPassage + " Нашлась такси: " + indexVehicle);
        infoWindowClientEnd.setContent("Едем сюда");

        infoWindowClientStart.open(getMap(), marker);
        infoWindowClientEnd.open(getMap(), markerEnd);
    }

    public void showPasseger(Integer i) {
        passageMarkersStart.get(i).setVisible(true);
        //  passageMarkersEnd.get(i).setVisible(true);
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
                    } else {
                        infoWindow = new InfoWindow(getMap());
                    }
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
        //   passageMarkersStart.set(indexOfTrip, null);
        //  passageMarkersEnd.set(indexOfTrip, null);
    }

    public void togglePassegerTransfer(int indexPass, int indexVeh) {
        Marker marker = vehicleMarkers.get(indexVeh);
        Marker markerEnd = passageMarkersEnd.get(indexPass);
        Marker markerStart = passageMarkersStart.get(indexPass);

        marker.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                closeAllInfoWindow();
                // infoWindowClientEnd = new InfoWindow(getMap());
                infoWindowClientEnd.setContent("#" + indexPass + " Едем сюда в #" + indexVeh);
                infoWindowClientEnd.open(getMap(), markerEnd);
                // infoWindowClientStart = new InfoWindow(getMap());
                infoWindowClientStart.setContent("#" + indexPass + " Еду в #" + indexVeh);
                infoWindowClientStart.open(getMap(), markerStart);
                //  infoWindowTaxi = new InfoWindow(getMap());
                infoWindowTaxi.setContent("#" + indexVeh + " Подобрал #" + indexPass);
                infoWindowTaxi.open(getMap(), marker);
            }
        });
        markerEnd.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                closeAllInfoWindow();
                //  infoWindowClientStart = new InfoWindow(getMap());
                infoWindowClientStart.setContent("#" + indexPass + " Еду в #" + indexVeh);
                infoWindowClientStart.open(getMap(), markerStart);
                //  infoWindowTaxi = new InfoWindow(getMap());
                infoWindowTaxi.setContent("#" + indexVeh + " Подобрал #" + indexPass);
                infoWindowTaxi.open(getMap(), marker);
                //   infoWindowClientEnd = new InfoWindow(getMap());
                infoWindowClientEnd.setContent("#" + indexPass + " Едем сюда в #" + indexVeh);
                infoWindowClientEnd.open(getMap(), markerEnd);
            }
        });
        markerStart.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                closeAllInfoWindow();
                //  infoWindowClientEnd = new InfoWindow(getMap());
                infoWindowClientEnd.setContent("#" + indexPass + " Едем сюда в #" + indexVeh);
                infoWindowClientEnd.open(getMap(), markerEnd);
                //   infoWindowTaxi = new InfoWindow(getMap());
                infoWindowTaxi.setContent("#" + indexVeh + " Подобрал #" + indexPass);
                infoWindowTaxi.open(getMap(), marker);
                //   infoWindowClientStart = new InfoWindow(getMap());
                infoWindowClientStart.setContent("#" + indexPass + " Еду в #" + indexVeh);
                infoWindowClientStart.open(getMap(), markerStart);
            }
        });

        if (infoWindowForClickOnly) {
            return;
        }
        if (infoWindowTaxi != null) {
            infoWindowTaxi.close();
        }
        //  infoWindowTaxi = new InfoWindow(getMap());
        infoWindowTaxi.setContent("#" + indexVeh + " Подобрал #" + indexPass);
        infoWindowTaxi.open(getMap(), marker);
    }

    public synchronized void showMessVehicleComplete(int i, int depo) {
        Marker marker = vehicleMarkers.get(i);

        marker.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                if (infoWindowTaxi != null) {
                    infoWindowTaxi.close();
                }
                if (infoWindow != null) {
                    infoWindow.close();
                }
                // infoWindowTaxi = new InfoWindow(getMap());
                infoWindowTaxi.setContent(marker.getTitle());
                infoWindowTaxi.open(getMap(), marker);

                //  infoWindow = new InfoWindow(getMap());
                infoWindow.setContent(clusterMarkers.get(depo).getTitle());
                infoWindow.open(getMap(), clusterMarkers.get(depo));
            }
        });

        if (infoWindowForClickOnly) {
            return;
        }
        if (infoWindow != null) {
            infoWindow.close();
        }
        // infoWindow = new InfoWindow(getMap());
        infoWindow.setContent("Completed");
        infoWindow.open(getMap(), marker);
    }

    public void toggleFailPasseger(int i, LatLng latLng) {
        Marker markerFail = new Marker(getMap());
        markerFail.setPosition(latLng);

        passageMarkersStart.get(i).setVisible(false);
        passageMarkersEnd.get(i).setVisible(false);
        //  passageMarkersStart.set(i, null);
        //   passageMarkersEnd.set(i, null);

        Icon icon = new Icon();
        File file = MapUtils.getIconFail();
        icon.loadFromFile(file);
        markerFail.setIcon(icon);

        markerFail.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                InfoWindow infoWindow = new InfoWindow(getMap());
                infoWindow.setContent("#" + i + " Failed");
                infoWindow.open(getMap(), markerFail);
            }
        });
        passageMarkersFailed.add(markerFail);

        if (infoWindowForClickOnly) {
            return;
        }
        if (infoWindow != null) {
            infoWindow.close();
        }
        // infoWindow = new InfoWindow(getMap());
        infoWindow.setContent("Failed");
        infoWindow.open(getMap(), markerFail);
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

        if (!infoWindowForClickOnly) {
            closeAllInfoWindow();
        }
    }

    public void closeAllInfoWindowForClients() {
        closeAllInfoWindowForClients(false);
    }

    public void closeAllInfoWindowForClients(boolean onClick) {
        if (infoWindowForClickOnly && !onClick) {
            return;
        }
        if (infoWindowClientStart != null) {
            infoWindowClientStart.close();
        }
        if (infoWindowClientEnd != null) {
            infoWindowClientEnd.close();
        }
    }

    public void closeAllInfoWindow() {
        closeAllInfoWindow(false);
    }

    public void closeAllInfoWindow(boolean onClick) {
        if (infoWindowForClickOnly && !onClick) {
            return;
        }
        if (infoWindow != null) {
            infoWindow.close();
        }
        if (infoWindowTaxi != null) {
            infoWindowTaxi.close();
        }
        closeAllInfoWindowForClients(onClick);
    }

    public void showMsgFinish(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showPoint(Trip trip, int i) {
        Marker marker = new Marker(getMap());
        marker.setPosition(trip.getLatLngStart());
        marker.setTitle("#" + i);
        Icon icon = new Icon();
        File file = MapUtils.getIconSmallEnd();
        icon.loadFromFile(file);
        marker.setIcon(icon);
    }

    public void showPoint(LatLng depo, String msg) {
        if (marker != null) {
            marker.remove();
        }
        marker = new Marker(getMap());
        marker.setPosition(depo);
        marker.setTitle(msg);
        Icon icon = new Icon();
        File file = MapUtils.getIconFu();
        icon.loadFromFile(file);
        marker.setIcon(icon);

        if (infoWindow != null) {
            //   infoWindow.close();
        }
        // infoWindow = new InfoWindow(getMap());
        // infoWindow.setContent(msg);
        //  infoWindow.open(getMap(), marker);

        marker.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(MouseEvent mouseEvent) {
                if (infoWindow != null) {
                    infoWindow.close();
                }
                //  infoWindow = new InfoWindow(getMap());
                infoWindow.setContent(msg);
                infoWindow.open(getMap(), marker);
            }
        });
    }
}