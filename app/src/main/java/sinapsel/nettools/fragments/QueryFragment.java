package sinapsel.nettools.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "";
                if (raw.isChecked())
                    type="RAW";
                if (get.isChecked())
                    type="GET";
                if (post.isChecked())
                    type="POST";

                new SocketClient(ip.getText().toString(), headers.getText().toString(),
                            body.getText().toString(), type, portfield.getText().toString()){
                    @Override
                    public void commit(){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                output.setText(answ);
                            }
                        });
                    }
                }.start();
            }
        });
        return view;
    }

}
