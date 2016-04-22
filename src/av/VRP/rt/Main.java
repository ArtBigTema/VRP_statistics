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

    private int wCount;
    private int pCount;

    public int n = 0;

    private volatile Trips trips;

    public static void main(String[] args) {
        getInstance();
    }

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
        Log.p("------------------");
        Log.p("Clear list");

        trips.clear();
        pCount = wCount = n = 0;
    }

    public void startParserThread() {
        Log.d("start threads parser");
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

        pCount = 0;
        parsers = new ArrayList<>(n);

        int count = 0;
        while (count != n) {
            ThreadParser thread = new ThreadParser(count++);
            thread.setListener(this);
            parsers.add(thread);
        }
        for (Thread thread : parsers) {//FIXME
            thread.start();
        }
    }

    public void aggregateStatistic() {
        trips.sortWithDate();
        showStatistic();
    }

    private void showStatistic() {//FIXME rename
        String[] dates = trips.getActiveDaysStr();
        Long[] counts = trips.getCountTripsForEveryDay();
        String month = trips.getMonthYear();

        frame.showGraph(dates, counts, month);
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
        Log.p(list);
        n = list.size();

        trips.setTitle(list.toString());
        frame.startDownloading();

        if (writers != null) {
            for (ThreadWriter writer : writers) {
                writer.interrupt();
                writer = null;//FIXME ask
            }
            writers = null;
        }

        wCount = 0;
        writers = new ArrayList<>(n);

        for (String s : list) {
            ThreadWriter thread = new ThreadWriter(s, list.indexOf(s));//FIXME
            thread.setListener(this);
            writers.add(thread);
        }
        for (Thread thread : writers) {//FIXME
            thread.start();
        }
    }

    @Override
    public void onSuccess() {
        wCount++;
        Log.d("stopped thread writer");
        if (wCount >= writers.size()) {
            Log.d("stopped all threads writer");
            startParserThread();
            frame.setTableModel(false);//обновить таблицу
            // frame.endDownloading();//начать парсить
        }
    }

    @Override
    public void show(String[] row) {

    }

    @Override
    public void show(int n, String row) {
        //  sb.append(System.currentTimeMillis() + "-" + n + "." + row);
        Log.p(n, ". ", row);
    }

    @Override
    public void onError() {
        frame.endDownloading(false);
    }

    @Override
    public void generated(String s) {//fixme thread
        if (trips.size() < 200_000) {//fixme artefact
            trips.add(s);
        }
        //    frame.showData(t.toString() + "\n");
    }

    @Override
    public void started() {
        frame.startDownloading();
    }

    @Override
    public void stoped(int count) {
        pCount++;
        Log.d("stopped thread parser");

        if (pCount >= parsers.size()) {
            Log.d("stopped all threads parser");//fixme indexOf
            Log.d("Trips size= ", trips.size());

            frame.setTableData(trips.toTable());
            frame.endDownloading(true);//начать парсить
        }
    }
}