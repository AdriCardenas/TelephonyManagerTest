package adrian.com.telephonymanagertest;

import android.telephony.CellLocation;
import android.telephony.SignalStrength;

public interface MainPhoneListener {
    void onSignalStrengthChanged(SignalStrength signalStrength);
    void onServiceStateChanged(String state);
    void onCellLocationChanged(CellLocation location);
}
