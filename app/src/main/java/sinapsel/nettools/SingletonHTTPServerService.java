package sinapsel.nettools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;

import java.util.ArrayList;
import java.util.Arrays;

import sinapsel.nettools.service.GetIPAddress;
import sinapsel.nettools.service.SocketServer;

public class SingletonHTTPServerService extends Service {
    public static final int BARUPD = 753;
    public static final int LOGUPD = 491;
    private final String TAG = "HSSService";
    private String html;
    protected SocketServer httpServerThread;
    private String LastLog = "";
    protected int num = 0;

    private Messenger messageHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("ServiceStartArguments", 0x0000000a);//THREAD_PRIORITY_BACKGROUND
        thread.start();
        Log.d(TAG, "onCreate");
    }

    public void sendNotification() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hellocat, options);
        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("NetTools HTTP Server - (".concat(String.valueOf(num)).concat(")"))
                .setContentText(LastLog)
                .setOngoing(true)
                .setNumber(num)
                .setUsesChronometer(true)
                .setColor(0x0000FF00)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.hellocat)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(LastLog));
        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (httpServerThread != null)
            httpServerThread.destruct();
        assert httpServerThread != null;
        httpServerThread.interrupt();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(1);
        }
        Log.d(TAG, "onDestroy");
    }


    public SingletonHTTPServerService() {
        super();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent != null) {
            if (intent.hasExtra("start")) {
                messageHandler = (Messenger) intent.getExtras().get("messenger");
                html = intent.getStringExtra("html");
                smain();
            } else {
                if(messageHandler != null)
                    sendMessage(BARUPD,new Object[]{});
                Log.d(TAG, "killing");
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void smain() {
        Log.d(TAG, "thread started");
        httpServerThread = new SocketServer(html) {
            @Override
            public void commitLog() {
                LastLog = lastLog;
                num++;
                sendNotification();
                sendMessage(LOGUPD, msgLog.toArray());
                Log.d(TAG, lastLog);
            }

            @Override
            public void showConnectInfo() {
                sendMessage(BARUPD, new Object[]{GetIPAddress.getIP().concat(":").concat(Integer.toString(HttpServerPORT))});
            }
        };
        if (!httpServerThread.isAlive())
            httpServerThread.start();
        sendNotification();
    }

    public void sendMessage(int TARGET, Object[] text) {
        Message message = Message.obtain();
        Bundle b = new Bundle();
        switch (TARGET) {
            case BARUPD :
                message.what = BARUPD;
                b.putString("text", (String)text[0]);
                break;
            case LOGUPD :
                message.what = LOGUPD;
                b.putStringArray("text", Arrays.copyOf(text, text.length, String[].class));
                break;
        }

        message.obj = b;
        try {
            messageHandler.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

