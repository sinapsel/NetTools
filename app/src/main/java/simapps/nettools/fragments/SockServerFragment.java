package simapps.nettools.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import simapps.nettools.R;
import simapps.nettools.service.GetIPAddress;

public class SockServerFragment extends Fragment {
    CompoundButton switcher;
    ServerSocket httpServerSocket;
    HttpServerThread httpServerThread;
    String msgLog;
    EditText loggerEdit;
    TextView sockservconinfo;
    public SockServerFragment() {
        super();
    }
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sock_server, container, false);
        switcher = view.findViewById(R.id.toggleButton);
        loggerEdit = view.findViewById(R.id.servsocklog);
        sockservconinfo = view.findViewById(R.id.sockservconnectinfo);
        httpServerThread = new HttpServerThread();

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    httpServerThread = new HttpServerThread(){};
                    if(!httpServerThread.isAlive())
                        httpServerThread.start();
                    //else httpServerThread.run();
                }
                else{
                    try {
                        httpServerSocket.close();
                        sockservconinfo.setText("");
                    } catch (IOException e) {
                        System.out.println(e.toString());
                    }
                    httpServerThread.destruct();
                    httpServerThread.interrupt();
                    System.out.println(httpServerThread.isInterrupted());
                }
            }
        });

        return view;
    }




    private class HttpServerThread extends Thread {

        static final int HttpServerPORT = 8888;
        HttpResponseThread httpResponseThread;

        void destruct(){
            if(httpResponseThread != null && httpResponseThread.isAlive())
                httpResponseThread.interrupt();
            this.interrupt();
        }
        @Override
        public void run() {
            Socket socket = null;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sockservconinfo.setText(GetIPAddress.getIP().concat(":").concat(Integer.toString(HttpServerPORT)));
                }
            });
           try {
                httpServerSocket = new ServerSocket(HttpServerPORT);

                while(!this.isInterrupted()){
                    socket = httpServerSocket.accept();

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


    }

    private class HttpResponseThread extends Thread {

        Socket socket;
        String h1;

        HttpResponseThread(Socket socket, String msg){
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

                String response =
                        "<html><head></head>" +
                                "<body>" +
                                "<h1>" + h1 + "</h1>" +
                                "</body></html>";

                os.print("HTTP/1.0 200" + "\r\n");
                os.print("Content type: text/html" + "\r\n");
                os.print("Content length: " + response.length() + "\r\n");
                os.print("\r\n");
                os.print(response + "\r\n");
                os.flush();



                msgLog += "Request of " + request
                        + " from " + socket.getInetAddress().toString() + "\n";
                socket.close();
                System.out.println(msgLog);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loggerEdit.setText(msgLog);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
