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

    public Vehicles() {
        vehicles = new ArrayList<>();

        for (int i = 0; i < Constant.VEHICLES; i++) {
            vehicles.add(new Vehicle(i));
        }
    }

    public void initDepo(Cluster cluster, int k) {
        double part;
        int csize = cluster.getTripSize();
        int vsize = vehicles.size();
        int countVehicles;
        PointWithMessage point;

        while (k < Constant.VEHICLES) {
            for (int i = 0; i < cluster.size(); i++) {
                point = cluster.get(i);
                part = vsize * point.getClustD() / csize;

                countVehicles = (int) Math.round(part);
                while (countVehicles > 0) {
                    if (k >= Constant.VEHICLES) {
                        Log.e("depo initialized");
                        return;
                    }
                    vehicles.get(k).setCurrPoint(point.getLatLng());
                    vehicles.get(k).setFileIcon("vi/" + k + ".png");
                    vehicles.get(k).initTime(initDateTime);
                    k++;
                    countVehicles--;
                }
            }
        }

        Log.e("depo initialized");
    }

    public DateTime getInitDateTime() {
        return initDateTime;
    }

    public void setInitDateTime(PointWithTime pointWithTime) {
        this.initDateTime = pointWithTime.getDateTime().minusMinutes(10);
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
        countBusy--;
    }

    public int getCountBusy() {
        return countBusy;
    }

    public int getCountFree() {
        return vehicles.size() - countBusy;
    }

    public boolean timeMoreDistance(int index, Trip trip) {
        return index < 0 || vehicles.get(index).timeMoreDistance(trip.getStartPoint());
    }
}