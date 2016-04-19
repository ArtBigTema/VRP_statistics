package av.VRP.rt;

import av.VRP.rt.parser.ThreadParser;
import av.VRP.rt.generator.VRPStaticData;
import av.VRP.rt.listener.VRPgeneratorListener;
import av.VRP.rt.substance.PointWithTime;
import av.VRP.rt.substance.Trips;

import java.util.List;


/**
 * Created by Artem on 09.04.2016.
 */
public class Main implements VRPgeneratorListener<PointWithTime> {
    public MainFrame frame;//FIXME remove public

    public StringBuilder sb;
    public int n = 0;

    private volatile Trips trips;

    public Main() {
        frame = new MainFrame();
        sb = new StringBuilder();
        trips = new Trips();

        VRPStaticData data = new VRPStaticData();
        data.setListener(this);

        ThreadParser parser1 = new ThreadParser(1);
        parser1.setListener(this);
        ThreadParser parser2 = new ThreadParser(2);
        parser2.setListener(this);
        ThreadParser parser3 = new ThreadParser(3);
        parser3.setListener(this);
        ThreadParser parser4 = new ThreadParser(4);
        parser4.setListener(this);

        frame.showData(System.currentTimeMillis());

        parser1.start();
        parser2.start();
        parser3.start();
        parser4.start();
    }

    public static void main(String[] args) {
        new Main();

      /*  ListFrame lf = new ListFrame();
        lf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {//FIXME
                new Main();
            }
        });*/

    }

    @Override
    public void generated(PointWithTime t) {
        if (t != null) {
            trips.add(t);
        }
        //    frame.showData(t.toString() + "\n");
    }

    @Override
    public void started() {

    }

    @Override
    public void stoped(int count) {
        n++;
        System.err.println("stopped " + count);
        if (n > 3) {
            // frame.showData(sb.toString());
            //   frame.showData(trips.size());
            //   frame.showData(System.currentTimeMillis());

            // frame.setTable(Utils.listToTable(trips));
            System.err.println("stopped " + count);
            frame.showData(System.currentTimeMillis());

            //  trips.removeNull();
            trips.sortWithDate();
            checkData();
        }
    }

    private void checkData() {
        List<String> dates = trips.getActiveDaysStr();
        List<Long> counts = trips.getCountTripsForEveryDay();
        String month = trips.getMonthYear();

        frame.showGraph(
                dates.toArray(new String[dates.size()]), counts.toArray(new Long[counts.size()]), month);
    }

    @Override
    public void show(String[] row) {
        frame.addRow(row);
    }

    @Override
    public void show(int n, String row) {
        //  sb.append(System.currentTimeMillis() + "-" + n + "." + row);

        frame.showData(n + "." + row);
    }
}