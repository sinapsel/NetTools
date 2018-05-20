package sinapsel.nettools.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import sinapsel.nettools.R;
import sinapsel.nettools.service.Ping;
import sinapsel.nettools.service.SocketClient;
import sinapsel.nettools.service.TracerouteWithPing;

public class QueryFragment extends Fragment {
    EditText ip, headers, body, output, portfield;
    RadioButton raw, get, post;
    Button send;
    ProgressBar pBar;

    public QueryFragment() {super();}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_querier, container, false);
        ip = view.findViewById(R.id.ipurlsend);
        headers = view.findViewById(R.id.queryheaders);
        body = view.findViewById(R.id.querycontent);
        output = view.findViewById(R.id.queryoutput);
        raw = view.findViewById(R.id.radioButton);
        get = view.findViewById(R.id.radioButton2);
        post = view.findViewById(R.id.radioButton3);
        send = view.findViewById(R.id.sendquery);
        portfield = view.findViewById(R.id.portquery);
        pBar = view.findViewById(R.id.pbarQuery);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("");
                String type = "";
                if (raw.isChecked())
                    type="RAW";
                if (get.isChecked())
                    type="GET";
                if (post.isChecked())
                    type="POST";

                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
                if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected()))
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();

                startProgressBar();
                new SocketClient(ip.getText().toString(), headers.getText().toString(),
                            body.getText().toString(), type, portfield.getText().toString()){
                    @Override
                    public void commit(){

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                output.setText(answ);
                                stopProgressBar();
                            }
                        });

                    }
                }.start();
            }
        });
        return view;
    }

    public void startProgressBar() {
        pBar.setVisibility(View.VISIBLE);
    }
    public void stopProgressBar() {
        pBar.setVisibility(View.GONE);
    }

}
