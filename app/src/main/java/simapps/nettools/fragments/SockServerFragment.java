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
import java.util.logging.Logger;

import simapps.nettools.R;
import simapps.nettools.service.GetIPAddress;
import simapps.nettools.service.SocketServer;

public class SockServerFragment extends Fragment {
    CompoundButton switcher;
    SocketServer httpServerThread;
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

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    httpServerThread = new SocketServer(){
                        @Override
                        public void commitLog(){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loggerEdit.setText(msgLog);
                                }
                            });
                            Logger.getLogger(SockServerFragment.class.getName()).info(msgLog);
                        }
                        public void showConnectInfo(){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sockservconinfo.setText(GetIPAddress.getIP().concat(":").concat(Integer.toString(HttpServerPORT)));
                                }
                            });
                        }
                    };

                    if(!httpServerThread.isAlive())
                        httpServerThread.start();
                    //else httpServerThread.run();
                }
                else {
                    sockservconinfo.setText("");
                    httpServerThread.destruct();
                    httpServerThread.interrupt();
                    System.out.println(httpServerThread.isInterrupted());
                }
            }
        });

        return view;
    }
}
