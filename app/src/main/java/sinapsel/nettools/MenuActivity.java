package sinapsel.nettools;

import sinapsel.nettools.fragments.FolderHttpServerFragment;
import sinapsel.nettools.fragments.IPFragment;
import sinapsel.nettools.fragments.PingFragment;
import sinapsel.nettools.fragments.QueryFragment;
import sinapsel.nettools.fragments.HTTPServerSingletonFragment;
import sinapsel.nettools.fragments.TracerouteFragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment ipFragment;
    Fragment pingFragment;
    Fragment scsFragment;
    Fragment trsFragment;
    Fragment qryFragment;
    Fragment fhsFragment;
    ImageView cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Fragments declaration
        ipFragment = new IPFragment();
        pingFragment = new PingFragment();
        scsFragment = new HTTPServerSingletonFragment();
        trsFragment = new TracerouteFragment();
        qryFragment = new QueryFragment();
        fhsFragment = new FolderHttpServerFragment();

        //Cat opens drawer
        cat = findViewById(R.id.catclick);
        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * If <- pressed on screen then show drawer else close
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Drawer menu selector
     * @param item selected item from menu
     * @return anyway true
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        cat.setVisibility(View.INVISIBLE);
        findViewById(R.id.clickmeannotation).setVisibility(View.INVISIBLE);
        if (id == R.id.nav_ip) { // i'd like to use switch here but don't want writing <<break>> every condition
            fragmentTransaction.replace(R.id.menucontainer, ipFragment);
        } else if (id == R.id.nav_ping) {
            fragmentTransaction.replace(R.id.menucontainer, pingFragment);
        } else if (id == R.id.nav_css) {
            fragmentTransaction.replace(R.id.menucontainer, scsFragment);
        } else if (id == R.id.nav_trsrt) {
            fragmentTransaction.replace(R.id.menucontainer, trsFragment);
        }
        else if(id == R.id.nav_fss){
            fragmentTransaction.replace(R.id.menucontainer, fhsFragment);
        } else if (id == R.id.nav_sendquery) {
            fragmentTransaction.replace(R.id.menucontainer, qryFragment);
        }
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
