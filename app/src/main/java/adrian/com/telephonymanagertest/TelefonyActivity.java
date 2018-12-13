package adrian.com.telephonymanagertest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellLocation;
import android.telephony.SignalStrength;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TelefonyActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.cell)
    TextView cellTv;
    @BindView(R.id.signal_strenght)
    TextView signalTv;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.service_state)
    TextView serviceTv;

    private static int REQUEST_ACCESS_FINE_LOCATION = 101;
    private static int REQUEST_ACCESS_COARSE_LOCATION = 102;
    private static int REQUEST_READ_PHONE_STATE = 103;
    private static int REQUEST_WRITE_EXTERNAL = 104;
    private static int REQUEST_WIFI_STATE = 105;
    private static int REQUEST_CHANGE_WIFI_STATE = 105;

    public static final String SIGNAL_EVENT = "signal_event";
    public static final String STATE_EVENT = "state_event";
    public static final String LOCATION_EVENT = "location_event";

    public static final String EXTRAS_STATE = "state";
    public static final String EXTRAS_LOCATION = "location";
    public static final String EXTRAS_SIGNAL = "signal";

    private TelephonyService mService;
    private boolean mBound;
    private MyLogAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MyLogAdapter();
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(SIGNAL_EVENT);
        intentFilter.addAction(STATE_EVENT);
        intentFilter.addAction(LOCATION_EVENT);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, intentFilter);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(TelefonyActivity.this, WifiActivity.class);
            startActivity(intent);
        });
        super.onResume();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TelephonyService.LocalBinder binder = (TelephonyService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenData();
    }

    private void listenData() {
        if (checkPermission()) {
            Intent intent = new Intent(this, TelephonyService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(SIGNAL_EVENT) && intent.getExtras() != null && intent.getExtras().containsKey(EXTRAS_SIGNAL)) {
                    String signal = intent.getStringExtra(EXTRAS_SIGNAL);
                    signalTv.setText(signal);
                    mAdapter.addItem(signal);
                    mAdapter.notifyDataSetChanged();
                } else if (intent.getAction().equals(STATE_EVENT) && intent.getExtras() != null && intent.getExtras().containsKey(EXTRAS_STATE)) {
                    String state = intent.getStringExtra(EXTRAS_STATE);
                    serviceTv.setText(state);
                    mAdapter.addItem(state);
                    mAdapter.notifyDataSetChanged();
                } else if (intent.getAction().equals(LOCATION_EVENT) && intent.getExtras() != null && intent.getExtras().containsKey(EXTRAS_LOCATION)) {
                    String location = intent.getStringExtra(EXTRAS_LOCATION);
                    cellTv.setText(location);
                    mAdapter.addItem(location);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private boolean checkPermission() {
        boolean permissionGranted = true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                    REQUEST_WIFI_STATE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                    REQUEST_CHANGE_WIFI_STATE);
        }

        return permissionGranted;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (resultCode == REQUEST_ACCESS_COARSE_LOCATION || resultCode == REQUEST_ACCESS_FINE_LOCATION || resultCode == REQUEST_READ_PHONE_STATE) {
                listenData();
            }
        }
    }
}
