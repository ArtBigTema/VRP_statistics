package av.VRP.rt.substance;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.map.MapUtils;
import com.teamdev.jxmaps.LatLng;
import org.joda.time.DateTime;

/**
 * Created by Artem on 09.02.2017.
 */
public class Vehicle {
    private Trip trip;

    private PointWithTime currPoint;
    private DateTime currTime;

    private boolean isBusy;
    private boolean withClient;

    private String fileIcon;
    private String title;

    private int stepsLat = 0;
    private int stepsLng = 0;
    private int indexOfTrip = -1;

    public Vehicle() {
        // title = "Taxi" + System.nanoTime();
    }

    public void setFileIcon(String fileIcon) {
        this.fileIcon = fileIcon;
        title = "Taxi:" + fileIcon; //+ ":" + System.nanoTime();
    }

    public String getTitle() {
        return title;
    }

    public String getFileIcon() {
        return fileIcon;
    }

    public void setTrip(Trip t, int index) {
        setBusy(true);
        indexOfTrip = index;

        trip = t;

        if (t.getEndPoint() == null) {
            Log.e("errrrrrrrrrrrrrrrrrrrroooooooor");
        }
        calculateSteps(currPoint.toLatLng(), t.getStartPoint().toLatLng());
    }

    public Point getCurrPoint() {
        return currPoint;
    }

    public void setCurrPoint(LatLng curr) {
        currPoint = new PointWithTime(curr, currPoint.getDateTime().getMillis());
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public boolean move() {
        if (Math.abs(currPoint.getLat()) < 0.1 || Math.abs(currPoint.getLng()) < 0.1) {
            Log.e("errrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrooooooooooooooooooooorr");
        }

        if (isBusy) {
            if (!withClient) {
                return moveToClient();
            } else {
                return moveToEndPoint();
            }
        } else {
            return false;  //TODO move to cluster
        }
    }

    private boolean moveToClient() {
        Log.p();
        Log.p("moveToClient", currPoint.toLatLng(), trip.getStartPoint().toLatLng());

        if (MapUtils.getDistance(currPoint, trip.getStartPoint()) < Constant.PRECISION) {
            Log.e("arrived to client with getDistance");
            arrivedToPointStart();
            return false;
        }
        if (stepsLat == 0 && stepsLng == 0) {
            Log.e("arrived to client with 00");
            arrivedToPointStart();
            return false;
        }

        currTime.plusMinutes(1);

        currPoint.plusLat(Constant.STEP * Math.signum(-stepsLat));
        currPoint.plusLng(Constant.STEP * Math.signum(-stepsLng));
        stepsLat -= Math.signum(stepsLat);
        stepsLng -= Math.signum(stepsLng);

        return true;
    }

    private boolean moveToEndPoint() {
        Log.p();
        Log.p("moveToEndPoint", currPoint.toLatLng(), trip.getEndPoint().toLatLng());

        if (MapUtils.getDistance(currPoint, trip.getEndPoint()) < Constant.PRECISION) {
            Log.e("arrived moveToEndPoint with distance");
            arrivedToPointEnd();
            // TODO move to cluster
            return false;
        }
        if (stepsLat == 0 && stepsLng == 0) {
            Log.e("arrived moveToEndPoint with 00");
            arrivedToPointEnd();
            return false;
        }

        currTime.plusMinutes(1);

        currPoint.plusLat(Constant.STEP * Math.signum(-stepsLat));
        currPoint.plusLng(Constant.STEP * Math.signum(-stepsLng));
        stepsLat -= Math.signum(stepsLat);
        stepsLng -= Math.signum(stepsLng);

        return true;
    }

    private void arrivedToPointEnd() {
        withClient = false; // arrived to client end
        isBusy = false;
        // startPoint = null;
        // endPoint = null;
    }

    private void arrivedToPointStart() {
        isBusy = true;
        withClient = true; // arrived to client
        calculateSteps(currPoint.toLatLng(), trip.getEndPoint().toLatLng());
    }

    private void calculateSteps(LatLng start, LatLng end) {
        double lat = start.getLat() - end.getLat();
        double lng = start.getLng() - end.getLng();

        stepsLat = (int) Math.round(lat / Constant.STEP);
        stepsLng = (int) Math.round(lng / Constant.STEP);
    }

    public boolean containEndPoint() {
        return indexOfTrip >= 0 && !isBusy;
    }

    public int getIndexOfTrip() {
        return indexOfTrip;
    }

    public void resetTrip() {
        indexOfTrip = -1;
    }

    @Override
    public String toString() {
        return "Vehicle{" + title + " " +
                "isBusy=" + isBusy +
                '}';
    }

    public void initTime(DateTime initDateTime) {
        currTime = new DateTime(initDateTime);
    }

    public boolean timeMoreDistance(PointWithTime startPoint) {
        double lat = currPoint.getLat() - startPoint.getLat();
        double lng = currPoint.getLng() - startPoint.getLng();

        stepsLat = (int) Math.round(lat / Constant.STEP);
        stepsLng = (int) Math.round(lng / Constant.STEP);

        return Math.abs(stepsLat) > Constant.TIME_WAITING
                || Math.abs(stepsLng) > Constant.TIME_WAITING;
    }

    public void incTime() {
        currPoint.incTime();
    }
}