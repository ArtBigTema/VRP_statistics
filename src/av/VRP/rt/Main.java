package av.VRP.rt;

import av.VRP.rt.Utils.*;
import av.VRP.rt.forecasting.Forecast;
import av.VRP.rt.listener.FileWriterListener;
import av.VRP.rt.listener.MessageListener;
import av.VRP.rt.listener.VRPgeneratorListener;
import av.VRP.rt.map.Cluster;
import av.VRP.rt.map.MapExample;
import av.VRP.rt.map.ThreadImitation;
import av.VRP.rt.parser.ThreadParser;
import av.VRP.rt.parser.ThreadWriter;
import av.VRP.rt.substance.Trips;
import av.VRP.rt.substance.Vehicles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Artem on 09.04.2016.
 */
public class Main implements VRPgeneratorListener, FileWriterListener, MessageListener {
    private static volatile Main instance;
    private MainFrame frame;

    private List<ThreadWriter> writers;
    private List<ThreadParser> parsers;
    private ThreadImitation imitation;

    private int wCount;
    private int pCount;

    private int n = 0;
    private int i = 0;
    private int size = 0;

    private volatile Trips trips;
    private volatile Vehicles vehicles;

    private Forecast forecast;
    private Cluster cluster;

    private boolean modeMap;

    public static void main(String[] args) throws Exception {
        getInstance();
    }

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
            Files.deleteDirectory();
        }
        return instance;
    }

    private Main() {
        modeMap = true;

        frame = new MainFrame();
        trips = new Trips();
        vehicles = new Vehicles();
        cluster = new Cluster();

        Log.p(System.currentTimeMillis());
    }

    public void clear() {
        Log.p("------------------");
        Log.p("Clear list");

        trips.clear();
        pCount = wCount = size = n = i = 0;
    }

    public void startParserThread() {
        Log.d("startForH threads parser");
        size = size / 200_000;//for 1m = 5

        /*
        VRPStaticData data = new VRPStaticData();
        data.setListener(this);
        */
        if (parsers != null) {
            for (ThreadParser parser : parsers) {
                parser.interrupt();
                parser = null;//FIXME ask
            }
            parsers.clear();
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

    public void aggregateStatisticForDay() {
        trips.sortWithDate();
        showStatisticForDay();
        Log.p(System.currentTimeMillis());
    }

    public void aggregateStatisticForHour() {
        //  trips.sortWithHour();
        showStatisticForHour();
        Log.p(System.currentTimeMillis());
    }

    private void showStatisticForDay() {//FIXME rename
        String[][] dates = trips.getActiveDaysStr();
        Integer[][] counts = trips.getCountTripsForDay();
        String[] months = trips.getTitles();

        frame.showGraphForDays(dates, counts, months);
    }

    private void showStatisticForHour() {//FIXME rename
        String[][] dates = trips.getActiveHoursStr();
        Integer[][] counts = trips.getCountTripsForHour();
        String[] months = trips.getTitles();

        frame.showGraphForHours(dates, counts, months);
    }

    public void aggregateList() {
        Log.p("Скачивание списка ссылок");

        String rowGreenYellow = HttpApi.getInstance().getContent(Constant.URL_ALL_GREEN_AND_YELLOW);
        String rowUber = HttpApi.getInstance().getContent(Constant.URL_ALL_UBER);

        String[] rows = Utils.strToArray(rowUber + "\n" + rowGreenYellow, "\n");

        frame.setListData(rows);

        if (modeMap) {
            //  frame.clickDownloadLink();
        }
    }

    public void aggregateLink(List<String> list) {
        clear();

        if (list == null) {
            list = Collections.singletonList(Constant.URL_FIRST);
        }

        Log.p("Скачивание выбранной ссылки");
        Log.p(list.size());
        Log.p(list);
        n = list.size();

        frame.startDownloading();

        if (writers != null) {
            for (ThreadWriter writer : writers) {
                writer.interrupt();
                writer = null;//FIXME ask
            }
            writers.clear();
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

    public void agregateForecast() {
        forecast = new Forecast(frame);
        forecast.setTrips(trips);

        forecast.preStart();
    }

    public void startForecastH(int[] index) {
        forecast.startForH(index);
    }

    public void startForecastD(int[] index) {
        forecast.startForD(index);
    }

    public void showForecastGraphicFor(int index, boolean forH) {
        forecast.showForecastGraphicFor(index, forH);
    }

    public void export() {
        Exporter.init(trips.getTitles()[0]);
        Exporter.export(trips.exportDataForDay());
    }

    @Override
    public void onSuccess(int count) {
        size += count;
        Log.p("Trips real listSize = ", count);
        wCount++;
        Log.d("stopped thread writer ", wCount);

        if (wCount >= writers.size()) {//FIXME if > then err
            Log.d("stopped all threads writer");
            Log.p("Max real listSize = ", size);

            frame.showPanelReadFile();
            frame.setTableModel(false);//обновить таблицу
            frame.endDownloading(true);

            if (modeMap) {
                frame.clickReadFile();
            }
        }
    }

    @Override
    public void onError() {
        frame.endDownloading(false);
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
    public void generated(String s) {//fixme thread

        //    frame.showData(t.toString() + "\n");
    }

    @Override
    public void generated(String s, String ss) {
        if (!modeMap) {
            trips.add(s, ss);
        }

        if (++i > size) {
            i = 0;
            trips.add(ss);
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
        Log.d("stopped thread parser ", pCount);

        if (pCount >= parsers.size()) {//FIXME if > then err
            Log.d("stopped all threads parser");
            Log.p("Screen Trips listSize = ", trips.listSize());
            Log.p("Trips mapSizeForDay = ", trips.mapSizeForDay());
            Log.p("Trips mapSizeForHour = ", trips.mapSizeForHour());

            frame.setTableData(trips.toTable());
            if (!modeMap) {
                frame.setTableData(trips.toTable());
                agregateForecast();//fixme
            }

            if (modeMap) {
                vehicles.setInitDateTime(trips.getFirstPoint());
                //vehicles.initDepo(trips.getPoints().subList(0, Constant.VEHICLES));//FIXME

                cluster.constructClusters(trips);

                frame.showMap();
            }
        }
    }

    private boolean isClicked;
    public void showPoints(MapExample map) {
        map.clearAll();

        if (!isClicked) {
            isClicked = true;
            map.showCluster(cluster);
        } else {
            isClicked = false;
            map.showAllPoints(trips, true);
        }
    }

    public void zoom(int zoom) {
        imitation.setDelay(zoom);
    }

    public void startImitation(MapExample map) {
        map.clearAll();
        imitation = new ThreadImitation(map);
        imitation.setMessageListener(this);
        imitation.setCluster(cluster);
        imitation.setTrips(trips);
        imitation.setVehicles(vehicles);
        imitation.run();
    }

    public void stopImitation() {
        if (imitation != null) {
            imitation.cancel();
            imitation.interrupt();
            imitation = null;//FIXME ask
        }
    }

    @Override
    public void showMessage(String msg) {
        frame.showMessage(msg);
    }
}