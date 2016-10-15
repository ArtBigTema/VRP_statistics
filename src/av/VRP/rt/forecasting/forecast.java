package av.VRP.rt.forecasting;

import av.VRP.rt.MainFrame;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.Utils.Utils;
import av.VRP.rt.substance.Trips;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Artem on 03.10.2016.
 */
public class Forecast {
    private Integer[] count;
    private Integer[][] oldData;

    private Integer[] countSecond;

    private List<List<Integer>> countForecastA;//float
    private Integer[] real;
    //   private Integer[] countForecast5;//float

    private Float alpha, delta, gamma; // 0..1
    private List<Float> arrAlpha, arrDelta, arrGamma; // 0..1

    private byte index = 0;

    private Trips trips;

    private String[] dates;//float

    private MainFrame mainFrame;

    public Forecast(MainFrame frame) {
        mainFrame = frame;
    }

    public void setTrips(Trips trips) {
        this.trips = trips;
    }

    public void setDates(String[] dates) {
        this.dates = dates;
    }

    public void preStart() {
        mainFrame.setListForecast(trips.getTitles());
    }

    public void startForH(int[] index) {//индексы выборки
        Log.p("start forecast ForH");

        //setCount(trips.getCountTripsForHour()[index]);
        oldData = new Integer[index.length][];
        int j = 0;
        for (int i : index) {
            oldData[j++] = trips.getCountTripsForHour()[i];
        }
        count = oldData[j - 1];//check ?
        real = oldData[j - 1];
        // countSecond = trips.getCountTripsForHour()[1];

        setDates(trips.getActiveHoursStr()[index[0]]);

        countForecastA = new ArrayList<>();
        //  countForecast5 = new Integer[count.length];

        startEXPma();
        // startMA2();

        Log.p("end  forecast ForH");
        showGraphicForH();
        Log.p("end graphic forecast ForH");
    }

    private int getMeanOldData(int i) {
        Integer sum = 0;
        for (Integer[] el : oldData) {
            sum += el[i];
        }
        return sum;
    }

    private void startEXPma() {
        alpha = gamma = delta = 0.1f;

        arrAlpha = new ArrayList<>();
        arrGamma = new ArrayList<>();
        arrDelta = new ArrayList<>();

        for (alpha = 0.5f; alpha < 1.4; alpha += 0.3f) {
            for (delta = 0.5f; delta < 1.4; delta += 0.3f) {
                for (gamma = 0.5f; gamma < 1.4; gamma += 0.3f) {
                    arrAlpha.add(alpha);
                    arrGamma.add(gamma);
                    arrDelta.add(delta);
                    List<Integer> tmp = new ArrayList<>();
                    Integer pred = 0;

                    for (int i = 0; i < count.length - 12; i++) {
                        tmp.add(count[i]);
                        pred = count[i];
                    }

                    for (int i = count.length - 12; i < count.length; i++) {
                        tmp.add(Math.round(delta *
                                (gamma * (alpha * getMeanOldData(i) + (1 - alpha) * pred) +
                                        (1 - gamma) * count[i - 2] +
                                        (1 - delta) * count[i - 12])));
                        pred = tmp.get(tmp.size() - 1);
                    }
                    countForecastA.add(tmp);
                }
            }
        }

        mainFrame.setListCoefForecastH(arrCoefToArrStr());
    }

    private void showGraphicForH() {
        String[][] ds = new String[28][];
        Arrays.fill(ds, dates);

        Integer[][] counts = new Integer[28][];
        int j = 1;
        counts[0] = count;
        for (List<Integer> tmp : countForecastA) {
            counts[j++] = tmp.toArray(new Integer[tmp.size()]);
        }
        //  counts[2] = countForecast3;
        String[] months = new String[28];
        Arrays.fill(months, "Forecast");

        mainFrame.showGraphForForecastH(ds, counts, months);
    }

    public void startForD(int[] index) {
        Log.p("start forecast ForD");
        //   setCount(trips.getCountTripsForDay()[index]);
        setDates(trips.getActiveDaysStr()[0]);
        count = trips.getCountTripsForDay()[0];
        countSecond = count;
        oldData = new Integer[index.length][];
        int j = 0;
        for (int i : index) {
            oldData[j++] = trips.getCountTripsForHour()[i];
        }
        count = oldData[j - 1];//check ?
        // countSecond = trips.getCountTripsForHour()[1];


        startEXPma();
        // startMA2();

        Log.p("end  forecast ForD");
        showGraphicForD();
        Log.p("end graphic forecast ForD");
    }

    private void showGraphicForD() {
        String[][] ds = new String[28][];
        Arrays.fill(ds, dates);
        //   ds[2] = dates;

        Integer[][] counts = new Integer[28][];
        int j = 1;
        counts[0] = count;
        for (List<Integer> tmp : countForecastA) {
            counts[j++] = tmp.toArray(new Integer[tmp.size()]);
        }
        //  counts[2] = countForecast3;
        String[] months = new String[28];
        Arrays.fill(months, "Forecast");
        mainFrame.showGraphForForecastD(ds, counts, months);
    }

    public String[] arrCoefToArrStr() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < arrAlpha.size(); i++) {
            sb.append("a: ");
            sb.append(arrAlpha.get(i).floatValue());
            sb.append(", d: ");
            sb.append(arrDelta.get(i).floatValue());
            sb.append(", g: ");
            sb.append(arrGamma.get(i).floatValue());
            sb.append("\n");
        }
        return Utils.strToArray(sb.toString(), "\n");
    }

    public void showForecastGraphicFor(int index) {
        String[][] ds = new String[][]{dates, dates};
        Integer[][] counts = new Integer[2][];
        counts[0] = real;
        List<Integer> tmp = countForecastA.get(index);
        counts[1] = tmp.toArray(new Integer[tmp.size()]);
        String[] months = new String[]{"Real", "ForecastEXp"};
        mainFrame.showGraphForForecastH(ds, counts, months);
    }
}