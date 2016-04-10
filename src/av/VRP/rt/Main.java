package av.VRP.rt;

import av.VRP.rt.parser.ThreadParser;
import av.VRP.rt.generator.VRPStaticData;
import av.VRP.rt.listener.VRPgeneratorListener;
import av.VRP.rt.substance.Point;
import av.VRP.rt.substance.PointT;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Artem on 09.04.2016.
 */
public class Main implements VRPgeneratorListener {
    public MainFrame frame;//FIXME remove public

    public StringBuilder sb;
    public int n = 0;

    public Main() {
        frame = new MainFrame();
        sb = new StringBuilder();

        VRPStaticData data = new VRPStaticData();
        data.setListener(this);

        ThreadParser parser1 = new ThreadParser(1);
        parser1.setListener(this);
        ThreadParser parser2 = new ThreadParser(2);
        parser2.setListener(this);
        ThreadParser parser3 = new ThreadParser(3);
        parser3.setListener(this);
        ThreadParser parser4 = new ThreadParser(4);
        parser4.setListener(this);

        parser1.start();
        parser2.start();
        parser3.start();
        parser4.start();

    }

    public static void main(String[] args) {
        ListFrame lf = new ListFrame();
        lf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {//FIXME
                new Main();
            }
        });

    }

    @Override
    public void generated(PointT t) {

    }

    @Override
    public void generated(Point t) {

    }

    @Override
    public void started() {

    }

    @Override
    public void stoped(int count) {
        n++;
        if (n > 3) {
            frame.showData(sb.toString());
        }
    }

    @Override
    public void show(String row) {
        frame.showData(row);
    }

    @Override
    public void show(int n, String row) {
        //  sb.append(System.currentTimeMillis() + "-" + n + "." + row);

        frame.showData(n + "." + row);
    }
}