package av.VRP.rt.forecasting;

import av.VRP.rt.MainFrame;
import av.VRP.rt.Utils.Log;

/**
 * Created by Artem on 03.10.2016.
 */
public class Forecast {
    private Integer[] count;
    private Integer[] countForecast;//float
    private Integer[][] countForecastA;//float
    //   private Integer[] countForecast5;//float
    private Float alpha, delta, gamma; // 0..1

    private String[] dates;//float

    private String title;
    private MainFrame mainFrame;

    public Forecast(MainFrame frame) {
        mainFrame = frame;
    }

    public void setCount(Integer[] count) {
        this.count = count;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDates(String[] dates) {
        this.dates = dates;
    }

    public void preStart() {
        mainFrame.setBtnTitle(title);
        mainFrame.setListForecast(count);
    }

    public void start() {
        Log.p("start forecast");

        countForecast = new Integer[count.length];
        countForecastA = new Integer[128][count.length];
        //  countForecast5 = new Integer[count.length];

        startEXPma();
        // startMA2();

        mainFrame.setListForecastF(countForecast);

        showGraphic();
    }

    private void startEXPma() {

        for (int i = 0; i <= count.length - 12; i++) {
            countForecast[i] = count[i];
        }
        alpha = gamma = delta = 0.1f;

        int j = 0;

        for (alpha = 0.6f; alpha <= 1.3; alpha += 0.3f) {
            for (delta = 0.6f; delta <= 1.3; delta += 0.3f) {
                for (gamma = 0.6f; gamma <= 1.3; gamma += 0.3f) {

                    for (int i = 0; i < count.length - 12; i++) {
                        countForecastA[j][i] = count[i];
                    }

                    for (int i = count.length - 12; i < count.length; i++) {
                        countForecastA[j][i] = Math.round(delta *
                                (gamma * (alpha * count[i] + (1 - alpha) * countForecastA[j][i - 1]) +
                                        (1 - gamma) * count[i - 2] +
                                        (1 - delta) * count[i - 12]));
                    }
                    j++;
                }
            }
        }
    }

    private void startMA2() {
        countForecast[0] = count[0];


        for (int i = 1; i < count.length; i++) {
            countForecast[i] = (count[i] + count[i - 1]) / 2;
        }
        for (int i = 2; i < count.length; i++) {
            // countForecast3[i] = (count[i] + count[i - 1] + count[i - 2]) / 3;
        }
    }

    private void showGraphic() {
        String[][] ds = new String[][]{dates, dates, dates, dates, dates, dates, dates, dates, dates, dates, dates, dates, dates,
                dates, dates, dates, dates, dates, dates, dates, dates, dates, dates, dates, dates, dates,
                dates, dates};
        //   ds[2] = dates;

        Integer[][] counts = new Integer[28][];
        counts[0] = count;
        counts[1] = countForecastA[0];
        counts[2] = countForecastA[1];
        counts[3] = countForecastA[2];
        counts[4] = countForecastA[3];
        counts[5] = countForecastA[4];
        counts[6] = countForecastA[5];
        counts[7] = countForecastA[6];
        counts[8] = countForecastA[7];
        counts[9] = countForecastA[8];
        counts[10] = countForecastA[9];
        counts[11] = countForecastA[10];
        counts[12] = countForecastA[11];
        counts[13] = countForecastA[12];
        counts[14] = countForecastA[13];
        counts[15] = countForecastA[14];
        counts[16] = countForecastA[15];
        counts[17] = countForecastA[16];
        counts[18] = countForecastA[17];
        counts[19] = countForecastA[18];
        counts[20] = countForecastA[19];
        counts[21] = countForecastA[20];
        counts[22] = countForecastA[21];
        counts[23] = countForecastA[22];
        counts[24] = countForecastA[23];
        counts[25] = countForecastA[24];
        counts[26] = countForecastA[25];
        counts[27] = countForecastA[26];
        //  counts[2] = countForecast3;
        String[] months = new String[]{title, "ForecastEXp1", "ForecastEXp2", "ForecastEXp3", "ForecastEXp4", "ForecastEXp5",
                "ForecastEXp6", "ForecastEXp7", "ForecastEXp8", "ForecastEXp", "ForecastEXp10"
                , "ForecastEXp1", "ForecastEXp2", "ForecastEXp3", "ForecastEXp4", "ForecastEXp5",
                "ForecastEXp6", "ForecastEXp7", "ForecastEXp8", "ForecastEXp", "ForecastEXp10"
                , "ForecastEXp1", "ForecastEXp2", "ForecastEXp3", "ForecastEXp4", "ForecastEXp5",
                "ForecastEXp6", "ForecastEXp6"};

        mainFrame.showGraphForForecast(ds, counts, months);
    }
}