package simapps.nettools.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import simapps.nettools.R;
import simapps.nettools.service.GetIPAddress;

public class IPFragment extends Fragment {
    Button refreship;
    TextView iptext;
    public IPFragment() {
        super();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ip, container, false);
        refreship = (Button) view.findViewById(R.id.refreshipbutton);
        iptext = (TextView) view.findViewById(R.id.iptextview);

        iptext.setText(GetIPAddress.getIP());
        refreship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iptext.setText(GetIPAddress.getIP());
            }
        });
        iptext.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){

                return true;
            }
        });

        return view;
    }

}
