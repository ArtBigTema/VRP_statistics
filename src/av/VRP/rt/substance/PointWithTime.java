package av.VRP.rt.substance;

import av.VRP.rt.Utils.Utils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Artem on 09.04.2016.
 */
public class PointWithTime extends Point {//FIXME remove public
    //"12/31/2014 0:03:00",40.7366,-73.9906,"B02512"
    public String dateTime = "";
    public String date = "";
    public String time = "";

    public DateTime _dateTime;
    public Date _date;//FIXME rename
    public DateTimeFormatter fmt = DateTimeFormat.forPattern("M/dd/yyyy HH:mm:ss");
    public SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy hh:mm:ss");

    public PointWithTime(double la, double ln) {
        super(la, ln);
    }

    public PointWithTime(double la, double ln, String dt) {
        super(la, ln);
        setDateTime(dt);
    }

    public PointWithTime(String la, String ln, String dt) {
        super(la, ln);
        setDateTime(dt);
    }

    public static PointWithTime construct(String s) {
        if (s != null && !s.isEmpty()) {
            String[] elements = Utils.strToArray(s, ",");
            //FIXME if size < 3
            if (elements.length > 2) {
                return new PointWithTime(elements[1], elements[2], elements[0]);
            }
        }
        return null;
    }

    public void setDateTime(String s) {
        dateTime = s;
        date = s.split("\\s+")[0];
        time = s.split("\\s+")[1];


        try {
            _date = sdf.parse(s);
            _dateTime = fmt.parseDateTime(s);
        } catch (ParseException e) {
            e.printStackTrace();//FIXME
        }
    }

    public Date getDate() {
        return _date;
    }

    public DateTime getDateTime() {
        return _dateTime;
    }

    @Override
    public String toString() {
        return "PointWithTime{" + super.toString() +
                "dateTime='" + dateTime + '\'' +
                '}';
    }

    public boolean checkSameDay(DateTime date) {
        return date.toLocalDate().isEqual(_dateTime.toLocalDate());
    }

    public boolean checkSameDay(String date) {
        return checkSameDay(fmt.parseDateTime(date));
    }
}