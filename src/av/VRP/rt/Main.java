package av.VRP.rt;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.HttpApi;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.Utils.Utils;
import av.VRP.rt.listener.FileWriterListener;
import av.VRP.rt.parser.ThreadParser;
import av.VRP.rt.listener.VRPgeneratorListener;
import av.VRP.rt.parser.ThreadWriter;
import av.VRP.rt.substance.Trip;
import av.VRP.rt.substance.Trips;

import java.util.List;


/**
 * Created by Artem on 09.04.2016.
 */
public class Main implements VRPgeneratorListener<Trip>, FileWriterListener {
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

    public void clear() {
        Log.p("Clear list");
        trips.clear();
        n = 0;
    }

    public static void main(String[] args) {
        getInstance();
    }

    public void startParserThread() {
        /*
        VRPStaticData data = new VRPStaticData();
        data.setListener(this);
        */

        //fixme count
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
    public void generated(Trip t) {
        if (t != null) {
            trips.add(t);
            frame.addRow(t.toTableVector());
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
            Log.d("Size ", trips.size());
            // trips.removeNull();
            //  trips.sortWithDate();
            // showStatistic();
            frame.endDownloading();//начать парсить
        }
    }

    private void showStatistic() {//FIXME rename
        String[] dates = trips.getActiveDaysStr();
        Long[] counts = trips.getCountTripsForEveryDay();
        String month = trips.getMonthYear();

        frame.showGraph(dates, counts, month);
    }

    @Override
    public void show(String[] row) {
        frame.addRow(row); //FIXME add all rows
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
        clear();

        Log.p("Скачивание выбранной ссылки");
        Log.p(list.toString());

        trips.setTitle(list.toString());
        frame.startDownloading();

        ThreadWriter thread = new ThreadWriter(list.toArray(new String[list.size()]));
        thread.setListener(this);
        thread.start();
    }

    @Override
    public void onSuccess() {
        startParserThread();
        frame.setTableModel(false);//обновить таблицу
        // frame.endDownloading();//начать парсить
    }

    @Override
    public void onError() {
        frame.endDownloading();
    }
}