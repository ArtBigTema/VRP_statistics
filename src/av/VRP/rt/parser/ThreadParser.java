package av.VRP.rt.parser;

import av.VRP.rt.Utils.Log;
import av.VRP.rt.Utils.Utils;
import av.VRP.rt.listener.VRPgeneratorListener;
import av.VRP.rt.substance.Trip;

import java.io.*;

/**
 * Created by Artem on 09.04.2016.
 */
public class ThreadParser extends Thread implements Runnable {//FIXME all
    public BufferedReader br;//FIXME remove public
    public VRPgeneratorListener listener;
    public int num = 0;

    //Date/Time,"Lat","Lon","Base"
    public ThreadParser(int n) {
        num = n;

        try {
            br = new BufferedReader(
                    new FileReader("Files/file" + n + ".txt"));
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
        String line = "";
        int i = 0;
        int count = 12;//max = 200 000
        StringBuilder sb = new StringBuilder();
        try {
            line = br.readLine();

            while (line != null && count != 0) {
                if (!line.isEmpty()) {
                    count--;
                    i++;
                    sb.append(line);
                    sb.append(System.lineSeparator());

                    listener.generated(Trip.construct(line));
                    //    listener.show(num, i + ":  " + line + "\n");//FIXME remove
                    listener.show(Utils.strToArray(line, ","));

                    //    this.sleep(2);
                }
                line = br.readLine();
            }
            // String everything = sb.toString().replace("\"", "");
            //  writer.append(everything);
            //  writer.append(System.lineSeparator());

        } catch (IOException e) {
            Log.e(e.getMessage());
            e.printStackTrace();
        } finally {//FIXME
            try {
                br.close();
                // this.interrupt();
                listener.stoped(num);
                //  writer.close();
            } catch (IOException e) {
                Log.e(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}