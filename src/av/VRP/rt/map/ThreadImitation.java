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
    private MapExample map;

    private Timer timer;
    private int period = 1000, oldPeriod = 1000;
    private long max;
    private int iter = 0;
    private int tripSize = 0;
    private DateTime now;

    private int countCompleteClient, countWaitingClient, countFailedOrder;

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
        max = Math.abs(now.getMillis() - now.plusMonths(1).getMillis()) / 60000;
        tripSize = trips.getSubAll().size();
        //minutes in month

        vehicles.initDepo(cluster);
        showPassegerOnMap();
        showVehicleOnMap();
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

                    moveVehicles();
                    map.clearFailedOrders(iter++);

                    Log.p("-------------------");
                    Log.p("inc time");
                    now = now.plusMinutes(1);
                    Log.p("now: " + now.toString());

                    findNearestCar(trips.get(now));//get same time

                    if (max-- < 0) {
                        stopTimer();
                        map.showMsgFinish(Constant.MSG_IMITATION);
                    }
                }).run();

                if (oldPeriod != period) {
                    startTimer();
                }
            }
        }, 10, period);
    }

    private void moveVehicles() {
        new Thread(() -> {
            for (int i = 0; i < vehicles.getVehicles().size(); i++) {
                Vehicle vehicle = vehicles.get(i);

                if (vehicle.isBusy()) {
                    Log.p("-------------------");
                    Log.p("start moveVehicles");
                    boolean moving = vehicle.move();

                    if (moving) {
                        map.moveVehicle(i, vehicle.getCurrPoint());
                    } else {
                        if (vehicle.containEndPoint()) {
                            map.removeClientMarkers(vehicle.getIndexOfTrip());
                            map.showMessVehicleComplete(i);
                            vehicle.resetTrip();
                            vehicles.decBusy();
                        }
                    }
                    Log.p("end moveVehicles");
                }
                vehicle.incTime();
            }
            checkImitation();
        }).run();
    }

    private synchronized void findNearestCar(List<Integer> list) {
        if (list.size() < 1) {
            Log.p("Client not found");
            return;
        }

        for (Integer i : list) {
            map.showPasseger(i);

            int index = vehicles.findNearestCar(trips.get(i).getStartPoint());

            Trip trip = trips.get(i);

            if (trip.isWaiting()) {
                countWaitingClient++;
            }
            trip.incTime();//inc for all

            if (vehicles.timeMoreDistance(index, trip)) {
                if (trip.isFailed()) {
                    countWaitingClient--;
                    countFailedOrder++;
                    map.toggleFailPasseger(i, trips.get(i).getLatLngStart());
                } else {
                    map.togglePasseger(i, index, false);
                }
                continue;// if time move to client > waitng
            }

            if (index >= 0) {
                vehicles.transfer(index, trip, i);
                trip.completed();

                map.toggleVehicle(index);
                map.togglePasseger(i, index, true);

                countWaitingClient--;
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
        }
    }

    public void stopTimer() {
        Log.e("stopTimer");
        timer.cancel();
    }

    public void showClusterZoom(int v) {
        new Thread(() -> {
            cluster.constructClusters(trips, v);
            map.showCluster(cluster);
        }).run();
    }

    private void showVehicleOnMap() {
        for (Vehicle vehicle : vehicles.getVehicles()) {
            map.showVehicle(vehicle);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Log.e(e);
                e.printStackTrace();
            }
        }
        // map.showVehicles(vehicles);
    }

    private void showPassegerOnMap() {
        map.showAllPoints(trips, false);
    }

    public void setDelay(int period) {
        this.period = period * 1;
    }

    public void setMessageListener(MessageListener listener) {
        messageListener = listener;
    }

    public String getMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Всего: ");
        message.append(tripSize);
        message.append("   ");
        message.append("Ждут: ");
        message.append(countWaitingClient);
        message.append("   ");
        message.append("Успешно: ");
        message.append(countCompleteClient);
        message.append("   ");
        message.append("Неудачно: ");
        message.append(countFailedOrder);
        message.append("   ");
        message.append("Now: ");
        message.append(Constant.FMT.print(now.getMillis()));
        message.append("   ");
        message.append("Занято: ");
        message.append(vehicles.getCountBusy());
        message.append("   ");
        message.append("Свободно: ");
        message.append(vehicles.getCountFree());
        message.append("   ");
        message.append("Итерация: ");
        message.append(iter);
        message.append("   ");

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