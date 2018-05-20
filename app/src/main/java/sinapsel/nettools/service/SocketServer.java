/**
 * @author sinapsel
 *
 * Singleton HTTP Socket Server class
 */
package sinapsel.nettools.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Date;

public abstract class SocketServer extends Thread {
    protected String response;
    protected static final int HttpServerPORT = 8888;
    private HttpResponseThread httpResponseThread;
    protected String request;
    private ServerSocket httpServerSocket;
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

    public abstract String[] prepareResponse();

    protected SocketServer(String html){
        super();
        response = html;
    }

    public void destruct() {
        if (httpResponseThread != null && httpResponseThread.isAlive())
            httpResponseThread.interrupt();
        try {
            httpServerSocket.close();
        } catch (NullPointerException e) {
            System.out.println("Already closed");
        }
        catch (IOException e){
            e.printStackTrace();
        }
        this.interrupt();
    }

    @Override
    public void run() {
        //Socket socket = null;
        showConnectInfo();
        /*getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sockservconinfo.setText(GetIPAddress.getIP().concat(":").concat(Integer.toString(HttpServerPORT)));
            }
        });
        */
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private class HttpResponseThread extends Thread {

        Socket socket;

        HttpResponseThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedReader is;
            PrintWriter os;
            String request;


            try {
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while (!(request = is.readLine()).equals(""))
                    sb.append(request.concat("\n"));
                request = sb.toString();
                SocketServer.this.request = request;
                os = new PrintWriter(socket.getOutputStream(), true);

                String[] resp = prepareResponse();
                for (String i : resp){
                    os.print(i.concat("\r\n"));
                }
                os.flush();

                lastLog = "Request " + new Date().toString() + ":\n" +request
                        + " from " + socket.getInetAddress().toString() + "\n";
                msgLog.add(lastLog);
                socket.close();
                commitLog();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}