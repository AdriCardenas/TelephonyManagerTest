package adrian.com.telephonymanagertest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

public class TelephonyService extends Service implements MainPhoneListener {
    private static TelephonyManager tm;
    Runnable runnable = this::getTelephonyInfo;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Handler().post(runnable);
        return START_STICKY;
    }

    private void getTelephonyInfo() {
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        CustomPhoneStateListener listener = new CustomPhoneStateListener(this);
        tm.listen(listener, PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_SERVICE_STATE
                | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onSignalStrengthChanged(SignalStrength signalStrength) {
        sendMessage(signalStrength.toString(), TelefonyActivity.SIGNAL_EVENT, TelefonyActivity.EXTRAS_SIGNAL);
    }

    @Override
    public void onServiceStateChanged(String state) {
        sendMessage(state, TelefonyActivity.STATE_EVENT, TelefonyActivity.EXTRAS_STATE);
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        String message = "";
        if (location instanceof GsmCellLocation) {
            message = "Lac: " + ((GsmCellLocation) location).getLac() + ", Cid:" + ((GsmCellLocation) location).getCid();
        } else if (location instanceof CdmaCellLocation) {
            message = "Lat:" + ((CdmaCellLocation) location).getBaseStationLatitude() + ", Long:" + ((CdmaCellLocation) location).getBaseStationLongitude();
        }
        sendMessage(message, TelefonyActivity.LOCATION_EVENT, TelefonyActivity.EXTRAS_LOCATION);
    }

    private void sendMessage(String message, String eventName, String extraName) {
        Intent intent = new Intent(eventName);
        intent.putExtra(extraName, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
