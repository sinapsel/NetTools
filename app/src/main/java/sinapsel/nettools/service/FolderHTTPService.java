package sinapsel.nettools.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import sinapsel.nettools.R;

public class FolderHTTPService extends Service {
    public static final int BARUPD = 753;
    public static final int LOGUPD = 491;
    private final String TAG = "FHSService";
    private String BASE_ROUTE;
    protected SocketServer httpServerThread;
    private String LastLog = "";
    protected int num = 0;

    private Messenger messageHandler;
    public FolderHTTPService() {
        super();
    }


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
                        .setContentTitle("NetTools Folder HTTP Server - (".concat(String.valueOf(num)).concat(")"))
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
                BASE_ROUTE = intent.getStringExtra("baseroute");
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
        httpServerThread = new SocketServer("") {
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

            @Override
            public String[] prepareResponse() {
                String R = request.split("\r\n")[0].split(" ")[1];
                if ((R.charAt(R.length() - 1)) == '/')
                    R += "index.html";
                ArrayList<String> al = new ArrayList<>(4);
                String src;
                StringBuilder sb = new StringBuilder();
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    Log.d("FILEREADER", "SD-карта не доступна: " + Environment.getExternalStorageState());
                    return new String[]{""};
                }
                if(!request.split("\r\n")[0].split(" ")[0].equals("GET")){
                    src = "Error 406 - BAD Query";
                    al.add("HTTP/1.0 406");
                    al.add("Content type: text/html");
                    al.add("Content length:" + src.length());
                    al.add("");
                    al.add(src);
                    return Arrays.copyOf(al.toArray(), al.size(), String[].class);
                }
                File sdPath = Environment.getExternalStorageDirectory();
                File sdFile = new File(sdPath, BASE_ROUTE.concat(R).replace(sdPath.getAbsolutePath(), ""));
                try {
                    BufferedReader br = new BufferedReader(new FileReader(sdFile));
                    String str = "";
                    while ((str = br.readLine()) != null) {
                        sb.append(str);
                    }
                    src = sb.toString();
                    al.add("HTTP/1.0 200");
                    al.add("Content type: text/html");
                    al.add("Content length:" + src.length());
                    al.add("");
                    al.add(src);
                } catch (FileNotFoundException e) {
                        src = "Error 404 - File Not Found";
                        al.add("HTTP/1.0 404");
                        al.add("Content type: text/html");
                        al.add("Content length:" + src.length());
                        al.add("");
                        al.add(src);
                } catch (IOException e) {
                    src = "Error 403 - Access Denied";
                    al.add("HTTP/1.0 403");
                    al.add("Content type: text/html");
                    al.add("Content length:" + src.length());
                    al.add("");
                    al.add(src);
                }

                return Arrays.copyOf(al.toArray(), al.size(), String[].class);
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
