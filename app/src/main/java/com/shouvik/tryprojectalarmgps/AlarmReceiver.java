package com.shouvik.tryprojectalarmgps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by Shouvik on 18-Sep-16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String DEFAULT="N/A";

    @Override
    public void onReceive(Context context, Intent intent) {


        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String phoneNumber=sharedPreferences.getString("setPhoneNumber",DEFAULT);
        String message1=sharedPreferences.getString("setMessageBody",DEFAULT);

        /*if(phoneNumber.equals(DEFAULT)||message1.equals(DEFAULT))
        {
                Toast.makeText(context,"No Data was Found",Toast.LENGTH_SHORT).show();
        }*/

        if(message1.equals(DEFAULT))
        {
            Toast.makeText(context,"No Location was Found",Toast.LENGTH_SHORT).show();
        }
        else
        {
            String message2 = "I am in danger.Need your help.Follow my location";
            String message = message2 + ":" + message1;

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            try {
                Toast.makeText(context,
                        "SMS sent to "+phoneNumber, Toast.LENGTH_SHORT).show();
            }
            catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context,
                        "Failed to send SMS, please try again later.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}