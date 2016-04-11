package av.VRP.rt.listener;

import av.VRP.rt.substance.Point;
import av.VRP.rt.substance.PointT;
import av.VRP.rt.substance.PointWithTime;

/**
 * Created by Artem on 09.04.2016.
 */
public interface VRPgeneratorListener<E> {//FIXME

    public void generated(E t);

    public void started();

    public void stoped(int count);

    public void show(String row);

    public void show(int n, String row);
}