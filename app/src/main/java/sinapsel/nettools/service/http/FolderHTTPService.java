package sinapsel.nettools.service.http;

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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import sinapsel.nettools.R;
import sinapsel.nettools.service.GetIPAddress;

public class FolderHTTPService extends Service {
    public static final int BARUPD = 753;
    public static final int LOGUPD = 491;
    private final String TAG = "FHSService";
    private String BASE_ROUTE;
    protected SocketServer httpServerThread;
    private String LastLog = "";
    protected int num = 0;

    private boolean isIMG;

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
                        .setContentTitle("laCAT Folder HTTP Server - (".concat(String.valueOf(num)).concat(")"))
                        .setContentText(BASE_ROUTE)
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
        Log.d(TAG, "onStartCommand()");
        if (intent != null) {
            if (intent.hasExtra("start")) {
                messageHandler = (Messenger) intent.getExtras().get("messenger");
                BASE_ROUTE = intent.getStringExtra("baseroute");
                smain();
            } else {
                if(messageHandler != null)
                    sendMessage(BARUPD, new Object[]{});
                Log.d(TAG, "stoping service");
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void smain() {
        Log.d(TAG, "thread started");
        httpServerThread = new SocketServer("", 7777) {
            @Override
            public void run(){
                showConnectInfo();
                try {
                    httpServerSocket = new ServerSocket(HttpServerPORT);
                    while (!this.isInterrupted()){
                        Socket socket = httpServerSocket.accept();
                        SocketSaver.add(socket);
                        new Thread(new HttpResponseHandler(socket) {
                            @Override
                            public void readRequest(BufferedReader in) throws IOException {
                                Log.d(TAG, "readRequest");
                                String firstLine = in.readLine();
                                Log.d(TAG, "firstLine: " + firstLine);
                                String method = firstLine.split(" ")[0];
                                path = firstLine.split(" ")[1];
                                if (path.isEmpty() || (path.charAt(path.length() - 1) == '/'))
                                    path += "/index.html";
                                path = path.split("\\?")[0];
                                Log.d(TAG, "Path: "+path);
                                String ext = path.split("\\.")[path.split("\\.").length - 1]
                                        .toUpperCase();
                                Log.d(TAG, "RAW Ext: " + ext);
                                lastLog = firstLine;
                                headers = new Headers();
                                content = "";
                                isIMG = false;
                                if (!(method.equals("GET") || method.equals("POST"))){
                                    headers.setHTTP_Status("ERR400");
                                    headers.setContent_type("HTML");
                                    content = "<h1>Error 400</h1> - bad request";
                                    headers.setLength(content.length());
                                }
                                else if (!Environment.getExternalStorageState().equals(
                                        Environment.MEDIA_MOUNTED)) {
                                    Log.d("FILEREADER", "SD-карта не доступна: " + Environment.getExternalStorageState());
                                    headers.setHTTP_Status("ERR403");
                                    headers.setContent_type("HTML");
                                    content = "<h1>Error 403</h1> - permission denied";
                                    headers.setLength(content.length());
                                }
                                else if (!(new File(Environment.getExternalStorageDirectory(), BASE_ROUTE.concat(path).
                                        replace(Environment.getExternalStorageDirectory().
                                                getAbsolutePath(), ""))).exists()) {
                                    headers.setHTTP_Status("ERR404");
                                    headers.setContent_type("HTML");
                                    content = "<h1>Error 404</h1> - not found";
                                    headers.setLength(content.length());
                                    Log.d(TAG, "NOT FOUND!");
                                }
                                else {
                                    headers.setHTTP_Status("OK200");
                                    boolean isMimed = false;
                                    for (MIME mime : MIME.values()){
                                        if (ext.equals(mime.name())){
                                            isMimed = true;
                                            Log.d(TAG, mime.name());
                                        }
                                    }
                                    if (!isMimed)
                                        ext = "OTHER";
                                    headers.setContent_type(ext);
                                    isIMG = Arrays.asList(new MIME[]{MIME.JPEG, MIME.JPG, MIME.PNG, MIME.GIF,
                                            MIME.ICO}).contains(headers.getContent_type());
                                }
                                Log.d(TAG, headers.toString());
                            }

                            @Override
                            public void postResponse(Socket socket) throws IOException {
                                lastLog += " from " + socket.getInetAddress().toString() + " at " + new Date().toString();
                                msgLog.add(lastLog);
                                File sdPath = Environment.getExternalStorageDirectory();
                                File sdFile = new File(sdPath, BASE_ROUTE.concat(path).
                                        replace(sdPath.getAbsolutePath(), ""));
                                if(isIMG){
                                    FileInputStream is = new FileInputStream(sdFile);
                                    OutputStream os = socket.getOutputStream();
                                    int a;
                                    headers.setLength(is.available());
                                    for (char c : headers.toString().toCharArray()) {
                                        os.write(c);
                                    }
                                    os.write('\r'); os.write('\n');
                                    while ((a = is.read()) > -1) {
                                        os.write(a);
                                    }
                                    os.flush();
                                    is.close();
                                    os.close();
                                }
                                else {
                                    PrintWriter pw = new PrintWriter(socket.getOutputStream());
                                    if(content.isEmpty()){
                                        BufferedReader br = new BufferedReader(new FileReader(sdFile));
                                        String str;
                                        StringBuilder sb = new StringBuilder();
                                        while ((str = br.readLine()) != null) {
                                            sb.append(str.concat("\n"));
                                        }
                                        br.close();
                                        content = sb.toString();
                                        Log.d(TAG, content);
                                    }
                                    headers.setLength(content.length());
                                    pw.write(headers.toString());
                                    pw.write("\r\n");
                                    pw.write(content);
                                    pw.close();
                                }
                            }
                            @Override
                            public void run() {
                                BufferedReader is;
                                try {
                                    is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    readRequest(is);
                                    postResponse(socket);
                                    commitLog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                finally {
                                    try{
                                        socket.close();
                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void commitLog() {
                                LastLog = lastLog;
                                num++;
                                sendNotification();
                                sendMessage(LOGUPD, getMsgLog().toArray());
                                Log.d(TAG, lastLog);
                            }
                        }).start();
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void showConnectInfo() {
                sendMessage(BARUPD, new Object[]{GetIPAddress.getIP().concat(":")
                        .concat(Integer.toString(getPORT()))});
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
