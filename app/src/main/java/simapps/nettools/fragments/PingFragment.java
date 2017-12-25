package simapps.nettools.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import simapps.nettools.R;
import simapps.nettools.service.Ping;

public class PingFragment extends Fragment {
    EditText pingip;
    Button pingbutton;
    SeekBar seekbar;
    TextView packnum;
    EditText pingoutput;
    public PingFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_ping, container, false);
        pingip = view.findViewById(R.id.pingip);
        pingbutton = view.findViewById(R.id.pingbutton);
        seekbar = view.findViewById(R.id.seekBar);
        packnum = view.findViewById(R.id.packnumnum);
        pingoutput = view.findViewById(R.id.pingoutput);
        packnum.setText(getString(R.string.number_of_packets).concat(Integer.toString(seekbar.getProgress() + 1)));
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                packnum.setText(getString(R.string.number_of_packets).concat(Integer.toString(seekBar.getProgress() + 1)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pingoutput.setText(getString(R.string.pingingwait));
                pingoutput.setText(Ping.ping(seekbar.getProgress() + 1, pingip.getText().toString()));
            }
        });
        return view;
    }

}
