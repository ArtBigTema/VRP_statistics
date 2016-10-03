package av.VRP.rt;

import av.VRP.rt.listener.FileWriterListener;
import av.VRP.rt.parser.ThreadParser;
import av.VRP.rt.generator.VRPStaticData;
import av.VRP.rt.listener.VRPgeneratorListener;
import av.VRP.rt.parser.ThreadWriter;
import av.VRP.rt.substance.Point;
import av.VRP.rt.substance.PointT;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Artem on 09.04.2016.
 */
public class Main implements FileWriterListener {
    public MainFrame frame;//FIXME remove public

    public StringBuilder sb;
    public int n = 0;

    public Main() {
        frame = new MainFrame();
        sb = new StringBuilder();


        ThreadWriter wr = new ThreadWriter(this);
        wr.run();
    }

    public static void main(String[] args) {
        new Main();

    }


    @Override
    public void started() {

    }

    @Override
    public void stoped() {

    }
}