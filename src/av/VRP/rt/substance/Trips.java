package av.VRP.rt.substance;

import av.VRP.rt.Utils.*;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;

import java.util.*;

/**
 * Created by Artem on 19.04.2016.
 */
public class Trips {
    private List<Trip> trips;

    private Map<String, Integer> mapTrips;//FIXME
    private List<String> titles;

    public Trips() {
        trips = Collections.synchronizedList(new ArrayList<Trip>());
        mapTrips = Collections.synchronizedMap(new TreeMap<String, Integer>());
        titles = new ArrayList<>();
    }

    public void addTitle(String title) {//не убирай цифру после имени, т.к. сортировка
        // String title = who.replace(Constant.FILE_FORMAT, "&");//System.lineSeparator()
        if (!titles.contains(title)) {
            titles.add(title);
            Log.p("add title " + title);
        }
    }

    public void add(String s) {
        Trip trip = Trip.construct(s);
        if (trip != null) {
            add(trip);
        }
    }

    public void add(String who, String line) {
        Trip trip = Trip.construct(line);
        if (trip != null) {
            add(who, trip);
        }
    }

    public void add(String who, Trip t) {
        String key = who.replace(Constant.FILE_FORMAT, "&") + t.getDateStr();
        addTitle(who.replace(Constant.FILE_FORMAT, "&") + t.getMonthYear());//FIXME move to ...

        if (mapTrips.get(key) != null) {
            mapTrips.put(key, mapTrips.get(key) + 1);
        } else {
            mapTrips.put(key, 1);
        }
    }

    public void add(Trip t) {
        trips.add(t);
    }

    public int listSize() {
        return trips.size();
    }

    public int mapSize() {
        return mapTrips.size();
    }

    public void clear() {
        trips.clear();
        titles.clear();
        mapTrips.clear();
    }

    public DateTime getDateFromStr(String title) {
        return new DateTime(
                DateTimeFormat.forPattern(PointWithTime.fmtShort)
                        .parseDateTime(title.split("&")[1]));
    }

    public String[] getActiveDaysStr() {
        List<List<String>> result = new ArrayList<>(titles.size());

        for (String title : titles) {
            List<String> subResult = new ArrayList<>();

            int i = 0;
            int lastDayOfMonth = getDateFromStr(title)
                    .dayOfMonth().getMaximumValue();//title

            while (++i <= lastDayOfMonth) {
                subResult.add(String.valueOf(i));
            }
            result.add(subResult);
        }

        return result.get(0).toArray(new String[result.get(0).size()]);//fIXME all
    }

    public Integer[] getCountTrips() {//FIXME method
        List<List<Integer>> result = new ArrayList<>(titles.size());

        for (String title : titles) {
            List<Integer> subResult = new ArrayList<>();
            List<String> keys = getKeysContainsTitle(title);
            String preKey = keys.get(0).split("&")[0] + "&";

            for (int i = 1; i <= getDateFromStr(title)
                    .dayOfMonth().getMaximumValue(); i++) {

                String postKey = (i < 10 ? "0" + i : i) + "."
                        + getDateFromStr(title).toString(PointWithTime.fmtShort);
                if (keys.contains(preKey + postKey)) {
                    subResult.add(mapTrips.get(preKey + postKey));
                } else {
                    subResult.add(0);
                }
            }
            result.add(subResult);
        }

        return result.get(0).toArray(new Integer[result.get(0).size()]);
    }

    public List<String> getKeysContainsTitle(String title) {
        List<String> result = new ArrayList<>();
        Set<String> keys = mapTrips.keySet();

        for (String key : keys) {
            if (key.contains(getDateFromStr(title).toString(PointWithTime.fmtShort))) {
                result.add(key);
            }
        }
        return result;
    }

    public void sortWithDate() {// add comparators
        Log.d("sorting");
        Collections.sort(trips);
        Log.d("sorted");
    }

    public void removeNull() {
        trips.removeAll(Collections.singleton(null));
    }

    public String getMonthYear() {
        return titles.get(0).split("&")[1];//FIXME
    }

    public String[][] toTable() {
        String[][] result = new String[listSize()][];
        int i = 0;
        for (Trip trip : trips) {
            result[i++] = trip.toTableVector();
        }
        return result;
    }
}