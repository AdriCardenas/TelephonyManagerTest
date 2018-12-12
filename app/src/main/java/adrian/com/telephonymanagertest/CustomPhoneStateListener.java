package adrian.com.telephonymanagertest;

import android.telephony.*;

public class CustomPhoneStateListener extends PhoneStateListener {
    private final MainPhoneListener listener;

    public CustomPhoneStateListener(MainPhoneListener listener) {
        this.listener = listener;
    }

    private void serviceStateToString(int serviceState) {
        switch (serviceState) {
            case ServiceState.STATE_IN_SERVICE:
                listener.onServiceStateChanged("In service");
                break;
            case ServiceState.STATE_OUT_OF_SERVICE:
                listener.onServiceStateChanged("Out of service");
                break;
            case ServiceState.STATE_EMERGENCY_ONLY:
                listener.onServiceStateChanged("State emergency");
                break;
            case ServiceState.STATE_POWER_OFF:
                listener.onServiceStateChanged("Power off");
                break;
            default:
                listener.onServiceStateChanged("Unknown State");
                break;
        }
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        serviceStateToString(serviceState.getState());
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        super.onCellLocationChanged(location);
        listener.onCellLocationChanged(location);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        listener.onSignalStrengthChanged(signalStrength);
    }
}
