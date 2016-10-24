package com.example.a37754.cl1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ColorLight extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String ACTION_CONNECT = "ColorLight_CONNECT";
    private final String DefaultTitle = "Color Light";
    private final int Request_DeviceScanActivity = 2;

    static final String TAG = "MainActivity_TAG_DEBUG";

    TelephonyManager tm;
    TextView str;
    Handler mHandler;
    LEDView mLEDView;
    public static LEDVM mLEDVM;
    public static Point WindowPoint;

    static String telNum = "";
    static boolean telIn = false;

    static boolean IsBLEconnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_color_light);
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
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        registerBoradcastReceiver();

        LEDVM.Initiate(this);

        IsBLEconnected = false;
        mLEDView = (LEDView) this.findViewById(R.id.mLEDView);
        mLEDView.setBackgroundResource(R.drawable.b1);
        mHandler = new Handler();
        new Thread(new Runnable(){
            @Override
            public void run() {
                int i = (int) ((mLEDView.Cycle_Time + mLEDView.Release_Time)/49);
                while (i-- != 0) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            mLEDView.invalidate();
                        }
                    });
                }

            }
        }).start();

        //Thread to generate all the states
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        mHandler.post(new Runnable(){
                            @Override
                            public void run() {
                                mLEDView.invalidate();
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_CONNECT)) {
                int status = intent.getIntExtra("CONNECT_STATUC", 0);
                if (status == 0) {
                    getActionBar().setTitle(DefaultTitle + "...已断开连接");
                    //finish(); 不用结束
                    IsBLEconnected = false;
                } else {
                    getActionBar().setTitle(DefaultTitle + "...已连接");
                    IsBLEconnected = true;
                }
            }
        }
    };

    public static synchronized void char6_display(String str, byte[] data, String uuid) {
        if (uuid.equals(DeviceScanActivity.UUID_CHAR6)) {
            //get str from BLE
        } else {
            //should not reach here
        }
    }

    // 注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_CONNECT);
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
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
        getMenuInflater().inflate(R.menu.global, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode) {
            case Request_DeviceScanActivity:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String mac_addr = bundle.getString("mac_addr");
                    String char_uuid = bundle.getString("char_uuid");
                    Log.d(TAG, "mac_addr=" + mac_addr);
                    Log.d(TAG, "char_uuid" + char_uuid);
                    Toast.makeText(this, "成功连接设备", Toast.LENGTH_LONG);
                }
                break;
        }
    }

    public static void SendToLED(String x) {
        if (IsBLEconnected) {
            //DeviceScanActivity.mLEDService.writeChar6(x);
        }
        Log.i(TAG, "Not Send:" + x);
    }
}
