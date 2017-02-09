package av.VRP.rt.substance;

import com.teamdev.jxmaps.LatLng;

/**
 * Created by Artem on 09.02.2017.
 */
public class Vehicle {
    private PointWithTime startPoint;
    private PointWithTime endPoint;
    private PointWithTime currPoint;

    private boolean isBusy;
    private String fileIcon;
    private String title;

    public Vehicle() {
        // title = "Taxi" + System.nanoTime();
    }

    public void setFileIcon(String fileIcon) {
        this.fileIcon = fileIcon;
        title = "Taxi:" + fileIcon + ":" + System.nanoTime();
    }

    public String getTitle() {
        return title;
    }

    public String getFileIcon() {
        return fileIcon;
    }

    public void setPassager(Trip trip) {
        startPoint = trip.getStartPoint();
        endPoint = trip.getEndPoint();
    }

    public PointWithTime getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(PointWithTime startPoint) {
        this.startPoint = startPoint;
    }

    public PointWithTime getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(PointWithTime endPoint) {
        this.endPoint = endPoint;
    }

    public PointWithTime getCurrPoint() {
        return currPoint;
    }

    public void setCurrPoint(PointWithTime currPoint) {
        this.currPoint = currPoint;
    }

    public void setCurrPoint(Point currPoint) {
        this.currPoint = new PointWithTime(currPoint);
    }

    public void setCurrPoint(LatLng curr) {
        this.currPoint = new PointWithTime(new Point(curr.getLat()+0.001, curr.getLng()+0.001));
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }
}