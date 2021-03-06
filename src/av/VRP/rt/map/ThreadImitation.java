package av.VRP.rt.map;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.listener.MessageListener;
import av.VRP.rt.substance.Trip;
import av.VRP.rt.substance.Trips;
import av.VRP.rt.substance.Vehicle;
import av.VRP.rt.substance.Vehicles;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Artem on 09.02.2017.
 */
public class ThreadImitation extends Thread implements Runnable {
    private MessageListener messageListener;

    private Cluster cluster;
    private Trips trips;
    private Vehicles vehicles;
    private MapExample map;  boolean t = false;

    private Timer timer;
    private int period = 1000, oldPeriod = 1000;
    private long max;
    private int iter = 0;
    private int tripSize = 0;
    private DateTime now;

    // int countComing, countDepo;
    private int countCompleteClient, countFailedOrder;

    public ThreadImitation(MapExample mapExample) {
        map = mapExample;
        timer = new Timer();
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public void setTrips(Trips trips) {
        this.trips = trips;
    }

    public void setVehicles(Vehicles vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        now = vehicles.getInitDateTime();
        max = (now.plusMonths(1).getMillis() - now.getMillis()) / 60000;
        tripSize = trips.getSubAll().size();
        //minutes in month

        showPassegerOnMap();
        showVehicleOnMap();
        showClusterOnMap();
        // map.showCluster(cluster);

        startTimer();
    }

    public void startTimer() {
        oldPeriod = period;
        timer.cancel();
        timer = new Timer();

        Log.p();
        Log.e("startTimer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Thread(() -> {
                    messageListener.showMessage(getMessage());

                    //checkImitation();
                    moveVehicles();
                    shuffleCluster();

                    map.clearFailedOrders(iter++);

                    now = now.plusMinutes(1);

                    findNearestCar(trips.get(now));//get same time

                    if (oldPeriod != period) {
                        startTimer();
                    }
                }).run();
            }
        }, 10, period);
    }

    private void printTime() {
        Log.p("-------------------");
        Log.p("inc time");
        Log.p("now: " + now.toString());
    }

    private void shuffleCluster() {
        if (iter % 20 != 0) {
            return;
        }
        new Thread(() -> {
            cluster.shuffle();
        }).run();
    }

    private void moveVehicles() {
        new Thread(() -> {
            for (int i = 0; i < vehicles.getVehicles().size(); i++) {
                Vehicle vehicle = vehicles.get(i);

                if (vehicle.isBusy()) {
                    Log.p("-------------------");
                    Log.p("start moveVehicles for client");
                    boolean moving = vehicle.move();

                    if (moving) {
                        map.moveVehicle(i, vehicle.getCurrPoint());
                    } else {
                        if (vehicle.containEndPoint()) {
                            Log.p(" Taxi #" + i + " перевез клиента #" + vehicle.getIndexOfTrip());
                            map.removeClientMarkers(vehicle.getIndexOfTrip());

                            vehicle.resetTrip();
                            int indexCluster = cluster.getNearestCluster(vehicle);
                            if (indexCluster < 0) {
                                continue;
                            }

                            vehicle.resetDepo(cluster.get(indexCluster), indexCluster);

                            cluster.incComing(vehicle);

                            map.toggleCluster(indexCluster, i, cluster.get(indexCluster).getComingVehicle());
                            map.showMessVehicleComplete(i, indexCluster);

                            vehicles.decBusy();
                        } else {
                            map.togglePassegerTransfer(vehicle.getIndexOfTrip(), i);
                        }
                    }
                    Log.p("end moveVehicles for client");
                } else {
                    if (vehicle.goToDepo()) {
                        boolean moving = vehicle.moveToDepo();//to nearest depo

                        if (moving) {
                            //   map.showPoint(vehicle.getDepo().toLatLng(), "Car " + i + " to ");
                            //   map.showPoint(vehicle.getCurrPoint().toLatLng(), "Car " + i + " to ");

                            Log.p("-------------------");
                            Log.p("start moveVehicles for depo");
                            map.moveVehicle(i, vehicle.getCurrPoint());
                            int k = vehicle.getDepoIndex();
                            if (iter % 10 == 0) {
                                map.toggleCluster(k, i, cluster.get(k).getComingVehicle());
                            }
                            Log.p("end moveVehicles for depo");
                        } else {//in depo
                            Log.p(" Taxi #" + i + " arrived");
                            vehicle.resetGoDepo();

                            cluster.decComing(vehicle);
                            cluster.incClusterSize(vehicle);
                            //  map.showPoint(vehicle.getDepo().toLatLng(), "Car " + i + " to arrived");

                            map.removeCluster(vehicle.getDepoIndex());
                        }
                    } else {
                        // отдых такси

                        if (t) {
                            map.showPoint(vehicle.getDepo().toLatLng(), "Ожидаю");
                        }
                    }
                }
                vehicle.incTime();
            }
        }).run();
    }

    private synchronized void findNearestCar(List<Integer> list) {
        if (list.size() < 1) {
            // Log.p("Client not found");
            return;
        }
        printTime();

        for (Integer i : list) {
            map.showPasseger(i);

            int index = vehicles.findNearestCar(trips.get(i).getStartPoint());

            Trip trip = trips.get(i);

            if (trip.isWaiting()) {
                trips.incWaiting();
            }
            trip.incTime();//inc for all

            if (vehicles.timeMoreDistance(index, trip)) {
                if (trip.isFailed()) {
                    trips.decWaiting();
                    countFailedOrder++;
                    map.toggleFailPasseger(i, trips.get(i).getLatLngStart());
                } else {
                    Log.p("Client #" + i + " wait " + trips.get(i).getTimeFailed());
                    map.togglePasseger(i, index, false);
                }
                continue;// if time move to client > waitng
            }

            if (index >= 0) {
                Log.p("Client #" + i + " transfered in  Taxi #" + index);

                map.removeCluster(vehicles.get(index).getDepoIndex());
                cluster.countTransfCl(+1);

                if (vehicles.get(index).goToDepo()) {
                    cluster.decComing(vehicles.get(index));
                } else {
                    cluster.decDepoSize(vehicles.get(index));
                }

                vehicles.transfer(index, trip, i);
                //fixme check
                trip.completed();

                map.toggleVehicle(index, i);
                map.togglePasseger(i, index, true);

                trips.decWaiting();
                countCompleteClient++;
            }
        }
    }

    private void checkImitation() {
        if ((countFailedOrder + countCompleteClient) >= tripSize) {
            if (vehicles.getCountBusy() == 0) {
                stopTimer();
                map.showMsgFinish(Constant.MSG_IMITATION);
            }
        } else {
            if (max-- < 0) {
                //   stopTimer();
                //  map.showMsgFinish(Constant.MSG_IMITATION);
            }
        }
    }

    public void stopTimer() {
        Log.e("stopTimer");
        timer.cancel();
        setDelay(1000);
    }

    public void showClusterZoom(int v) {
        new Thread(() -> {
            cluster.constructClusters(trips, v);
            map.showCluster(cluster);
        }).run();
    }

    private void showVehicleOnMap() {
        Log.p("start showAll vehicle");
        for (Vehicle vehicle : vehicles.getVehicles()) {
            map.showVehicle(vehicle);
        }
        // map.showVehicles(vehicles);
        Log.p("end showAll vehicle");
    }

    private void showClusterOnMap() {
        Log.p("start showAll cluster");
        map.showCluster(cluster, false);
        // map.showVehicles(vehicles);
        Log.p("end showAll cluster");
    }

    private void showPassegerOnMap() {
        Log.p("start showAllPoints");
        map.showAllPoints(trips, false);
        Log.p("end showAllPoints");
    }

    public void setDelay(int period) {
        map.setShowInfo(// invisible
                period < 6 || tripSize > Constant.MIDDLE_SIZE);

        if (period == 6) {
            period = 1000;
        }
        if (period == 5) {
            period = 500;
        }
        if (period == 4) {
            period = 100;
        }
        if (period == 3) {
            period = 50;
        }
        if (period < 3) {
            this.period = period * 10;
        } else {
            this.period = period;
        }
    }

    public void setMessageListener(MessageListener listener) {
        messageListener = listener;
    }

    public String getMessage() {
        // map.setShowInfo(trips.getCountWaiting() > 5);

        StringBuilder message = new StringBuilder();
        message.append("All: ");
        message.append(tripSize);
        message.append("  ");
        message.append("Wait: ");
        message.append(trips.getCountWaiting());
        message.append("  ");
        message.append("Succ: ");
        message.append(countCompleteClient);
        message.append("  ");
        message.append("Fail: ");
        message.append(countFailedOrder);
        message.append("  ");
        message.append("Now: ");
        message.append(Constant.FMT.print(now.getMillis()));
        message.append("  ");
        message.append("Busy: ");
        message.append(vehicles.getCountBusy());
        message.append("  ");
        message.append("Free: ");
        message.append(vehicles.getCountFree());
        message.append("  ");
        message.append("Iter: ");
        message.append(iter);
        message.append("  ");
        message.append("mWait: ");
        message.append(trips.getMaxWaiting());
        message.append("  ");
        message.append("mBusy: ");
        message.append(vehicles.getMaxBusy());

        return message.toString();
    }

    public void cancel() {
        // map.clearAll();
        if (timer != null) {
            timer.cancel();
        }
        Log.e("thread imitation stopped");
        Log.e("-------------------");
    }
}