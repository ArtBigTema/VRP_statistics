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
    private Point depo;
    private int depoIndex;
    private DateTime currTime;

    private boolean isBusy;
    private boolean withClient;
    private boolean goToDepo;

    private String fileIcon;
    private String title;

    private int stepsLat = 0;
    private int stepsLng = 0;
    private int indexOfTrip = -1;

    public Vehicle(int i) {
        title = "#" + i;
        // title = "Taxi" + System.nanoTime();
    }

    public void setFileIcon(String fileIcon) {
        this.fileIcon = fileIcon;
        //  title = "Taxi: " + title; //+ ":" + System.nanoTime();
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
        goToDepo = false;

        trip = t;

        if (t.getEndPoint() == null) {
            Log.e("errrrrrrrrrrrrrrrrrrrroooooooor");
        }
        calculateSteps(currPoint.toLatLng(), t.getStartPoint().toLatLng());
    }

    public Point getCurrPoint() {
        return currPoint;
    }

    public void setCurrPoint(LatLng curr, int i) {
        depo = new Point(curr.getLat(), curr.getLng());
        depoIndex = i;
        currPoint = new PointWithTime(curr);
        currPoint.setDateTime(currPoint.getDateTime());
    }

    public int getDepoIndex() {
        return depoIndex;
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

    public boolean moveToDepo() {
        if (!goToDepo) {
            return false;
        }

        Log.p();
        Log.p("moveToDepo", currPoint.toLatLng(), depo.toLatLng());

        if (MapUtils.getDistance(currPoint, depo) < Constant.PRECISION) {
            Log.e("arrived to depo with distance");
            stepsLat = stepsLng = 0;//fixme
            goToDepo = false;
            return false;
        }
        if (stepsLat == 0 && stepsLng == 0) {
            Log.e("arrived to Depo with 00");
            goToDepo = false;
            return false;
        }

        currTime.plusMinutes(1);

        currPoint.plusLat(Constant.STEP * Math.signum(-stepsLat));
        currPoint.plusLng(Constant.STEP * Math.signum(-stepsLng));
        stepsLat -= Math.signum(stepsLat);
        stepsLng -= Math.signum(stepsLng);

        return true;
    }

    private boolean moveToClient() {
        Log.p();
        Log.p("moveToClient", currPoint.toLatLng(), trip.getStartPoint().toLatLng());

        if (MapUtils.getDistance(currPoint, trip.getStartPoint()) < Constant.PRECISION) {
            Log.e("arrived to client with getDistance");
            stepsLat = stepsLng = 0;//fixme
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

        return false; //move to end
    }

    private boolean moveToEndPoint() {
        Log.p();
        Log.p("moveToEndPoint", currPoint.toLatLng(), trip.getEndPoint().toLatLng());

        if (MapUtils.getDistance(currPoint, trip.getEndPoint()) < Constant.PRECISION) {
            Log.e("arrived moveToEndPoint with distance");
            stepsLat = stepsLng = 0;//fixme
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
        //  indexOfTrip = -1;
        goToDepo = true;

        //  calculateSteps(currPoint.toLatLng(), depo.toLatLng());
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

    public void resetDepo(Point point, int i) {
        depo = point;
        depoIndex = i;
        calculateSteps(currPoint.toLatLng(), depo.toLatLng());
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

    public Point getDepo() {
        return depo;
    }

    public boolean goToDepo() {
        return goToDepo;
    }

    public void resetGoDepo() {
        goToDepo = false;
    }
}