package av.VRP.rt.substance;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Artem on 19.04.2016.
 */
public class Trips {
    private List<Trip> trips;
    private String title;

    public Trips() {
        trips = Collections.synchronizedList(new ArrayList<Trip>());
    }

    public void setTitle(String title) {
        if (title.contains(Constant.GREEN)) {
            title = Constant.GREEN;
        }
        if (title.contains(Constant.YELLOW)) {
            title = Constant.YELLOW;
        }
        if (title.contains(Constant.UBER)) {
            title = Constant.UBER;
        }
    }

    public void add(Trip t) {
        trips.add(t);
    }

    public int size() {
        return trips.size();
    }

    public void clear() {
        trips.clear();
        title = "";
    }

    public long getCountTripsForDay(DateTime date) {
        //переделать для отсортированного, без перебора всего массива //FIXME
        long count = 0l;

        for (Trip point : trips) {
            if (point.checkSameDay(date)) {
                count++;
            }
        }

        return count;
    }

    public List<String> getActiveDaysStr() {
        List<String> result = new ArrayList<>();

        for (DateTime date : getActiveDays()) {
            result.add(String.valueOf(date.toLocalDate().getDayOfMonth()));
        }

        return result;
    }

    public List<DateTime> getActiveDays() {
        List<DateTime> result = new ArrayList<>();

        Trip dateEnd = getDateEnd();
        int days = getDaysBetweenDateSE(getDateStart(), getDateEnd());

        while (days >= 0) {
            result.add(dateEnd.getDateTime().minusDays(days--));
        }

        return result;
    }

    public int getDaysBetweenDateSE(Trip dateStart, Trip dateEnd) {
        return Days.daysBetween(
                dateStart.getDateTime().toLocalDate(), dateEnd.getDateTime().toLocalDate())
                .getDays();
    }

    public Trip getDateStart() {
        return trips.get(0);//FIXME if sorted
    }

    public Trip getDateEnd() {
        return trips.get(size() - 1);//FIXME if sorted
    }

    public List<Long> getCountTripsForEveryDay() {
        //переделать для отсортированного, без перебора всего массива //FIXME
        List<Long> result = new ArrayList<>();

        for (DateTime date : getActiveDays()) {
            //  System.err.println(date);
            //  System.err.println(getCountTripsForDay(date));
            //  System.err.println("");
            result.add(getCountTripsForDay(date));
        }

        return result;
    }

    public void sortWithDate() {
        Log.d("sorting");
        Collections.sort(trips);
        Log.d("sorted");
    }

    public void removeNull() {
        trips.removeAll(Collections.singleton(null));
    }

    public String getMonthYear() {
        return getDateStart().getStartPoint()._dateTime.toString("MMM YYYY");
    }
}