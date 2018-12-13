package adrian.com.telephonymanagertest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class TelephonyService extends Service implements MainPhoneListener {
    private static TelephonyManager tm;
    Runnable runnable = this::getTelephonyInfo;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        TelephonyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TelephonyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Handler().post(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        Log.d("LOG1", message);
        Intent intent = new Intent(eventName);
        intent.putExtra(extraName, message);
        Utils.appendLog(message + "\n");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
