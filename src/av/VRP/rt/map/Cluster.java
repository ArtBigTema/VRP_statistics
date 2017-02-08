package av.VRP.rt.map;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.substance.Point;
import av.VRP.rt.substance.PointWithMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artem on 08.02.2017.
 */
public class Cluster {
    private List<PointWithMessage> clusters;
    private int count;

    public Cluster() {
        count = Constant.CLUSTERS;
        clusters = new ArrayList<>();
    }

    public void add(Point point) {
        for (PointWithMessage p : clusters) {
            if (p.getHash().equals(point.getHash())) {
                p.incClust();
                return;
            }
        }
        clusters.add(new PointWithMessage(point));
    }

    public List<PointWithMessage> getClusters() {
        return clusters;
    }
}