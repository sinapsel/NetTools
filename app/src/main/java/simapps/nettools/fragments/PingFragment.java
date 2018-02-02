package simapps.nettools.fragments;

import android.content.Intent;
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
    private Bundle savedState = null;
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
        if(savedInstanceState !=null && savedState == null){
            savedState = savedInstanceState.getBundle("STATE");
        }
        if(savedState != null){
            pingip.setText(savedState.getString("IP"));
            seekbar.setProgress(Integer.parseInt(savedState.getString("numOfPacks")));
            pingoutput.setText(savedState.getString("IPLog"));
        }
        savedState = null;
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
    private Bundle saveState(){
        Bundle state = new Bundle();
        state.putString("IP", pingip.getText().toString());
        state.putString("numOfPacks", Integer.toString(seekbar.getProgress()));
        state.putString("IPLog", pingoutput.getText().toString());
        return state;
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        savedState = saveState();
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBundle("STATE", (savedState != null) ? savedState : saveState());
    }
}
