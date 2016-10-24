package com.example.a37754.cl1;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by 37754 on 2016/10/21.
 */

public class Broadcastr extends BroadcastReceiver {
    private static boolean mIncomingFlag = false;
    private static String mIncomingNumber = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Dial number;
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            //这个是拨号的时候采用的到的，所以这里没用
        } else {
            //Get call;
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            switch (tManager.getCallState()) {
                //有电话打进来
                case TelephonyManager.CALL_STATE_RINGING:
                    //mIncomingNumber就是来电号码
                    mIncomingNumber = intent.getStringExtra("incoming_number");
                    Toast.makeText(context, mIncomingNumber,
                            Toast.LENGTH_LONG).show();
                    ColorLight.telIn = true;
                    ColorLight.telNum = mIncomingNumber;
                    Log.d(ColorLight.TAG, "incoming_number: " + mIncomingNumber);
                    SendStateC(true, mIncomingNumber);
                    break;
                default:
                    ColorLight.telIn = false;
                    SendStateC(false, "000");
                    break;
            }
        }
    }

    private void SendStateC(boolean telIn, String telNum) {
        Log.d(ColorLight.TAG, "SendStateC: " + telNum);
        LEDVM.setTel(telIn, telNum);
    }

}
