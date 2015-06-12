package co.gounplugged.unpluggeddroid.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.zeromq.ZMQ;

/**
 * Created by Marvin Arnold on 12/06/15.
 */
public class EdgenetClientService extends Service {
    private final static String TAG = "HydraServer";
    private APIThread mAPIThread;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        mAPIThread = new APIThread("5567", "unplugged");
        Log.d(TAG, "execute");
        mAPIThread.execute();
        return 0;
    }


    @Override
    public void onDestroy() {
       Log.d(TAG, "onDestroy");
    }

    class APIThread extends AsyncTask<String, Void, String> {

        public final String apiPort;
        public final String msg;

        private ZMQ.Context ctx;
        private ZMQ.Socket socket;

        public APIThread(String apiPort, String msg) {
            super();
            this.apiPort = apiPort;
            this.msg = msg;
            Log.d(TAG, "created");
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "doInBackground");
            ZMQ.Context ctx = ZMQ.context(1);

            Log.d(TAG, "Connecting to HydraServer");

            ZMQ.Socket socket = ctx.socket(ZMQ.REQ);
            socket.connect("tcp://127.0.0.1:" + apiPort);
            Log.d(TAG, "Connected to HydraServer");
            socket.send(msg.getBytes(ZMQ.CHARSET), 0);

            byte[] resp = socket.recv(0);
            String response = new String(resp, ZMQ.CHARSET);
            Log.d(TAG, "Received response " + response);
            socket.close();
            ctx.term();

            return response;
        }
    }
}
