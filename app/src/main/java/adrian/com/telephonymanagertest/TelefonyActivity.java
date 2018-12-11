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
import android.telephony.CellLocation;
import android.telephony.SignalStrength;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TelefonyActivity extends AppCompatActivity implements MainPhoneListener {
    @BindView(R.id.cell)
    TextView cellTv;
    @BindView(R.id.signal_strenght)
    TextView signalTv;
    @BindView(R.id.service_state)
    TextView serviceTv;

    private static int REQUEST_ACCESS_FINE_LOCATION = 101;
    private static int REQUEST_ACCESS_COARSE_LOCATION = 102;
    private static int REQUEST_READ_PHONE_STATE = 103;

    Intent serviceIntent;

    public static final String SIGNAL_EVENT = "signal_event";
    public static final String STATE_EVENT = "state_event";
    public static final String LOCATION_EVENT = "location_event";

    public static final String EXTRAS_STATE = "state";
    public static final String EXTRAS_LOCATION = "location";
    public static final String EXTRAS_SIGNAL = "signal";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        listenData();
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(SIGNAL_EVENT);
        intentFilter.addAction(STATE_EVENT);
        intentFilter.addAction(LOCATION_EVENT);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    private void listenData() {
        if (checkPermission()) {
            serviceIntent = new Intent(TelefonyActivity.this, TelephonyService.class);
            startService(serviceIntent);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(SIGNAL_EVENT) && intent.getExtras() != null && intent.getExtras().containsKey(EXTRAS_SIGNAL)) {
                    String signal = intent.getStringExtra(EXTRAS_SIGNAL);
                    signalTv.setText(signal);
                } else if (intent.getAction().equals(STATE_EVENT) && intent.getExtras() != null && intent.getExtras().containsKey(EXTRAS_STATE)) {
                    String state = intent.getStringExtra(EXTRAS_STATE);
                    serviceTv.setText(state);
                } else if (intent.getAction().equals(LOCATION_EVENT) && intent.getExtras() != null && intent.getExtras().containsKey(EXTRAS_LOCATION)) {
                    String location = intent.getStringExtra(EXTRAS_LOCATION);
                    cellTv.setText(location);
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
        return permissionGranted;
    }

    @Override
    public void onSignalStrengthChanged(SignalStrength signalStrength) {
        signalTv.setText(signalStrength.toString());
    }

    @Override
    public void onServiceStateChanged(String state) {
        serviceTv.setText(state);
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        if (location instanceof GsmCellLocation) {
            cellTv.setText("Lac: " + ((GsmCellLocation) location).toString() + ", Cid:" + ((GsmCellLocation) location).getCid());
        } else if (location instanceof CdmaCellLocation) {
            cellTv.setText("Lat:" + ((CdmaCellLocation) location).getBaseStationLatitude() + ", Long:" + ((CdmaCellLocation) location).getBaseStationLongitude());
        }
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
