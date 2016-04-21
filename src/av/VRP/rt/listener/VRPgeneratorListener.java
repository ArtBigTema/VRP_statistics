package av.VRP.rt.listener;

/**
 * Created by Artem on 09.04.2016.
 */
public interface VRPgeneratorListener {//FIXME and rename

    public void generated(String s);

    public void started();

    public void stoped(int count);

    public void show(String[] row);

    public void show(int n, String row);
}