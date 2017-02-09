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
    private PointWithTime startPoint;
    private PointWithTime endPoint;

    private Point currPoint;
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

    public void setTrip(Trip trip, int index) {
        setBusy(true);
        indexOfTrip = index;

        startPoint = trip.getStartPoint();
        endPoint = trip.getEndPoint();

        if (endPoint == null) {
            endPoint = new PointWithTime(Point.nearby(startPoint));//generate nearby
        }
        calculateSteps(currPoint.toLatLng(), startPoint.toLatLng());
    }

    public Point getCurrPoint() {
        return currPoint;
    }

    public void setCurrPoint(LatLng curr) {
        currPoint = new Point(curr.getLat() + 0.005, curr.getLng() + 0.005);
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
        Log.p("moveToClient", currPoint.toLatLng(), startPoint.toLatLng());

        if (MapUtils.getDistance(currPoint, startPoint) < Constant.PRECISION) {
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
        Log.p("moveToEndPoint", currPoint.toLatLng(), endPoint.toLatLng());

        if (MapUtils.getDistance(currPoint, endPoint) < Constant.PRECISION) {
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
        startPoint = null;
        endPoint = null;
    }

    private void arrivedToPointStart() {
        isBusy = true;
        withClient = true; // arrived to client
        calculateSteps(currPoint.toLatLng(), endPoint.toLatLng());
    }

    private void calculateSteps(LatLng start, LatLng end) {
        double lat = start.getLat() - end.getLat();
        double lng = start.getLng() - end.getLng();

        stepsLat = (int) Math.round(lat / Constant.STEP);
        stepsLng = (int) Math.round(lng / Constant.STEP);
    }

    public boolean containEndPoint() {
        return indexOfTrip > 0 && !isBusy;
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
}