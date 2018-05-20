package sinapsel.nettools.fragments;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import sinapsel.nettools.R;
import sinapsel.nettools.service.SingletonHTTPServerService;
import sinapsel.nettools.service.GetIPAddress;

public class HTTPServerSingletonFragment extends Fragment {
    CompoundButton switcher;
    public ListView loggerEdit;
    public TextView sockservconinfo;
    Button edithtmlbttn;
    String html = "<html>\n<head>\n<title>Compact webserver</title>\n</head>\n<body>\n<h1>Seems to be working)</h1>\n</body>\n</html>";
    private Bundle savedState = null;
    public Handler messageHandler = new MessageHandler();

    public HTTPServerSingletonFragment() {
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

        if (isMyServiceRunning(SingletonHTTPServerService.class)){
            switcher.setChecked(true);
            sockservconinfo.setText( GetIPAddress.getIP().concat(":").concat(Integer.toString(8888)));
        }

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    getActivity().startService(new Intent(getActivity(),
                            SingletonHTTPServerService.class).putExtra("start", 1)
                            .putExtra("html", html)
                            .putExtra("messenger", new Messenger(messageHandler)));
                }else {
                    getActivity().stopService(new Intent(getActivity(), SingletonHTTPServerService.class));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, new String[] {});
                    loggerEdit.setAdapter(adapter);
                }

            }
        });

        edithtmlbttn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), html_edit.class);
                intent.putExtra("html", html);
                startActivityForResult(intent, 1);
            }
        });

        if(savedState != null){
            switcher.setChecked(savedState.getBoolean("Switch"));
            //loggerEdit.setText(savedState.getString("Textlog"));
            if(switcher.isChecked()){
                switcher.callOnClick();
            }
        }
        savedState = null;

        return view;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        html = data.getStringExtra("html");
        getActivity().stopService(new Intent(getActivity(), SingletonHTTPServerService.class));
        getActivity().startService(new Intent(getActivity(),
                SingletonHTTPServerService.class).putExtra("start", 1)
                .putExtra("html", html)
                .putExtra("messenger", new Messenger(messageHandler)));
    }

    private Bundle saveState(){
        Bundle state = new Bundle();
        //state.putString("Textlog", loggerEdit.getText().toString());
        state.putBoolean("Switch", switcher.isChecked());
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

    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case SingletonHTTPServerService.BARUPD:
                    HTTPServerSingletonFragment.this.sockservconinfo.setText(((Bundle)message.obj).getString("text"));
                    break;
                case SingletonHTTPServerService.LOGUPD:
                    String[] list = ((Bundle)message.obj).getStringArray("text");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, list);
                    loggerEdit.setAdapter(adapter);
                    //HTTPServerSingletonFragment.this.loggerEdit.setText(((Bundle)message.obj).getString("text"));
                    break;
            }

        }
    }
}
