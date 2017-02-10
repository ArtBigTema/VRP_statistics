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

    private Trips trips;
    private Vehicles vehicles;
    private MapExample map;

    private Timer timer;
    private int DELAY = 10, period = 100, oldPeriod = 100;
    private int max = 200;
    private int iter = 0;
    private DateTime now;

    private int countCompleteClient, countWaitingClient;

    public ThreadImitation(MapExample mapExample) {
        map = mapExample;
        timer = new Timer();
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
        max = Constant.ITER;
        now = vehicles.getInitDateTime();

        showPassegerOnMap();
        showVehicleOnMap();

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
        }, DELAY, period);
    }

    private void moveVehicles() {
        Log.p("-------------------");
        Log.p("start moveVehicles");
        new Thread(() -> {
            for (int i = 0; i < vehicles.getVehicles().size(); i++) {
                Vehicle vehicle = vehicles.get(i);

                if (vehicle.isBusy()) {
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
                }
            }
            Log.p("end moveVehicles");
        }).run();
    }

    private void findNearestCar(List<Integer> list) {
        if (list.size() < 1) {
            Log.p("Client not found");
        }
        countWaitingClient = 0;

        for (Integer i : list) {
            map.showPasseger(i);

            int index = vehicles.findNearestCar(trips.get(i).getStartPoint());

            Trip trip = trips.get(i);
            if (index < 0) {
                trip.incTime();

                if (trip.isFailed()) {
                    map.toggleFailPasseger(i, trips.get(i).getLatLngStart());
                } else {
                    map.togglePasseger(i, index);
                }

                countWaitingClient++;
            } else {
                vehicles.transfer(index, trip, i);
                trip.completed();

                map.toggleVehicle(index);
                map.togglePasseger(i, index);
                countCompleteClient++;
            }
        }
    }

    public void stopTimer() {
        Log.e("stopTimer");
        timer.cancel();
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
        this.period = period * 10;
    }

    public void setMessageListener(MessageListener listener) {
        messageListener = listener;
    }

    public String getMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Всего заказов: ");
        message.append(trips.getSubAll().size());
        message.append("   ");
        message.append("Выполнено заказов: ");
        message.append(countCompleteClient);
        message.append("   ");
        message.append("Ожидающие заказы: ");
        message.append(countWaitingClient);
        message.append("   ");
        message.append("Now: ");
        message.append(Constant.FMT.print(now.getMillis()));
        message.append("   ");
        message.append("Занятый транспорт: ");
        message.append(vehicles.getCountBusy());
        message.append("   ");
        message.append("Свободный транспорт: ");
        message.append(vehicles.getCountFree());
        message.append("   ");

        return message.toString();
    }
}