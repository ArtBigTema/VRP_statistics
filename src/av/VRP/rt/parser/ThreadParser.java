package av.VRP.rt.parser;

import av.VRP.rt.Utils.*;
import av.VRP.rt.listener.VRPgeneratorListener;

import java.io.*;

/**
 * Created by Artem on 09.04.2016.
 */
public class ThreadParser extends Thread implements Runnable {
    private BufferedReader br;
    private VRPgeneratorListener listener;

    public ThreadParser(int i) {
        try {
            br = new BufferedReader(Files.getReader(i));
        } catch (IOException e) {
            Log.e(e.getMessage());
            e.printStackTrace();//FIXME
        }
    }

    public void setListener(VRPgeneratorListener listener) {
        this.listener = listener;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        try {
            String line = br.readLine();

            while (line != null) {// && count != 0) {//FIXME
                if (!line.isEmpty() && !line.contains(",0,0,0,0,")) {//fixme

                    listener.generated(line);
                    //    listener.show(num, i + ":  " + line + "\n");//FIXME remove
                    // listener.show(Utils.strToArray(line, ","));
                } else {
                    Log.e(line);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            Log.e(e.getMessage());
            e.printStackTrace();
        } finally {//FIXME
            try {
                br.close();
                listener.stoped(0);
            } catch (IOException e) {
                Log.e(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}