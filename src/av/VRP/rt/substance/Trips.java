package av.VRP.rt.substance;

import av.VRP.rt.Utils.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.*;

/**
 * Created by Artem on 19.04.2016.
 */
public class Trips {
    private List<Trip> trips;

    private Map<String, Integer> mapTripsForDay;
    private Map<String, Integer> mapTripsForHour;
    private List<String> titles;

    public Trips() {
        trips = Collections.synchronizedList(new ArrayList<Trip>());
        mapTripsForDay = Collections.synchronizedMap(new TreeMap<String, Integer>());
        mapTripsForHour = Collections.synchronizedMap(new TreeMap<String, Integer>());
        titles = Collections.synchronizedList(new ArrayList<String>());
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

        if (mapTripsForDay.get(key) != null) {
            mapTripsForDay.put(key, mapTripsForDay.get(key) + 1);
        } else {
            mapTripsForDay.put(key, 1);
        }

        key = who.replace(Constant.FILE_FORMAT, "&") + t.getTimeStr();

        if (mapTripsForHour.get(key) != null) {
            mapTripsForHour.put(key, mapTripsForHour.get(key) + 1);
        } else {
            mapTripsForHour.put(key, 1);
        }
    }

    public void add(Trip t) {
        trips.add(t);
    }

    public int listSize() {
        return trips.size();
    }

    public int mapSizeForDay() {
        return mapTripsForDay.size();
    }

    public int mapSizeForHour() {
        return mapTripsForHour.size();
    }

    public void clear() {
        trips.clear();
        titles.clear();
        mapTripsForDay.clear();
        mapTripsForHour.clear();
    }

    public DateTime getDateFromStr(String title) {
        return new DateTime(
                DateTimeFormat.forPattern(PointWithTime.fmtShort)
                        .parseDateTime(title.split("&")[1]));
    }

    public String[][] getActiveDaysStr() {
        String[][] result = new String[titles.size()][];

        for (String title : titles) {
            List<String> subResult = new ArrayList<>();

            int i = 0;
            //  int lastDayOfMonth = getDateFromStr(title)
            //           .dayOfMonth().getMaximumValue();
            int lastDayOfMonth = 31;

            while (++i <= lastDayOfMonth) {
                subResult.add(String.valueOf(i));
            }
            result[titles.indexOf(title)] = subResult.toArray(new String[subResult.size()]);
        }

        return result;
    }

    public String[][] getActiveHoursStr() {
        String[][] result = new String[titles.size()][];

        for (String title : titles) {
            List<String> subResult = new ArrayList<>();

            int i = 0;
            //  int lastDayOfMonth = getDateFromStr(title)
            //           .dayOfMonth().getMaximumValue();
            int lastHour = 24;

            while (i < lastHour) {
                subResult.add(String.valueOf(i++));
            }
            result[titles.indexOf(title)] = subResult.toArray(new String[subResult.size()]);
        }

        return result;
    }

    public Integer[][] getCountTripsForDay() {//FIXME method
        Integer[][] result = new Integer[titles.size()][];

        for (String title : titles) {
            if (title == null) {
                continue;
            }
            List<Integer> subResult = new ArrayList<>();
            List<String> keys = getKeysContainsTitle(title, mapTripsForDay, PointWithTime.fmtShort);
            String preKey = keys.get(0).split("&")[0] + "&";//FIXME to index

            int lastDayOfMonth = 31;
            //getDateFromStr(title)
            // .dayOfMonth().getMaximumValue()
            for (int i = 1; i <= lastDayOfMonth; i++) {

                String postKey = (i < 10 ? "0" + i : i) + "."//fixme pattern
                        + getDateFromStr(title).toString(PointWithTime.fmtShort);
                if (keys.contains(preKey + postKey)) {
                    subResult.add(mapTripsForDay.get(preKey + postKey));
                } else {
                    subResult.add(0);
                }
            }
            result[titles.indexOf(title)] = subResult.toArray(new Integer[subResult.size()]);
        }

        return result;
    }

    public List<String> getKeysContainsTitle(String title, Map map, String format) {
        List<String> result = new ArrayList<>();
        Set<String> keys = map.keySet();

        for (String key : keys) {
            if (key.contains(getDateFromStr(title).toString(format))) {
                result.add(key);
            }
        }
        return result;
    }

    public Integer[][] getCountTripsForHour() {//FIXME method
        Integer[][] result = new Integer[titles.size()][];

        for (String title : titles) {
            if (title == null) {
                continue;
            }
            List<Integer> subResult = new ArrayList<>();
            //     List<String> keys = getKeysContainsTitle(title, mapTripsForHour, PointWithTime.fmtTime);
            String preKey = title.split("&")[0] + "&";//FIXME to index

            int lastHour = 24;
            //getDateFromStr(title)
            // .dayOfMonth().getMaximumValue()
            for (int i = 0; i < lastHour; i++) {

                String postKey = (i < 10 ? "0" + i : i) + "";//fixme pattern
                //  + getDateFromStr(title).toString(PointWithTime.fmtTime);
                if (mapTripsForHour.containsKey(preKey + postKey)) {
                    subResult.add(mapTripsForHour.get(preKey + postKey));
                } else {
                    subResult.add(0);
                }
            }
            result[titles.indexOf(title)] = subResult.toArray(new Integer[subResult.size()]);
        }

        return result;
    }

    public void sortWithDate() {// add comparators
        Log.d("sorting");
        Collections.sort(trips);
        Log.d("sorted");
    }

    public void sortWithHour() {// add comparators
        Log.d("sorting");
        Collections.sort(trips);
        Log.d("sorted");
    }

    public void removeNull() {
        trips.removeAll(Collections.singleton(null));
    }

    public String[] getTitles() {
        return titles.toArray(new String[titles.size()]);
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