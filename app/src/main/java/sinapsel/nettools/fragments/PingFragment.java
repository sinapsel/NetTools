package sinapsel.nettools.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import sinapsel.nettools.R;
import sinapsel.nettools.service.IPContainer;
import sinapsel.nettools.service.Ping;

public class PingFragment extends Fragment {
    EditText pingip;
    Button pingbutton;
    SeekBar seekbar;
    TextView packnum;
    ProgressBar pb;
    EditText pingoutput;
    private Bundle savedState = null;
    String out;
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
        pb = view.findViewById(R.id.pBarPing);
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
                pb.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        out = (Ping.ping(seekbar.getProgress() + 1, new IPContainer(pingip.getText().toString())));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setVisibility(View.GONE);
                                pingoutput.setText(out);
                            }
                        });
                    }
                }).start();

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
