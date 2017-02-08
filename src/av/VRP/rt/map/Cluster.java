package av.VRP.rt.map;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.substance.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artem on 08.02.2017.
 */
public class Cluster {
    private List<Point> clusters;
    private int count;

    public Cluster() {
        count = Constant.CLUSTERS;
        clusters = new ArrayList<>();
    }

    public void add(Point point) {
        for (Point p : clusters) {
            if (p.getHash().equals(point.getHash())) {
                return;
            }
        }
        clusters.add(point);
    }

    public List<Point> getClusters() {
        return clusters;
    }
}