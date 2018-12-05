package kkimmg.guesscadence;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * センサーデータの受け取り、および保存サービス
     */
    private StoreSensorDataService mService;
    /**
     * 接続中
     */
    private boolean mBound = false;
    /**
     * サービスへの接続
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            StoreSensorDataService.LocalBinder binder = (StoreSensorDataService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    /** セッション */
    private RideSession rideSession = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // ここから
        onCreateMine(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(), kkimmg.guesscadence.StoreSensorDataService.class);
        BikeInfo bikeInfo = BikeInfoProvider.getDefaultBikeInfo(getApplicationContext());
        intent.putExtra(StoreSensorDataService.BIKEINFO_KEY, bikeInfo);
        // アクティビティによらず継続するためここで開始
        startService(intent);
        //
        boolean started = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (started) {
            Log.i("ACTIVITY", "SERVICE Started Successfully.");
        }
    }

    /** 現在のバイク情報 */
    private BikeInfo currentBikeInfo = null;

    /** 現在のセッション */
    private RideSession currentRideSession = null;

    /**
     * OnCreateのオリジナル部分
     */
    private void onCreateMine(Bundle savedInstanceState) {
        Button btnStartSession = findViewById(R.id.btnStartSession);
        Button btnEndSession = findViewById(R.id.btnEndSession);
        Button btnBikeList = findViewById(R.id.btnBikeList);

        btnStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mService != null && mBound) {
                    rideSession = new RideSession();
                    rideSession.setInitialBikeInfo(currentBikeInfo);
                    rideSession.setBikeInfo(currentBikeInfo);
                    rideSession = mService.startSession(rideSession);
                    currentBikeInfo = rideSession.getBikeInfo();
                }
            }
        });
        btnEndSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mService != null && mBound) {
                    mService.endSession();
                    currentBikeInfo = BikeInfoProvider.getDefaultBikeInfo(NavigationDrawerActivity.this);
                }
            }
        });

        List<BikeInfo> bikeList = BikeInfoProvider.getBikeInfoList(this);
        if (bikeList.size() == 0) {
            BikeInfo info = new BikeInfo();
            currentBikeInfo = BikeInfoProvider.insertBikeInfo(this, info);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            editor.putLong(BikeInfo.DEFAULT_BIKEINFO_KEY, currentBikeInfo.getId());
            editor.apply();
        } else {
            currentBikeInfo = BikeInfoProvider.getDefaultBikeInfo(this);
        }
}

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
