package av.VRP.rt;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.HttpApi;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.Utils.Utils;
import av.VRP.rt.listener.FileWriterListener;
import av.VRP.rt.parser.ThreadParser;
import av.VRP.rt.listener.VRPgeneratorListener;
import av.VRP.rt.parser.ThreadWriter;
import av.VRP.rt.substance.PointWithTime;
import av.VRP.rt.substance.Trips;

import java.util.List;


/**
 * Created by Artem on 09.04.2016.
 */
public class Main implements VRPgeneratorListener<PointWithTime>, FileWriterListener {
    private static volatile Main instance;
    private MainFrame frame;

    public int n = 0;

    private volatile Trips trips;

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    private Main() {
        frame = new MainFrame();
        trips = new Trips();


        Log.p(System.currentTimeMillis());

    }

    public static void main(String[] args) {
        getInstance();
    }

    public void startParserThread() {
         /*
        VRPStaticData data = new VRPStaticData();
        data.setListener(this);
        */

        ThreadParser parser1 = new ThreadParser(1);
        parser1.setListener(this);
        ThreadParser parser2 = new ThreadParser(2);
        parser2.setListener(this);
        ThreadParser parser3 = new ThreadParser(3);
        parser3.setListener(this);
        ThreadParser parser4 = new ThreadParser(4);
        parser4.setListener(this);

        parser1.start();
        parser2.start();
        parser3.start();
        parser4.start();
    }

    public void aggregateStatistic() {
        trips.sortWithDate();
        showStatistic();
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
        n = 0;
        frame.startDownloading();
    }

    @Override
    public void stoped(int count) {
        n++;
        Log.d("stopped ", count);
        if (n > 3) {
            Log.d("last stopped ", count);

            // trips.removeNull();
            //  trips.sortWithDate();
            // showStatistic();
            frame.endDownloading();//начать парсить
        }
    }

    private void showStatistic() {//FIXME rename
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

        Log.p(n, ". ", row);
    }

    public void aggregateList() {
        Log.p("Скачивание списка ссылок");

        String rowGreenYellow = HttpApi.getInstance().getContent(Constant.URL_ALL_GREEN_AND_YELLOW);
        String rowUber = HttpApi.getInstance().getContent(Constant.URL_ALL_UBER);

        String[] rows = Utils.strToArray(rowUber + "\n" + rowGreenYellow, "\n");

        frame.setListData(rows);
    }

    public void aggregateLink(List<String> list) {
        Log.p("Скачивание выбранной ссылки");
        Log.p(list.toString());

        frame.startDownloading();
        ThreadWriter thread = new ThreadWriter(list.toArray(new String[list.size()]));
        thread.setListener(this);
        thread.start();
    }

    @Override
    public void onSuccess() {
        startParserThread();
        // frame.endDownloading();//начать парсить
    }

    @Override
    public void onError() {
        frame.endDownloading();
    }
}