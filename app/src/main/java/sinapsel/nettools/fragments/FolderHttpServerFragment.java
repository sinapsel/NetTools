package sinapsel.nettools.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import sinapsel.nettools.R;
import sinapsel.nettools.service.GetIPAddress;
import sinapsel.nettools.service.http.FolderHTTPService;
import sinapsel.nettools.service.http.SingletonHTTPServerService;

import static android.app.Activity.RESULT_OK;

public class FolderHttpServerFragment extends Fragment {
    CompoundButton switcher;
    private static final int RES = 1;
    public ListView loggerEdit;
    public TextView sockservconinfo;
    EditText editroute;
    public Handler messageHandler = new FolderHttpServerFragment.MessageHandler();
    public FolderHttpServerFragment() {
        super();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Permissions are required!", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_folder_http_server, container, false);
        switcher = view.findViewById(R.id.toggleButton);
        loggerEdit = view.findViewById(R.id.servsocklog);
        sockservconinfo = view.findViewById(R.id.sockservconnectinfo);
        editroute = view.findViewById(R.id.route);

        if (!(ActivityCompat.checkSelfPermission(getActivity().
                getApplicationContext(), Manifest.permission.
                READ_EXTERNAL_STORAGE) == ActivityCompat.
                checkSelfPermission(getActivity().
                        getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && ActivityCompat.checkSelfPermission(getActivity().
                getApplicationContext(), Manifest.permission.
                WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            } else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }

        }

        if (isMyServiceRunning(FolderHTTPService.class)){
            switcher.setChecked(true);
            sockservconinfo.setText( GetIPAddress.getIP().concat(":").concat(Integer.toString(7777)));
        }

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //starting service
                    getActivity().startService(new Intent(getActivity(),
                            FolderHTTPService.class).putExtra("start", 1)
                            .putExtra("baseroute", editroute.getText().toString())
                            .putExtra("messenger", new Messenger(messageHandler)));
                }else {
                    //stopping service
                    getActivity().stopService(new Intent(getActivity(), FolderHTTPService.class));
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, new String[] {});
                    loggerEdit.setAdapter(adapter);
                }

            }
        });

        //directory picker
        editroute.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, RES);
            }
        });


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
        switch (requestCode) {
            case RES:
                if (resultCode == RESULT_OK) {
                    String FilePath = data.getData().getPath();
                    StringBuilder sb = new StringBuilder();
                    String[] FP = FilePath.split("/");
                    for (int i = 0; i < FP.length - 1; ++i){
                        sb.append(FP[i].concat("/"));
                    }
                    editroute.setText(sb.toString());
                }
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case SingletonHTTPServerService.BARUPD:
                    FolderHttpServerFragment.this.sockservconinfo.setText(((Bundle)message.obj).getString("text"));
                    break;
                case SingletonHTTPServerService.LOGUPD:
                    String[] list = ((Bundle)message.obj).getStringArray("text");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, list != null ? list : new String[0]);
                    loggerEdit.setAdapter(adapter);
                    //HTTPServerSingletonFragment.this.loggerEdit.setText(((Bundle)message.obj).getString("text"));
                    break;
            }

        }
    }

}
