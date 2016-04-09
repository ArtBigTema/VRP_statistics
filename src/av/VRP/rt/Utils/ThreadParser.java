package av.VRP.rt.Utils;

import av.VRP.rt.generator.VRPgeneratorListener;

import java.io.*;

/**
 * Created by Artem on 09.04.2016.
 */
public class ThreadParser extends Thread implements Runnable {//FIXME all
    public BufferedReader br;//FIXME remove public
    public PrintWriter writer;
    public VRPgeneratorListener listener;
    public int num = 0;

    //Date/Time,"Lat","Lon","Base"
    public ThreadParser(int n) {
        num = n;
        // new File("Files").mkdir();
        try {
            // writer = new PrintWriter("Files/file" + n + ".txt", "UTF-8");
            br = new BufferedReader(
                    new FileReader("data/file" + n + ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
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

            while (line != null && count > 0) {
                count--;
                i++;
                sb.append(line);
                sb.append(System.lineSeparator());
                // System.out.println(line);
                line = br.readLine();
                listener.show(num, i + ":  " + line + "\n");
                this.sleep(2);
            }
            // String everything = sb.toString().replace("\"", "");
            //  writer.append(everything);
            //  writer.append(System.lineSeparator());

            listener.stoped(num);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

    public PrintWriter getWriter(int i) throws FileNotFoundException, UnsupportedEncodingException {
        return new PrintWriter("Files/file" + i + ".txt", "UTF-8");
    }
}