package av.VRP.rt.forecasting;

import java.util.Date;
import java.util.Iterator;

import net.sourceforge.openforecast.models.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.Observation;

/**
 * Created by Artem on 22.03.2017.
 */
public class Test extends ApplicationFrame {
    /**
     * The set of data points for which forecast values are required.
     */
    private TimeSeries fc;

    /**
     * A demonstration application showing a quarterly time series
     * along with the forecast values.
     *
     * @param title the frame title.
     */
    public Test(String title) {
        super(title);

        // Create a title...
        String chartTitle = "OpenForecast Demo";
        XYDataset dataset = createDataset();

        JFreeChart chart
                = ChartFactory.createTimeSeriesChart(chartTitle,
                "Date",
                "Quarterly Sales (Units sold)",
                dataset,
                true,  // Legend
                true,  // Tooltips
                false);// URLs

        XYPlot plot = chart.getXYPlot();


        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return the dataset.
     */
    public XYDataset createDataset() {
        TimeSeries observations
                = new TimeSeries("Quarterly Sales", Quarter.class);

        observations.add(new Quarter(1, 1990), 362.0);
        observations.add(new Quarter(2, 1990), 385.0);
        observations.add(new Quarter(3, 1990), 432.0);
        observations.add(new Quarter(4, 1990), 341.0);
        observations.add(new Quarter(1, 1991), 382.0);
        observations.add(new Quarter(2, 1991), 409.0);
        observations.add(new Quarter(3, 1991), 498.0);
        observations.add(new Quarter(4, 1991), 387.0);
        observations.add(new Quarter(1, 1992), 473.0);
        observations.add(new Quarter(2, 1992), 513.0);
        observations.add(new Quarter(3, 1992), 582.0);
        observations.add(new Quarter(4, 1992), 474.0);
        observations.add(new Quarter(1, 1993), 544.0);
        observations.add(new Quarter(2, 1993), 582.0);
        observations.add(new Quarter(3, 1993), 681.0);
        observations.add(new Quarter(4, 1993), 557.0);
        observations.add(new Quarter(1, 1994), 628.0);
        observations.add(new Quarter(2, 1994), 707.0);
        observations.add(new Quarter(3, 1994), 773.0);
        observations.add(new Quarter(4, 1994), 592.0);
        observations.add(new Quarter(1, 1995), 627.0);
        observations.add(new Quarter(2, 1995), 725.0);
        observations.add(new Quarter(3, 1995), 854.0);
        observations.add(new Quarter(4, 1995), 661.0);

        fc = new TimeSeries("Forecast values", Quarter.class);
        fc.add(new Quarter(1, 1990), 0.0);
        fc.add(new Quarter(2, 1990), 0.0);
        fc.add(new Quarter(3, 1990), 0.0);
        fc.add(new Quarter(4, 1990), 0.0);
        fc.add(new Quarter(1, 1991), 0.0);
        fc.add(new Quarter(2, 1991), 0.0);
        fc.add(new Quarter(3, 1991), 0.0);
        fc.add(new Quarter(4, 1991), 0.0);
        fc.add(new Quarter(1, 1992), 0.0);
        fc.add(new Quarter(2, 1992), 0.0);
        fc.add(new Quarter(3, 1992), 0.0);
        fc.add(new Quarter(4, 1992), 0.0);
        fc.add(new Quarter(1, 1993), 0.0);
        fc.add(new Quarter(2, 1993), 0.0);
        fc.add(new Quarter(3, 1993), 0.0);
        fc.add(new Quarter(4, 1993), 0.0);
        fc.add(new Quarter(1, 1994), 0.0);
        fc.add(new Quarter(2, 1994), 0.0);
        fc.add(new Quarter(3, 1994), 0.0);
        fc.add(new Quarter(4, 1994), 0.0);
        fc.add(new Quarter(1, 1995), 0.0);
        fc.add(new Quarter(2, 1995), 0.0);
        fc.add(new Quarter(3, 1995), 0.0);
        fc.add(new Quarter(4, 1995), 0.0);
        fc.add(new Quarter(1, 1996), 0.0);
        fc.add(new Quarter(2, 1996), 0.0);
        fc.add(new Quarter(3, 1996), 0.0);
        fc.add(new Quarter(4, 1996), 0.0);
        fc.add(new Quarter(1, 1997), 0.0);
        fc.add(new Quarter(2, 1997), 0.0);
        fc.add(new Quarter(3, 1997), 0.0);
        fc.add(new Quarter(4, 1997), 0.0);
        fc.add(new Quarter(1, 1998), 0.0);
        fc.add(new Quarter(2, 1998), 0.0);
        fc.add(new Quarter(3, 1998), 0.0);
        fc.add(new Quarter(4, 1998), 0.0);

        DataSet initDataSet = getDataSet(observations, 0, 100);
        initDataSet.setTimeVariable("t");
        initDataSet.setPeriodsPerYear(4);

        // Get "best fit" simple exponential smoothing model
        ForecastingModel sesModel
                = SimpleExponentialSmoothingModel.getBestFitModel(initDataSet);
        TimeSeries sesSeries
                = getForecastTimeSeries(sesModel,
                initDataSet,
                0, 30,
                "Simple Exponential Smoothing");

        // Get "best fit" double exponential smoothing model
        ForecastingModel desModel
                = DoubleExponentialSmoothingModel.getBestFitModel(initDataSet);
        TimeSeries desSeries
                = getForecastTimeSeries(desModel,
                initDataSet,
                0, 30,
                "Double Exponential Smoothing");

        // Get "best fit" triple exponential smoothing model
        ForecastingModel tesModel
                = TripleExponentialSmoothingModel.getBestFitModel(initDataSet);
        TimeSeries tesSeries
                = getForecastTimeSeries(tesModel,
                initDataSet,
                5, 28,
                "Triple Exponential Smoothing");

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(observations);
        dataset.addSeries(sesSeries);
        dataset.addSeries(desSeries);
        dataset.addSeries(tesSeries);

        return dataset;
    }

    /**
     * A helper function to convert data points (from startIndex to
     * endIndex) of a (JFreeChart) TimeSeries object into an
     * OpenForecast DataSet.
     *
     * @param series     the series of data points stored as a JFreeChart
     *                   TimeSeries object.
     * @param startIndex the index of the first data point required from the
     *                   series.
     * @param endIndex   the index of the last data point required from the
     *                   series.
     * @return an OpenForecast DataSet representing the data points extracted
     * from the TimeSeries.
     */
    private DataSet getDataSet(TimeSeries series,
                               int startIndex, int endIndex) {
        DataSet dataSet = new DataSet();
        if (endIndex > series.getItemCount())
            endIndex = series.getItemCount();

        for (int i = startIndex; i < endIndex; i++) {
            TimeSeriesDataItem dataPair = series.getDataItem(i);
            DataPoint dp = new Observation(dataPair.getValue().doubleValue());
            dp.setIndependentValue("t", i);
            dataSet.add(dp);
        }

        return dataSet;
    }

    /**
     * Use the given forecasting model to produce a TimeSeries object
     * representing the periods startIndex through endIndex, and containing
     * the forecast values produced by the model.
     *
     * @param model       the forecasting model to use to generate the forecast
     *                    series.
     * @param initDataSet data set to use to initialize the forecasting model.
     * @param startIndex  the index of the first data point to use from the
     *                    set of potential forecast values.
     * @param endIndex    the index of the last data point to use from the set
     *                    of potential forecast values.
     * @param title       a title to give to the TimeSeries created.
     */
    private TimeSeries getForecastTimeSeries(ForecastingModel model,
                                             DataSet initDataSet,
                                             int startIndex,
                                             int endIndex,
                                             String title) {
        // Initialize the forecasting model
        model.init(initDataSet);

        // Get range of data required for forecast
        DataSet fcDataSet = getDataSet(fc, startIndex, endIndex);

        // Obtain forecast values for the forecast data set
        model.forecast(fcDataSet);

        // Create a new TimeSeries
        TimeSeries series
                = new TimeSeries(title, fc.getTimePeriodClass());

        // Iterator through the forecast results, adding to the series
        Iterator it = fcDataSet.iterator();
        while (it.hasNext()) {
            DataPoint dp = (DataPoint) it.next();
            int index = (int) dp.getIndependentValue("t");
            series.add(fc.getTimePeriod(index), dp.getDependentValue());
        }

        return series;
    }

    /**
     * Starting point for the forecasting charting demo application.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        Test demo
                = new Test("Forecasting Demo: Time Series");
        demo.pack();
        demo.setVisible(true);
    }
}