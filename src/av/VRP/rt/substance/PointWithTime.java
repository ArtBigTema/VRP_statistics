package av.VRP.rt.substance;

import av.VRP.rt.Utils.Log;
import av.VRP.rt.Utils.Utils;
import com.teamdev.jxmaps.LatLng;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Artem on 09.04.2016.
 */
public class PointWithTime extends Point implements Comparable<PointWithTime> {//FIXME remove public
    //"12/31/2014 0:03:00",40.7366,-73.9906,"B02512"
    // 2,2014-01-01 00:00:07,2014-01-01 00:08:28,N,1,-73.9169921875,40.771003723144531,-73.8885498046875,40.745452880859375,1,2.52,10,0.5,0.5,0,0,,11,2,,,

    private DateTime _dateTime;
    private int waiting;

    private DateTimeFormatter fmt_1 = DateTimeFormat.forPattern("M/dd/yyyy HH:mm:ss");

    private DateTimeFormatter fmt_2 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static String fmtLong = "dd.MM.yyyy";//fixme
    public static String fmtShort = "MM.yyyy";//fixme

    public static String fmtTime = "HH";//fixme

    public PointWithTime(String la, String ln, String dt) {
        super(la, ln);
        setDateTime(dt);
    }

    public PointWithTime(Point currPoint) {
        super(currPoint.getLat(), currPoint.getLng());
        _dateTime = DateTime.now();
    }

    public PointWithTime(LatLng currPoint) {
        super(currPoint.getLat(), currPoint.getLng());
        _dateTime = DateTime.now();
    }

    public static PointWithTime construct(String s) {
        if (s != null && !s.isEmpty()) {
            String[] elements = Utils.strToArray(s, ",");
            //FIXME if listSize < 3
            if (elements.length > 2) {
                return new PointWithTime(elements[1], elements[2], elements[0]);
            }
        }
        Log.e("null pointer");
        return null;
    }

    public void setDateTime(DateTime ms) {
        if (ms == null) {
            _dateTime = DateTime.now();
        } else {
            _dateTime = new DateTime(ms.getMillis());
        }
    }

    public void setDateTime(String s) {
        if (s.contains("/")) {
            _dateTime = fmt_1.parseDateTime(s).withSecondOfMinute(0);
        } else {//if '-'
            _dateTime = fmt_2.parseDateTime(s).withSecondOfMinute(0);
        }
    }

    public DateTime getDateTime() {
        return _dateTime;
    }

    public String[] toTableVector() {
        String[] result = new String[]{
                _dateTime.toString(), getLat().toString(), getLng().toString()
        };
        return result;
    }

    public String getTimeStr() {
        return _dateTime.toString(fmtTime);
    }

    public String getDateStr() {
        return _dateTime.toString(fmtLong);
    }

    @Override
    public String toString() {
        return "PointWithTime{" + super.toString() +
                "dateTime='" + _dateTime.toString() + '\'' +//FIXME if null
                '}';
    }

    public boolean checkSameDay(DateTime date) {
        return date.toLocalDate().isEqual(_dateTime.toLocalDate());
    }

    public boolean checkSameDay(String date) {
        return checkSameDay(fmt_1.parseDateTime(date));
    }

    @Override
    public int compareTo(PointWithTime o) {
        return DateTimeComparator.getDateOnlyInstance().compare(this._dateTime, o._dateTime);//FIXME if null
    }

    public void incWaiting() {
        waiting++;
    }

    public void incTime() {
        _dateTime = _dateTime.plusMinutes(waiting);
    }

    public DateTime getTimeForIm() {
        return _dateTime.plusMinutes(waiting);
    }
}