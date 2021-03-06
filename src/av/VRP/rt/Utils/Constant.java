package av.VRP.rt.Utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Artem on 10.04.2016.
 */
public class Constant {
    public static final DateTimeFormatter FMT = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
    public static final String KEY = "AIzaSyAOFN5rfvcfxbpYQJ2orX1kYwBxTnOP4w8";

    public static final String URL_ALL_GREEN_AND_YELLOW = "https://raw.githubusercontent.com/ArtBigTema/VRP_statistics/master/resources/nyc-taxi-data_green_and_yellow.txt";
    public static final String URL_ALL_UBER = "https://raw.githubusercontent.com/ArtBigTema/VRP_statistics/master/resources/nyc-taxi-data_uber.txt";

    public static final String URL_FIRST = "https://raw.githubusercontent.com/fivethirtyeight/uber-tlc-foil-response/master/uber-trip-data/uber-raw-data-apr14.csv";

    public static final String TITLE_BTN_IMITATION = "Start imitation";
    public static final String TITLE_BTN_IMITATION_AGAIN = "Start imitation again";

    public static final String TITLE_DT = "Date&Time";
    public static final String TITLE_LAT = "Latitude";
    public static final String TITLE_LON = "Longitude";

    public static final String[] TABLE_TITLES_FOR_UBER = new String[]{
            TITLE_DT, TITLE_LAT, TITLE_LON};

    public static final String TITLE_S_DT = "Start Date&Time";
    public static final String TITLE_E_DT = "End Date&Time";
    public static final String TITLE_S_LAT = "Start Latitude";
    public static final String TITLE_S_LON = "Start Longitude";
    public static final String TITLE_E_LAT = "End Latitude";
    public static final String TITLE_E_LON = "End Longitude";

    public static final String MSG_MORE_ONE = "Select more 2 elements";
    public static final String MSG_IMITATION = "Иммитация окончена";

    public static final String[] TABLE_TITLES_FOR_GY = new String[]{
            TITLE_S_DT, TITLE_S_LAT, TITLE_S_LON, TITLE_E_DT, TITLE_E_LAT, TITLE_E_LON};

    public static final String UBER = "uber";
    public static final String GREEN = "green";
    public static final String YELLOW = "yellow";

    public static final String DIR = "Files";
    public static final String DIR_FOR_SCALA = "FilesScala";
    public static final String FILE = "file";
    public static final String FILE_FORMAT = ".txt";
    public static final String FILE_FORMAT_FOR_SCALA = ".tsv";
    public static final String FILE_ENCODING = "UTF-8";

    public static final int CLUSTERS = 6;
    public static final int CLUSTERS_MAX = 12; // max hash

    public static final int VEHICLES = 100;
    public static final int TRIPS = 1000;

    public static final int MIDDLE_SIZE = 999;
    public static final int BIG_SIZE = 50000;

    public static final int TIME_WAITING = 50;//steps

    public static final double STEP = 0.0005;
    public static final double PRECISION = 0.001;
    public static final double NEARBY = 0.001;
    public static final double DISTANCE = 1;
}