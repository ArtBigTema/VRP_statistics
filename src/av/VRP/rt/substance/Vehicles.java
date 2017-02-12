package av.VRP.rt.substance;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.map.Cluster;
import av.VRP.rt.map.MapUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artem on 09.02.2017.
 */
public class Vehicles {
    private List<Vehicle> vehicles;
    private DateTime initDateTime;

    private int countBusy;
    private int maxBusy;

    public Vehicles() {
        vehicles = new ArrayList<>();

        for (int i = 0; i < Constant.VEHICLES; i++) {
            vehicles.add(new Vehicle(i));
        }
    }

    public void initDepo(Cluster cluster) {
        double part = 1.1d;
        int csize = cluster.getTripSize();
        int vsize = vehicles.size();
        int k = 0;
        int countVehicles;
        PointWithMessage point;
        double max = Double.MIN_VALUE;

        while (k < Constant.VEHICLES && part > 0.5) {
            for (int i = 0; i < cluster.size(); i++) {
                point = cluster.get(i);
                part = vsize * point.getClustD() / csize;
                if (part > max) {
                    max = part;
                }

                countVehicles = (int) Math.round(part);
                while (countVehicles > 0) {
                    if (k >= Constant.VEHICLES) {
                        cluster.sortMap();
                        Log.e("depo initialized");
                        return;
                    }
                    Vehicle vehicle = vehicles.get(k);
                    cluster.initDepo(vehicle, i);
                    vehicle.setCurrPoint(point.getLatLng(), i);
                    vehicle.setFileIcon("vi/" + k + ".png");
                    vehicle.initTime(initDateTime);
                    k++;
                    countVehicles--;
                }
            }
            part = max;
        }
        int i = 0;
        while (k < Constant.VEHICLES) {
            point = cluster.get(i);
            Vehicle vehicle = vehicles.get(k);
            cluster.initDepo(vehicle, i);
            vehicle.setCurrPoint(point.getLatLng(), i);
            vehicle.setFileIcon("vi/" + k + ".png");
            vehicle.initTime(initDateTime);
            k++;
            if (i++ > cluster.size()) {
                i = 0;
            }
        }

        cluster.sortMap();

        Log.e("depo initialized");
    }

    public DateTime getInitDateTime() {
        return initDateTime;
    }

    public void setInitDateTime(PointWithTime pointWithTime) {
        this.initDateTime = new DateTime(pointWithTime.getDateTime().minusMinutes(10));
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public int findNearestCar(PointWithTime point) {
        double tmp, distance = Double.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);

            if (vehicle.isBusy()) {
                continue;
            }
            tmp = MapUtils.getDistance(point, vehicle.getCurrPoint());

            if (tmp < distance) {
                distance = tmp;
                index = i;
            }
        }

        return index;
    }

    public void transfer(int index, Trip trip, int tripIndex) {
        countBusy++;
        vehicles.get(index).setTrip(trip, tripIndex);
    }

    public Vehicle get(int index) {
        return vehicles.get(index);
    }

    public void incBusy() {
        countBusy++;
    }

    public void decBusy() {
        if (countBusy > maxBusy) {
            maxBusy = countBusy;
        }
        countBusy--;
    }

    public int getCountBusy() {
        return countBusy;
    }

    public int getMaxBusy() {
        return maxBusy;
    }

    public int getCountFree() {
        return vehicles.size() - countBusy;
    }

    public boolean timeMoreDistance(int index, Trip trip) {
        return index < 0 || vehicles.get(index).timeMoreDistance(trip.getStartPoint());
    }
}