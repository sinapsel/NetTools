package sinapsel.nettools.service.http;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

import sinapsel.nettools.R;
import sinapsel.nettools.service.GetIPAddress;

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
                .setContentTitle("laCAT Singleton HTTP Server - (".concat(String.valueOf(num)).concat(")"))
                .setContentText(LastLog.split("\r\n")[0])
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
        httpServerThread = new SocketServer(html, 8888) {
            @Override
            public void commitLog() {
                LastLog = getLastLog();
                num++;
                sendNotification();
                sendMessage(LOGUPD, getMsgLog().toArray());
                Log.d(TAG, getLastLog());
            }

            @Override
            public void showConnectInfo() {
                sendMessage(BARUPD, new Object[]{GetIPAddress.getIP().concat(":").concat(Integer.toString(getPORT()))});
            }

            @Override
            public void readRequest(BufferedReader in) throws IOException {
                Log.d(TAG, "readRequest()");
                String firstLine = in.readLine();
                Log.d(TAG, "Request: " + firstLine);
                lastLog = firstLine;
                String method = firstLine.split(" ")[0];
                String path = firstLine.split(" ")[1];
                if (!(method.equals("GET") || method.equals("POST"))){
                    headers.setHTTP_Status("ERR400");
                    headers.setContent_type("HTML");
                    content = "<h1>Error 400</h1> - bad request";
                    headers.setLength(content.length());
                }
                else{
                    headers.setHTTP_Status("OK200");
                    headers.setContent_type("HTML");
                    headers.setLength(content.length());
                }
            }

            @Override
            public void postResponse(Socket socket) throws IOException{
                lastLog += " from " + socket.getInetAddress().toString() + " at " + new Date().toString();
                msgLog.add(lastLog);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                headers.setLength(content.length());
                pw.write(headers.toString());
                pw.write("\r\n\r\n");
                pw.write(content);
                pw.close();
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

