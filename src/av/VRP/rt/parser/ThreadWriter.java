package av.VRP.rt.parser;

import av.VRP.rt.Utils.HttpApi;
import av.VRP.rt.listener.FileWriterListener;
import av.VRP.rt.listener.VRPgeneratorListener;

import java.io.*;

/**
 * Created by Artem on 10.04.2016.
 */
public class ThreadWriter extends Thread implements Runnable {//FIXME all
    public int num = 200_000;//count in part
    public InputStream in;//FIXME remove public
    public FileWriterListener listener;

    public ThreadWriter(String url) {
        try {
            in = HttpApi.getInstance().getInputStream(url);
        } catch (IOException e) {
            e.printStackTrace();//fixme
        }
        new File("Files").mkdir();
    }

    public void setListener(FileWriterListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        listener.started();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String line = "";
        int i = 0;

        try {
            br.readLine();
            line = br.readLine();
            while (line != null) {
                int count = num;
                i++;
                StringBuilder sb = new StringBuilder();

                while (line != null && count > 0) {
                    count--;
                    sb.append(line);
                    line = br.readLine();
                }
                String everything = sb.toString().replace("\"", "");
                PrintWriter writer = getWriter(i);
                writer.append(everything);
                writer.append(System.lineSeparator());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {//FIXME
            listener.stoped();
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public PrintWriter getWriter(int i) throws FileNotFoundException, UnsupportedEncodingException {
        return new PrintWriter("Files/file" + i + ".txt", "UTF-8");
    }
}