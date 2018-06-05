package sinapsel.nettools;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.Arrays;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Button close = findViewById(R.id.closehelp);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static int[] HELPSCREENID = {R.drawable.hellocat, R.drawable.ip, R.drawable.ping,
                R.drawable.traceroute, R.drawable.sing_server, R.drawable.fold_server, R.drawable.query};

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
            super();
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int INDEX = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.d("HELP", "ID: " + INDEX);
            View rootView = inflater.inflate(R.layout.fragment_help, container, false);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageViewHelp);
            try{
                imageView.setImageResource(HELPSCREENID[INDEX - 1]);
                SeekBar seekBar = getActivity().findViewById(R.id.seekBar3);
                seekBar.setProgress(INDEX - 1);
            }
            catch (ArrayIndexOutOfBoundsException e){
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 7;
        }
    }
}
