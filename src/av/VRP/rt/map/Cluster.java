package av.VRP.rt.map;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.substance.Point;
import av.VRP.rt.substance.PointWithMessage;
import av.VRP.rt.substance.Trip;
import av.VRP.rt.substance.Trips;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Artem on 08.02.2017.
 */
public class Cluster {
    private List<PointWithMessage> clusters;
    private int count;
    private int countPoints;

    public Cluster() {
        count = Constant.CLUSTERS;
        clusters = new ArrayList<>();
    }

    public void add(Point point, int v) {
        for (PointWithMessage p : clusters) {
            if (p.getHash(v).equals(point.getHash(v))) {
                p.incLatLng(point);
                p.incClust();
                return;
            }
        }
        clusters.add(new PointWithMessage(point, point.getHashFull()));
    }

    public List<PointWithMessage> getClusters() {
        return clusters;
    }


    public void constructClusters(Trips trips, int v) {
        Log.p("start constructCluster");

        List<Trip> points = trips.getSubAll();
        countPoints = points.size();

        for (Trip point : points) {
            add(point.getStartPoint(), v);
        }
        Collections.sort(clusters);

        Log.p("end constructCluster");
    }

    public void constructClusters(Trips trips) {
        constructClusters(trips, Constant.CLUSTERS);
    }

    public List<PointWithMessage> getPoints() {
        return clusters;
    }

    public void clear() {
        clusters.clear();
    }

    public PointWithMessage get(int i) {
        return clusters.get(i);
    }

    public int size() {
        return clusters.size();
    }

    public int getTripSize() {
        return countPoints;
    }
}