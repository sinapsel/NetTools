/**
 * @author sinapsel
 *
 * Singleton HTTP Socket Server class
 */
package sinapsel.nettools.service.http;


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;

public abstract class SocketServer extends Thread {
    private int HttpServerPORT;
    private HttpResponseThread httpResponseThread;
    private ServerSocket httpServerSocket;
    private static final String TAG = "SOCKETSERVER";

    protected Headers headers;
    protected String content;
    protected String path = "";

    protected String lastLog = "";
    protected ArrayList<String> msgLog = new ArrayList<>(40);
    /**
     * implement on fragment or activity to show server log up
     */
    public abstract void commitLog();
    /**
     * implement on fragment or activity to show server connection info
     */
    public abstract void showConnectInfo();

    public abstract void readRequest(BufferedReader in) throws IOException;
    public abstract void postResponse(Socket socket) throws IOException;

    SocketServer(String html, int PORT){
        super();
        this.content = html;
        HttpServerPORT = PORT;
    }

    public void destruct() {
        if (httpResponseThread != null && httpResponseThread.isAlive())
            httpResponseThread.interrupt();
        try {
            httpServerSocket.close();
        } catch (NullPointerException e) {
            Log.d(TAG, "Already closed");
        }
        catch (IOException e){
            e.printStackTrace();
        }
        this.interrupt();
    }

    @Override
    public void run() {
        showConnectInfo();
        try {
            httpServerSocket = new ServerSocket(HttpServerPORT);
            while (!this.isInterrupted()) {
                Socket socket = httpServerSocket.accept();

                httpResponseThread =
                        new HttpResponseThread(
                                socket);
                httpResponseThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getLastLog() {
        return lastLog;
    }
    public int getPORT(){
        return HttpServerPORT;
    }
    public ArrayList<String> getMsgLog() {
        return msgLog;
    }


    private class HttpResponseThread extends Thread {

        Socket socket;

        HttpResponseThread(Socket socket) {
            this.socket = socket;
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
        }
    }
}