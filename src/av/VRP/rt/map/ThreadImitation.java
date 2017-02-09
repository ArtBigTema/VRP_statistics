package av.VRP.rt.map;

import av.VRP.rt.Utils.Log;
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
    private Trips trips;
    private Vehicles vehicles;
    private MapExample map;

    private Timer timer;
    private int DELAY = 1000, PERIOD = 100;
    private int max = 200;
    private DateTime now;

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
        max = trips.getSubAll().size();
        now = vehicles.getInitDateTime();

        showPassegerOnMap();
        showVehicleOnMap();

        startTimer();
    }

    private void startTimer() {
        Log.p();
        Log.e("startTimer");

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                new Thread(() -> {
                    moveVehicles();

                    Log.p("-------------------");
                    Log.p("inc time");
                    now = now.plusMinutes(1);
                    Log.p("now: " + now.toString());

                    findNearestCar(trips.get(now));//get same time

                    if (max-- < 0) {
                        stopTimer();
                    }
                }).run();
            }
        }, DELAY, PERIOD);
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
                        }
                    }
                }else{
                    if (vehicle.containEndPoint()) {
                        map.removeClientMarkers(vehicle.getIndexOfTrip());
                        map.showMessVehicleComplete(i);
                        vehicle.resetTrip();
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

        for (Integer i : list) {
            map.showPasseger(i);

            int index = vehicles.findNearestCar(trips.get(i).getStartPoint());

            Trip trip = trips.get(i);
            if (index < 0) {
                trip.incTime();
                map.togglePasseger(false, i, index);
            } else {
                vehicles.transfer(index, trip);
                trip.completed();

                map.toggleVehicle(index);
                map.togglePasseger(true, i, index);
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
}