package av.VRP.rt;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.HttpApi;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.Utils.Utils;
import av.VRP.rt.listener.FileWriterListener;
import av.VRP.rt.parser.ThreadParser;
import av.VRP.rt.listener.VRPgeneratorListener;
import av.VRP.rt.parser.ThreadWriter;
import av.VRP.rt.substance.Trips;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Artem on 09.04.2016.
 */
public class Main implements VRPgeneratorListener, FileWriterListener {
    private static volatile Main instance;
    private MainFrame frame;

    private List<ThreadWriter> writers;
    private List<ThreadParser> parsers;

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
        Log.d("start thread parser");
        /*
        VRPStaticData data = new VRPStaticData();
        data.setListener(this);
        */
        if (parsers != null) {
            for (ThreadParser parser : parsers) {
                parser.interrupt();
                parser = null;//FIXME ask
            }
            parsers = null;
        }

        parsers = new ArrayList<>(n);//FIXME n - ?

        while (n != 0) {
            ThreadParser thread = new ThreadParser(--n);//FIXME reverse
            thread.setListener(this);
            parsers.add(thread);
            thread.start();
        }
    }

    public void aggregateStatistic() {
        trips.sortWithDate();
        showStatistic();
    }

    @Override
    public void generated(String s) {
        trips.add(s);
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
        Log.d("stopped thread parser");
        if (n > parsers.size() - 1) {
            Log.d("stopped last thread parser");//fixme indexOf
            Log.d("Size ", trips.size());
            // trips.removeNull();
            //  trips.sortWithDate();
            // showStatistic();
            frame.setTableData(trips.toTable());
            frame.endDownloading(true);//начать парсить
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

        if (writers != null) {
            for (ThreadWriter writer : writers) {
                writer.interrupt();
                writer = null;//FIXME ask
            }
            writers = null;
        }

        writers = new ArrayList<>(list.size());
        for (String s : list) {
            ThreadWriter thread = new ThreadWriter(s, list.indexOf(s));//FIXME
            thread.setListener(this);
            writers.add(thread);
            thread.start();
        }
    }

    @Override
    public void onSuccess() {
        n++;
        Log.d("stopped thread writer");
        if (n > writers.size() - 1) {
            Log.d("stopped last thread writer");
            startParserThread();
            frame.setTableModel(false);//обновить таблицу
            // frame.endDownloading();//начать парсить
        }
    }

    @Override
    public void onError() {
        frame.endDownloading(false);
    }
}