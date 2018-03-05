package simapps.nettools.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    Button edithtmlbttn;
    String html = "<html><head></head><body><h1>Seems to be working)</h1></body></html>";
    private Bundle savedState = null;


    public SockServerFragment() {
        super();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_sock_server, container, false);
        switcher = view.findViewById(R.id.toggleButton);
        loggerEdit = view.findViewById(R.id.servsocklog);
        sockservconinfo = view.findViewById(R.id.sockservconnectinfo);
        edithtmlbttn = view.findViewById(R.id.goedit);


        if(savedInstanceState !=null && savedState == null){
            savedState = savedInstanceState.getBundle("STATE");
        }

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    httpServerThread = new SocketServer(html){
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

        edithtmlbttn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), html_edit.class);
                intent.putExtra("html", html);
                startActivity(intent);
            }
        });

        Intent intent = getActivity().getIntent();
        if(intent.hasExtra("html"))
            html = intent.getStringExtra("html");

        if(savedState != null){
            switcher.setChecked(savedState.getBoolean("Switch"));
            loggerEdit.setText(savedState.getString("Textlog"));
            if(switcher.isChecked()){
                switcher.callOnClick();
            }
        }
        savedState = null;

        return view;
    }



    private Bundle saveState(){
        Bundle state = new Bundle();
        state.putString("Textlog", loggerEdit.getText().toString());
        state.putBoolean("Switch", switcher.isChecked());
        return state;
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        savedState = saveState();
        httpServerThread.destruct();
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBundle("STATE", (savedState != null) ? savedState : saveState());
    }
}
