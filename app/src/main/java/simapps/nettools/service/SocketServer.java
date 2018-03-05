package simapps.nettools.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class SocketServer extends Thread {
    public String response;
    protected static final int HttpServerPORT = 8888;
    private HttpResponseThread httpResponseThread;
    private ServerSocket httpServerSocket;
    protected String msgLog;

    public abstract void commitLog();
    public abstract void showConnectInfo();

    public SocketServer(String html){
        super();
        response = html;
    }

    public void destruct() {
        if (httpResponseThread != null && httpResponseThread.isAlive())
            httpResponseThread.interrupt();
        try {
            httpServerSocket.close();
        } catch (IOException e) {
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
                                socket,
                                "It works!");
                httpResponseThread.start();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private class HttpResponseThread extends Thread {

        Socket socket;
        String h1;

        HttpResponseThread(Socket socket, String msg) {
            this.socket = socket;
            h1 = msg;
        }

        @Override
        public void run() {
            BufferedReader is;
            PrintWriter os;
            String request;


            try {
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                request = is.readLine();

                os = new PrintWriter(socket.getOutputStream(), true);

                os.print("HTTP/1.0 200" + "\r\n");
                os.print("Content type: text/html" + "\r\n");
                os.print("Content length: " + response.length() + "\r\n");
                os.print("\r\n");
                os.print(response + "\r\n");
                os.flush();


                msgLog += "Request of " + request
                        + " from " + socket.getInetAddress().toString() + "\n";
                socket.close();
                commitLog();
                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    System.out.println(msgLog);
                        loggerEdit.setText(msgLog);
                    }
                });*/


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}