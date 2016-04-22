package av.VRP.rt.substance;

import av.VRP.rt.Utils.Log;
import av.VRP.rt.Utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Artem on 20.04.2016.
 */
public class Trip implements Comparable<Trip> {
    private PointWithTime startPoint;
    private PointWithTime endPoint;

    public Trip(PointWithTime startPoint, PointWithTime endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public static Trip construct(String s) {
        if (s == null || s.isEmpty()) {
            Log.e("null pointer");
            return null;
        }

        String[] elements = Utils.strToArray(s, ",");
        if (s.length() > 100 || elements.length > 10) {//FIXME if split listSize>10 or else
            if (elements[7].length() < 3) {
                return new Trip(//yellow
                        new PointWithTime(elements[5], elements[6], elements[1]),//FIXME const
                        new PointWithTime(elements[9], elements[10], elements[2]));//FIXME const
            } else {
                return new Trip(//green
                        new PointWithTime(elements[5], elements[6], elements[1]),//FIXME const
                        new PointWithTime(elements[7], elements[8], elements[2]));//FIXME const
            }
        }

        if (s.length() > 20 || elements.length > 3) {//FIXME const
            return new Trip(
                    new PointWithTime(elements[2], elements[1], elements[0]),//FIXME const
                    null);
        }

        Log.e("null pointer");
        return null;
    }

    public PointWithTime getStartPoint() {
        return startPoint;
    }

    public PointWithTime getEndPoint() {
        return endPoint;
    }

    public DateTime getDateTime() {
        return getStartPoint().getDateTime();
    }

    public boolean checkSameDay(DateTime date) {
        return getStartPoint().checkSameDay(date);
    }

    public String[] toTableVector() {
        List<String> result = new ArrayList<>();
        result.addAll(Arrays.asList(startPoint.toTableVector()));

        if (endPoint != null) {
            result.addAll(Arrays.asList(endPoint.toTableVector()));
        } else {//FIXME if > 3
            result.addAll(Arrays.asList(new String[]{"", "", ""}));
        }
        return result.toArray(new String[result.size()]);
    }

    public String getDateStr() {
        return startPoint.getDateStr();
    }

    public String getMonthYear() {
        return getStartPoint().getDateTime().toString("MM.yyyy");//FIXME const
    }

    @Override
    public String toString() {
        return "Trip{" +
                "startPoint=" + startPoint.toString() +
                ", endPoint=" + endPoint.toString() +
                '}';
    }

    @Override
    public int compareTo(Trip o) {
        return DateTimeComparator.getDateOnlyInstance().compare(this.getStartPoint().getDateTime(), o.getStartPoint().getDateTime());//FIXME if null
    }
}