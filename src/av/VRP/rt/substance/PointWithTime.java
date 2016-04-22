package av.VRP.rt.substance;

import av.VRP.rt.Utils.Log;
import av.VRP.rt.Utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Artem on 09.04.2016.
 */
public class PointWithTime extends Point implements Comparable<PointWithTime> {//FIXME remove public
    //"12/31/2014 0:03:00",40.7366,-73.9906,"B02512"
    // 2,2014-01-01 00:00:07,2014-01-01 00:08:28,N,1,-73.9169921875,40.771003723144531,-73.8885498046875,40.745452880859375,1,2.52,10,0.5,0.5,0,0,,11,2,,,

    private DateTime _dateTime;
    private Date _date;//FIXME rename

    private DateTimeFormatter fmt_1 = DateTimeFormat.forPattern("M/dd/yyyy HH:mm:ss");
    private SimpleDateFormat sdf_1 = new SimpleDateFormat("M/dd/yyyy hh:mm:ss");

    private SimpleDateFormat sdf_2 = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    private DateTimeFormatter fmt_2 = DateTimeFormat.forPattern("yyyy-mm-dd HH:mm:ss");

    private SimpleDateFormat sdf_0 = new SimpleDateFormat("dd.mm.yyyy hh:mm:ss");
    private DateTimeFormatter fmt_0 = DateTimeFormat.forPattern("dd.mm.yyyy HH:mm:ss");

    private String fmt = "DD.MM.YYYY";

    public PointWithTime(String la, String ln, String dt) {
        super(la, ln);
        setDateTime(dt);
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

    public void setDateTime(String s) {
        try {
            if (s.contains("/")) {
                _date = sdf_1.parse(s);
                _dateTime = fmt_1.parseDateTime(s);
            } else {//if '-'
                _date = sdf_2.parse(s);
                _dateTime = fmt_2.parseDateTime(s);
            }
        } catch (ParseException e) {
            e.printStackTrace();//FIXME
            Log.e(e.getMessage());
        }
    }

    public DateTime getDateTime() {
        return _dateTime;
    }

    public String[] toTableVector() {
        String[] result = new String[]{
                fmt_0.print(_dateTime), getLat().toString(), getLat().toString()
        };
        return result;
    }

    public String getDateStr() {
        return _dateTime.toString(fmt);
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
}