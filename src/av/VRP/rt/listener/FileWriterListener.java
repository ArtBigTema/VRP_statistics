package av.VRP.rt.listener;

/**
 * Created by Artem on 10.04.2016.
 */
public interface FileWriterListener {
    void onSuccess(int count);

    void onError();
}