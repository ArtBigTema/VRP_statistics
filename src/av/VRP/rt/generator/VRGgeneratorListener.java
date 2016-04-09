package av.VRP.rt.generator;

import av.VRP.rt.substance.Point;
import av.VRP.rt.substance.PointT;

/**
 * Created by Artem on 09.04.2016.
 */
public interface VRGgeneratorListener {
    public void generated(PointT t);

    public void generated(Point t);

    public void started();

    public void stoped(int count);
}
