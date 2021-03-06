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

/**
 * @author sinapsel
 *
 * Socket Server class
 */
public abstract class SocketServer extends Thread {
    protected int HttpServerPORT;
    protected ServerSocket httpServerSocket;
    protected String static_content;

    protected ArrayList<String> msgLog = new ArrayList<>(40);
    /**
     * Protection from garbage cleaner
     */
    public ArrayList<Socket> SocketSaver = new ArrayList<>(15);

   /**
     * implement on fragment or activity to show server connection info
     */
    public abstract void showConnectInfo();
    /**
     * @param static_content responsing default value
     * @param PORT server port
     */
    SocketServer(String static_content, int PORT){
        super();
        this.static_content = static_content;
        HttpServerPORT = PORT;
    }

    public int getPORT(){
        return HttpServerPORT;
    }
    public ArrayList<String> getMsgLog() {
        return msgLog;
    }


    protected abstract class HttpResponseHandler implements Runnable {
        /**
         * implement on service to put outcome data
         * @param in buffered reader which is wrappering input stream of socket
         * @throws IOException so we have common try..catch block around call of this method
         */
        public abstract void readRequest(BufferedReader in) throws IOException;
        /**
         * implement on service to put outcome data
         * @param socket need for extracting different wrappers of output stream and log info
         * @throws IOException so we have common try..catch block around call of this method
         */
        public abstract void postResponse(Socket socket) throws IOException;

        /**
         * implement on service, changes will be shown with message handlers on UI
         */
        public abstract void commitLog();

        Socket socket;
        protected Headers headers;
        protected String content;
        protected String path;
        protected String lastLog = "";

        HttpResponseHandler(Socket socket) {
            this.socket = socket;
        }
    }
}