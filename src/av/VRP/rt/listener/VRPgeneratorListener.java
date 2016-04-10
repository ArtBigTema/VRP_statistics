package av.VRP.rt.listener;

import av.VRP.rt.substance.Point;
import av.VRP.rt.substance.PointT;

/**
 * Created by Artem on 09.04.2016.
 */
public interface VRPgeneratorListener {//FIXME

    public void generated(PointT t);

    public void generated(Point t);

    public void started();

    public void stoped(int count);

    public void show(String row);

    public void show(int n, String row);
}