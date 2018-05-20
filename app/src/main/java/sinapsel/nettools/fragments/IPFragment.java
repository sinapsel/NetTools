package sinapsel.nettools.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import sinapsel.nettools.R;
import sinapsel.nettools.service.GetIPAddress;

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
        refreship = view.findViewById(R.id.refreshipbutton);
        iptext = view.findViewById(R.id.iptextview);
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
                String text = iptext.getText().toString();
                ClipData clipData = ClipData.newPlainText("IP",text);
                ClipboardManager clipboardManager = (ClipboardManager)getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getActivity().getApplicationContext(),"IP Copied: ".concat(text), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return view;
    }

}
