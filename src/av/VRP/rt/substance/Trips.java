package av.VRP.rt.substance;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Artem on 19.04.2016.
 */
public class Trips {
    private List<PointWithTime> trips;

    public Trips() {
        trips = Collections.synchronizedList(new ArrayList<PointWithTime>());
    }

    public void add(PointWithTime t) {
        trips.add(t);
    }

    public int size() {
        return trips.size();
    }


    public long getCountTripsForDay(DateTime date) {
        long count = 0l;

        for (PointWithTime point : trips) {
            if (point.checkSameDay(date)) {
                count++;
            }
        }

        return count;
    }

    public List<Long> getCountTripsForEveryDay() {
        //переделать для отсортированного, без перебора всего массива //FIXME
        PointWithTime dateStart = trips.get(0);
        PointWithTime dateEnd = trips.get(size() - 1);

        int days = Days.daysBetween(
                dateStart.getDateTime().toLocalDate(), dateEnd.getDateTime().toLocalDate())
                .getDays();

        List<Long> counts = new ArrayList<>(days);

        while (days >= 0) {
            counts.add(getCountTripsForDay(dateEnd.getDateTime().minusDays(days--)));
        }

        return counts;
    }

    public void sortWithDate() {
        Collections.sort(trips);
    }

    public void removeNull() {
        trips.removeAll(Collections.singleton(null));
    }
}