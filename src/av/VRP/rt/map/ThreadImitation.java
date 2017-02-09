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
    private int DELAY = 10000, START = 1000;
    private DateTime now;

    private Trip trip;

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
        now = vehicles.getInitDateTime();

        showVehicleOnMap();
        showPassegerOnMap();

        startTimer();
    }

    int max = 20;

    int k = 0;

    public void startTimer() {
        //  showNearestTime();
        Log.e("startTimer");

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                new Thread(() -> {

                    map.show(trips.get(k).getLatLngStart(), k);
                    k++;

                 /*   Log.p("-------------------");
                    Log.p("inc time");
                    now = now.plusMinutes(1);
                    Log.p("now: " + now.toString());

                    findNearestCar(trips.get(now));//get same time
*/
                    if (max-- < 0) {
                        stopTimer();
                    }
                }).run();
            }
        }, DELAY, START);
    }

    private void findNearestCar(List<Integer> trip) {
        if (trip.size() < 1) {
            Log.p("Client not found");
        }
        for (Integer i : trip) {
            map.showPasseger(i);

            int index = vehicles.findNearestCar(trips.get(i).getStartPoint());

            if (index < 0) {
                Log.p("Vehicle not found for passage: " + trips.get(i));
                trips.get(i).incTime();
                map.togglePasseger(false, i, vehicles.get(index).getCurrPoint().toLatLng());
            } else {
                vehicles.transfer(index);
                trips.get(i).completed();

                Log.e("start");
                Log.p("found nearest vehicle: " + index);
                Log.e(vehicles.get(index).getCurrPoint(), index);
                map.toggleVehicle(index);

                Log.e("----------------");
                Log.e(trips.get(i).getStartPoint(), i);
                map.togglePasseger(true, i, trips.get(i).getStartPoint().toLatLng());
                Log.e("end");
            }
        }
    }

    public void stopTimer() {
        Log.e("stopTimer");
        timer.cancel();
    }

    private void showPassegerOnMap() {
        map.showAllPoints(trips, false);
    }

    private void showNearestTime() {
        map.showPoints(trips.getSubAll().subList(0, 5));
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
}