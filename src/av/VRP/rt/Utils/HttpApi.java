package av.VRP.rt.Utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Artem on 10.04.2016.
 */
public class HttpApi {
    private static volatile HttpApi instance;
    private OkHttpClient client;

    private HttpApi() {
        client = new OkHttpClient();
    }

    public static HttpApi getInstance() {
        if (instance == null) {
            instance = new HttpApi();
        }
        return instance;
    }

    public String getContent(String url) {
        try {
            return run(url);
        } catch (IOException e) {
            e.printStackTrace();//FIXME
        }
        return null;
    }

    public String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return "";
        }
    }

    public InputStream getInputStream(String url) throws IOException {
        Request request = new Request.Builder().url(url)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().byteStream();
    }
}
