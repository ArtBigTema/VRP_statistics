package av.VRP.rt.generator;

import av.VRP.rt.listener.VRPgeneratorListener;
import av.VRP.rt.substance.Point;
import av.VRP.rt.substance.PointT;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class VRPStaticData {//FIXME all
    private int DELAY = 10000, START = 10000;

    // private PointT currPointT;
    // private Point currPoint;
    private Timer timer;
    private Point max;
    private Point min;
    private long stop = 5;
    private int maxTime = 10;
    private int predMaxTime = 0;

    int count = 0;

    public VRPgeneratorListener listener;

    public VRPStaticData() {
        timer = new Timer();
    }

    public void setListener(VRPgeneratorListener lis) {
        this.listener = lis;
    }


    public void startTimer() {
        if (timer == null) {
            return;
        }
        if (listener != null) {
            listener.started();
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (decTime()) {
                    genertate();
                }
            }

            private boolean decTime() {
                stop--;
                if (stop < 0) {
                    return stopTimer();
                }
                return true;
            }
        }, DELAY, START);
    }

    public boolean stopTimer() {
        timer.cancel();
        timer = null;
        if (listener != null) {
            listener.stoped(count);
        }
        generatePoint();
        return false;
    }

    private void genertate() {
        if (listener != null) {
            listener.generated(generatePointT().toString());
            // listener.generated(generatePoint());
        }
    }

    private PointT generatePointT() {
        count++;
        PointT t = new PointT(new Point(random(min.getLat(), max.getLat()), random(min.getLng(), max.getLng())));
        t.setEndPoint(random(min.getLat(), max.getLat()), random(min.getLng(), max.getLng()));
        t.setDelay(0);
        // t.setTimeWindow(random(predMaxTime, predMaxTime + predMaxTime / 3), random(maxTime, maxTime + maxTime / 3));

        predMaxTime = (int) (maxTime + 2 * t.dis);
        maxTime = (int) (maxTime + 5 * t.dis);// Math.max(maxTime, t.end);

        int k = 1;
        max = new Point(max.getLat() + k, +max.getLng() + k);// Math.max(t.getEndPlace().getLng(), t.getPoint().getLng());
        // min.getLat() = min.getLat() + k;
        // min.getLng() = min.getLng() + k;
        // max = new Point(min.getLat() + 10, min.getLng() + 10);
        return t;
    }

    private Point generatePoint() {
        count++;
        return new Point(random(min.getLat(), max.getLat()), random(min.getLng(), max.getLng()));
    }

    public void setMaxPoint(int x, int y) {
        max = new Point(x, y);
    }

    public void setMaxTime(int time) {
        predMaxTime = time;
        maxTime = time + 10;

        min = new Point(time / 2, time / 2);
        max = new Point(time, time);
    }

    public void setMinPoint(int x, int y) {
        min = new Point(x, y);
    }

    public static int random(Double start, Double end) {
        Random rand = new Random();
        if (start >= end)
            return (int) (start + rand.nextInt(start.intValue() - end.intValue() + 1));// FIXME
        else
            return (int) (start + rand.nextInt(end.intValue() - start.intValue() + 1));
    }
}