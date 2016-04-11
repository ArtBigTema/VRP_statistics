package av.VRP.rt.parser;

import av.VRP.rt.listener.VRPgeneratorListener;
import av.VRP.rt.substance.PointT;
import av.VRP.rt.substance.PointWithTime;

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
                    new FileReader("data/file" + n + ".txt"));
        } catch (IOException e) {
            e.printStackTrace();//FIXME
        }
    }

    public void setListener(VRPgeneratorListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        String line = "";
        int i = 0;
        int count = 2000;//max = 200 000
        StringBuilder sb = new StringBuilder();
        try {
            line = br.readLine();

            while (line != null ) {
                count--;
                i++;
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();

                listener.generated(PointWithTime.construct(line));
              //  listener.show(num, i + ":  " + line + "\n");
                //    this.sleep(2);
            }
            // String everything = sb.toString().replace("\"", "");
            //  writer.append(everything);
            //  writer.append(System.lineSeparator());

            listener.stoped(num);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {//FIXME
            try {
                br.close();
                //  writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}