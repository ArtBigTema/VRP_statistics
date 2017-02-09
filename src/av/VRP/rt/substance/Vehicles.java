package av.VRP.rt.substance;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.map.MapUtils;
import com.teamdev.jxmaps.LatLng;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artem on 09.02.2017.
 */
public class Vehicles {
    private List<Vehicle> vehicles;
    private DateTime initDateTime;

    public Vehicles() {
        vehicles = new ArrayList<>();

        for (int i = 0; i < Constant.VEHICLES; i++) {
            vehicles.add(new Vehicle());
        }
    }

    public void initDepo(List<LatLng> list) {
        for (int i = 0; i < vehicles.size(); i++) {
            vehicles.get(i).setCurrPoint(list.get(i));
            vehicles.get(i).setFileIcon("vi/" + (i + 1) + ".png");
            vehicles.get(i).initTime(initDateTime);
        }
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

    public int findNearestCar(Point point) {
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

    public void transfer(int index, Trip trip) {
        vehicles.get(index).setTrip(trip);
    }

    public Vehicle get(int index) {
        return vehicles.get(index);
    }
}