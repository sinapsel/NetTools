package sinapsel.nettools.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sinapsel.nettools.R;
import sinapsel.nettools.service.TracerouteContainer;
import sinapsel.nettools.service.Traceroute;

public class TracerouteFragment extends Fragment {

    public static final String tag = "TraceroutePing";
    public static final String INTENT_TRACE = "INTENT_TRACE";

    private Button buttonLaunch;
    private EditText editTextPing;
    private ProgressBar progressBarPing;
    private ListView listViewTraceroute;
    private TraceListAdapter traceListAdapter;

    private Traceroute tracerouteWithPing;
    private final int maxTtl = 40;

    private List<TracerouteContainer> traces;

    public TracerouteFragment() {
        super();
    }

    /**
     * onCreate, init main components from view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_traceroute, container, false);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_trace);

        this.tracerouteWithPing = new Traceroute(TracerouteFragment.this);
        this.traces = new ArrayList<TracerouteContainer>();

        this.buttonLaunch = (Button) view.findViewById(R.id.buttonLaunch);
        this.editTextPing = (EditText) view.findViewById(R.id.editTextPing);
        this.listViewTraceroute = (ListView) view.findViewById(R.id.listViewTraceroute);
        this.progressBarPing = (ProgressBar) view.findViewById(R.id.progressBarPing);
        initView();
        return view;
    }

    /**
     * initView, init the main view components (action, adapter...)
     */
    private void initView() {
        buttonLaunch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPing.getText().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.no_text), Toast.LENGTH_SHORT).show();
                } else {
                    traces.clear();
                    traceListAdapter.notifyDataSetChanged();
                    startProgressBar();
                    tracerouteWithPing.executeTraceroute(editTextPing.getText().toString(), maxTtl);
                }
            }
        });

        traceListAdapter = new TraceListAdapter(getActivity().getApplicationContext());
        listViewTraceroute.setAdapter(traceListAdapter);
    }

    public void refreshList(TracerouteContainer trace) {
        final TracerouteContainer fTrace = trace;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                traces.add(fTrace);
                traceListAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * The adapter of the listview (build the views)
     */
    public class TraceListAdapter extends BaseAdapter {

        private Context context;

        public TraceListAdapter(Context c) {
            context = c;
        }

        public int getCount() {
            return traces.size();
        }

        public TracerouteContainer getItem(int position) {
            return traces.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            // first init
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.item_list_trace, null);

                TextView textViewNumber = (TextView) convertView.findViewById(R.id.textViewNumber);
                TextView textViewIp = (TextView) convertView.findViewById(R.id.textViewIp);
                TextView textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
                ImageView imageViewStatusPing = (ImageView) convertView.findViewById(R.id.imageViewStatusPing);

                // Set up the ViewHolder.
                holder = new ViewHolder();
                holder.textViewNumber = textViewNumber;
                holder.textViewIp = textViewIp;
                holder.textViewTime = textViewTime;
                holder.imageViewStatusPing = imageViewStatusPing;

                // Store the holder with the view.
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            TracerouteContainer currentTrace = getItem(position);

            if (position % 2 == 1) {
                convertView.setBackgroundResource(R.color.c4_even);
            } else {
                convertView.setBackgroundResource(R.color.c4_odd);
            }

            if (currentTrace.isSuccessful()) {
                holder.imageViewStatusPing.setImageResource(R.drawable.check);
            } else {
                holder.imageViewStatusPing.setImageResource(R.drawable.cross);
            }

            holder.textViewNumber.setText(position + "");
            holder.textViewIp.setText(currentTrace.getHostname() + " (" + currentTrace.getIp() + ")");
            holder.textViewTime.setText(currentTrace.getMs() + "ms");

            return convertView;
        }

        // ViewHolder pattern
        class ViewHolder {
            TextView textViewNumber;
            TextView textViewIp;
            TextView textViewTime;
            ImageView imageViewStatusPing;
        }
    }


    public void startProgressBar() {
        progressBarPing.setVisibility(View.VISIBLE);
    }
    public void stopProgressBar() {
        progressBarPing.setVisibility(View.GONE);
    }

}
