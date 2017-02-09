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

    public void setTrip(Trip trip) {
        setBusy(true);

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

    public boolean containOrder() {
        return startPoint != null;
    }

    public boolean move() {
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
            Log.e("arrived");
            withClient = true; // arrived to client
            calculateSteps(currPoint.toLatLng(), endPoint.toLatLng());
            return false;
        }
        if (stepsLat == 0 && stepsLng == 0) {
            Log.e("moveToClient","arrived");
            withClient = true; // arrived to client
            calculateSteps(currPoint.toLatLng(), endPoint.toLatLng());
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

        if(currPoint.getLat()<0.1){
            Log.p();
        }

        if (MapUtils.getDistance(currPoint, endPoint) < Constant.PRECISION) {
            withClient = false; // arrived to client end
            isBusy = false;
            // TODO move to cluster
            return false;
        }
        if (stepsLat == 0 && stepsLng == 0) {
            Log.e("moveToEndPoint","arrived");
            withClient = false; // arrived to client end
            isBusy = false;
            return false;
        }

        currTime.plusMinutes(1);

        currPoint.plusLat(Constant.STEP * Math.signum(-stepsLat));
        currPoint.plusLng(Constant.STEP * Math.signum(-stepsLng));
        stepsLat -= Math.signum(stepsLat);
        stepsLng -= Math.signum(stepsLng);

        return true;
    }

    private void calculateSteps(LatLng start, LatLng end) {
        double lat = start.getLat() - end.getLat();
        double lng = start.getLng() - end.getLng();

        stepsLat = (int) Math.round(lat / Constant.STEP);
        stepsLng = (int) Math.round(lng / Constant.STEP);
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