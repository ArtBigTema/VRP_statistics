package av.VRP.rt.substance;

import av.VRP.rt.Utils.Log;
import av.VRP.rt.Utils.Utils;

/**
 * Created by Artem on 20.04.2016.
 */
public class Trip {
    private PointWithTime startPoint;
    private PointWithTime endPoint;

    public Trip(PointWithTime startPoint, PointWithTime endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    //size == 100
    //[1,2, 8, 9, 10, 11] not null
    // 2,2014-01-01 00:00:07,2014-01-01 00:08:28,N,1,-73.9169921875,40.771003723144531,-73.8885498046875,40.745452880859375,1,2.52,10,0.5,0.5,0,0,,11,2,,,
    public static Trip construct(String s) {
        if (s == null || s.isEmpty()) {
            Log.e("null pointer");
            return null;
        }

        if (s.length() > 100) {//FIXME if split size>10 or else
            String[] elements = Utils.strToArray(s, ",");

            if (elements.length > 10) {//FIXME const
                return new Trip(
                        new PointWithTime(elements[1], elements[8], elements[9]),//FIXME const
                        new PointWithTime(elements[2], elements[10], elements[11]));//FIXME const
            }
        }

        if (s.length() > 20) {//FIXME
            String[] elements = Utils.strToArray(s, ",");

            if (elements.length > 3) {//FIXME const
                return new Trip(
                        PointWithTime.construct(s),//FIXME const
                        null);
            }
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

    @Override
    public String toString() {
        return "Trip{" +
                "startPoint=" + startPoint.toString() +
                ", endPoint=" + endPoint.toString() +
                '}';
    }
}