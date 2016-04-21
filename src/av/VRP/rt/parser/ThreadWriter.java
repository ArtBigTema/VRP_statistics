package av.VRP.rt.parser;

import av.VRP.rt.Utils.*;
import av.VRP.rt.listener.FileWriterListener;

import java.io.*;

/**
 * Created by Artem on 10.04.2016.
 */
public class ThreadWriter extends Thread implements Runnable {
    private FileWriterListener listener;
    private InputStream in;
    private PrintWriter writer;

    public ThreadWriter(String url, int n) {
        Files.deleteDirectory();
        Files.createDirectory();

        try {
            in = HttpApi.getInstance().getInputStream(url);
            writer = Files.getWriter(url, n);
        } catch (IOException e) {
            Log.e(e.getMessage());
            e.printStackTrace();//fixme
        }
    }

    public void setListener(FileWriterListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        try {
            br.readLine();// skip first stroke in csv
            String line = br.readLine();

            while (line != null) {
                writer.append(line.replace("\"", ""));
                writer.append(System.lineSeparator());
                line = br.readLine();
            }

            writer.flush();
        } catch (IOException e) {
            if (listener != null) {
                listener.onError();
            }
            Log.e(e.getMessage());
            e.printStackTrace();
        } finally {//FIXME
            writer.close();
            if (listener != null) {
                listener.onSuccess();
            }
            try {
                br.close();
            } catch (IOException e) {
                if (listener != null) {
                    listener.onError();
                }
                Log.e(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}