package sinapsel.nettools.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import sinapsel.nettools.R;

public class StartFragment extends Fragment {

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        ImageView iv = view.findViewById(R.id.imageView2);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawerLayout)getActivity().findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
            }
        });
        return view;
    }


}
