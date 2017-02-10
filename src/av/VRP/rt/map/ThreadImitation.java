package av.VRP.rt.map;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.listener.MessageListener;
import av.VRP.rt.substance.Trip;
import av.VRP.rt.substance.Trips;
import av.VRP.rt.substance.Vehicle;
import av.VRP.rt.substance.Vehicles;
import org.joda.time.DateTime;

import java.util.*;

/**
 * Created by Artem on 09.02.2017.
 */
public class ThreadImitation extends Thread implements Runnable {
    private MessageListener messageListener;

    private Cluster cluster;
    private Trips trips;
    private Vehicles vehicles;
    private MapExample map;
    public Set<Integer> index;
    public Set<Integer> indexx;

    private Timer timer;
    private int period = 1000, oldPeriod = 1000;
    private long max;
    private int iter = 0;
    private DateTime now;

    private int countCompleteClient, countWaitingClient, countFailedOrder;

    public ThreadImitation(MapExample mapExample) {
        index = Collections.synchronizedSet(new HashSet<>());
        indexx = Collections.synchronizedSet(new HashSet<>());
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
        //minutes in month

        vehicles.initDepo(cluster);
        showPassegerOnMap();
        showVehicleOnMap();
        // map.showCluster(cluster);

        startTimer();
    }

    private void startTimer() {
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
            }
        }).run();
    }

    private synchronized void findNearestCar(List<Integer> list) {
        index.addAll(list);
        if (list.size() < 1) {
            Log.p("Client not found");
            return;
        }

        for (Integer i : list) {
            indexx.add(i);
            map.showPasseger(i);

            int index = vehicles.findNearestCar(trips.get(i).getStartPoint());

            Trip trip = trips.get(i);

            trip.incTime();//inc for all

            if (vehicles.timeMoreDistance(index, trip)) {
                if (trip.isFailed()) {
                    countWaitingClient -= trip.getTimeFailed();
                    countFailedOrder++;
                    map.toggleFailPasseger(i, trips.get(i).getLatLngStart());
                } else {
                    map.togglePasseger(i, index, false);
                    countWaitingClient++;
                }

                return;// if time move to client > waitng
            }

            if (index >= 0) {
                vehicles.transfer(index, trip, i);
                trip.completed();

                map.toggleVehicle(index);
                map.togglePasseger(i, index, true);
                countWaitingClient -= trip.getTimeFailed();
                countCompleteClient++;
            } else {
                countWaitingClient--;
                map.showMsgFinish("err");
            }
        }
    }

    public void stopTimer() {
        Log.e("stopTimer");
        timer.cancel();
        map.showMsgFinish(Constant.MSG_IMITATION);
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
        message.append(trips.getSubAll().size());
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

       // return message.toString();
        return index.toString();
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