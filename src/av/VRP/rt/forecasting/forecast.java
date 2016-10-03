package av.VRP.rt.forecasting;

import av.VRP.rt.MainFrame;
import av.VRP.rt.Utils.Log;

/**
 * Created by Artem on 03.10.2016.
 */
public class Forecast {
    private Integer[] count;
    private Integer[] countForecast;//float
    private Integer[] countForecast3;//float
    private Integer[] countForecast5;//float

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
        countForecast3 = new Integer[count.length];
        countForecast5 = new Integer[count.length];

        startMA2();

        mainFrame.setListForecastF(countForecast);

        showGraphic();
    }

    private void startMA2() {
        countForecast[0] = count[0];

        countForecast3[0] = count[0];
        countForecast3[1] = count[0];

        countForecast5[0] = count[0];
        countForecast5[1] = count[1];
        countForecast5[2] = count[2];

        for (int i = 1; i < count.length; i++) {
            countForecast[i] = (count[i] + count[i - 1]) / 2;
        }
        for (int i = 2; i < count.length; i++) {
            countForecast3[i] = (count[i] + count[i - 1] + count[i - 2]) / 3;
        }
    }

    private void showGraphic() {
        String[][] ds = new String[3][count.length];
        ds[0] = dates;
        ds[1] = dates;
        ds[2] = dates;

        Integer[][] counts = new Integer[3][];
        counts[0] = count;
        counts[1] = countForecast;
        counts[2] = countForecast3;
        String[] months = new String[]{title, "ForecastMa2", "ForecastMa3"};

        mainFrame.showGraphForForecast(ds, counts, months);
    }
}














