package av.VRP.rt.forecasting;

import av.VRP.rt.MainFrame;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.substance.Trips;
import net.sourceforge.openforecast.*;
import net.sourceforge.openforecast.models.*;
import org.jfree.data.time.TimeSeries;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Artem on 22.03.2017.
 */
public class OpenForecast {

    private Trips trips;

    private MainFrame mainFrame;

    public OpenForecast(MainFrame frame) {
        mainFrame = frame;
    }

    public void setTrips(Trips trips) {
        this.trips = trips;
    }

    public void startForD(int[] index) {
        Log.p("start openForecast ForD");

        Integer[] oldData = trips.getCountTripsForDay()[index[0]];

        DataSet observedData = new DataSet();
        DataPoint dp;

        for (int i = 0; i < oldData.length; i++) {
            dp = new Observation(oldData[i]);
            dp.setIndependentValue("x", i);
            observedData.add(dp);
        }
        //  observedData.setTimeVariable("x");
        //  observedData.setPeriodsPerYear(4);
        // observedData.setPeriodsPerYear(365);

        ForecastingModel testModel = Forecaster.getBestForecast(observedData);

        // ForecastingModel testModel = SimpleExponentialSmoothingModel.getBestFitModel(observedData);
        //   ForecastingModel testModel = TripleExponentialSmoothingModel.getBestFitModel(observedData);

        // ForecastingModel testModel                = DoubleExponentialSmoothingModel.getBestFitModel(observedData);

        System.out.println("Forecast model type selected: " + testModel.getForecastType());
        System.out.println(testModel.toString());

        DataSet requiredDataPoints = new DataSet();
        for (int i = 0; i < oldData.length + 5; i++) {

            dp = new Observation(0.0);
            dp.setIndependentValue("x", i);

            requiredDataPoints.add(dp);

        }
        //  desModel.forecast(requiredDataPoints);
        testModel.forecast(requiredDataPoints);
        //forecaster.forecast(requiredDataPoints);

        String[] months = new String[128];
        Arrays.fill(months, "Forecast");
        months[0] = "Real";


        Integer[][] counts = new Integer[2][requiredDataPoints.size()];
        int j = 0;
        for (DataPoint requiredDataPoint : requiredDataPoints) {
            counts[1][j++] = (int) Math.round(requiredDataPoint.getDependentValue());
        }
        counts[0] = oldData;

        mainFrame.showGraphForForecastD("Days", counts, months);
    }

    public void startForH(int[] index) {
        Log.p("start openForecast ForD");

        Integer[] oldData = trips.getCountTripsForHour()[index[0]];

        DataSet observedData = new DataSet();
        DataPoint dp;

        for (int i = 0; i < oldData.length; i++) {
            dp = new Observation(oldData[i]);
            dp.setIndependentValue("x", i);
            observedData.add(dp);
        }
        //  observedData.setTimeVariable("x");
        //  observedData.setPeriodsPerYear(4);
        // observedData.setPeriodsPerYear(365);

        ForecastingModel testModel
                = Forecaster.getBestForecast(observedData);

        // ForecastingModel testModel
        //         = SimpleExponentialSmoothingModel.getBestFitModel(observedData);
        //   ForecastingModel testModel = TripleExponentialSmoothingModel.getBestFitModel(observedData);
        //  ForecastingModel testModel = NaiveForecastingModel.getBestFitModel(observedData);

        //  ForecastingModel desModel
        //          = DoubleExponentialSmoothingModel.getBestFitModel(observedData);

        System.out.println("Forecast model type selected: " + testModel.getForecastType());
        System.out.println(testModel.toString());

        DataSet requiredDataPoints = new DataSet();
        for (int i = 0; i < oldData.length + 5; i++) {

            dp = new Observation(0.0);
            dp.setIndependentValue("x", i);

            requiredDataPoints.add(dp);

        }
        //  desModel.forecast(requiredDataPoints);
        testModel.forecast(requiredDataPoints);
        //forecaster.forecast(requiredDataPoints);

        String[] months = new String[128];
        Arrays.fill(months, "Forecast");
        months[0] = "Real";

        Integer[][] counts = new Integer[2][requiredDataPoints.size()];
        int j = 0;
        for (DataPoint requiredDataPoint : requiredDataPoints) {
            counts[1][j++] = (int) Math.round(requiredDataPoint.getDependentValue());
        }
        counts[0] = oldData;

        mainFrame.showGraphForForecastH("Days", counts, months);
    }
}