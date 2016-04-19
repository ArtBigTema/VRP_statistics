package av.VRP.rt.substance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artem on 19.04.2016.
 */
public class Trips {
    private List<PointWithTime> trips;

    public Trips() {
        trips = new ArrayList<>();
    }

    public void add(PointWithTime t) {
        trips.add(t);
    }

    public int size() {
        return trips.size();
    }



    public long getCountTripsForDay() {
        long count = 0l;
        for (PointWithTime point : trips) {
            if (point.checkSameDay("7/1/2014 0:0:0")) {
                count++;
            }
        }
        return count;
    }
}
