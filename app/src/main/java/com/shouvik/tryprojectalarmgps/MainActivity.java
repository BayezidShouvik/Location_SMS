package com.shouvik.tryprojectalarmgps;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private Button startFbutton,sendButton,locationButton,setButton,showButton,stopFbutton,startRButton,stopRButton,useButton;
    private TextView textViewLoc;

    private PendingIntent pendingIntent;

    private BroadcastReceiver broadcastReceiver;
    private String messageBody1;

    public static final String DEFAULT="N/A";

    //boolean myHasSentMessage;

    boolean flag = false;
    boolean flag2 = false;

    int clickedStartService=0;
    int clickedStopService=0;
    int clickedStartPeriodicSms=0;
    int clickedStopPeriodicSms=0;
    int clickedSendSmsNow=0;
    int clickedSetNumber=0;
    int clickedShowNumber=0;

    private Sensor sensor;
    private SensorManager sm;
    private long lastUpdate;

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver==null){
            broadcastReceiver=new BroadcastReceiver() {
                int i=0;
                int t=0;
                @Override
                public void onReceive(Context context, Intent intent) {
                    messageBody1= (String) intent.getExtras().get("coordinates");
                    if(messageBody1!=null)
                    {
                        textViewLoc.setText("");
                        textViewLoc.setText(messageBody1);
                        i++;
                        if(i==1)
                        {

                            SharedPreferences sharedPreferences=getSharedPreferences("MyData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.remove("setMessageBody");
                            editor.apply();

                            editor.putString("setMessageBody",messageBody1.toString());
                            editor.apply();
                            //editor.commit();

                            //sendSMS();
                        }
                        if(i==20)
                            i=0;
                    }
                    /*if(messageBody1!=null && t==5)
                    {
                        textViewLoc.setText("");
                        textViewLoc.setText(messageBody1);
                        t=1;
                    }*/
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver!=null)
        {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startFbutton = (Button) findViewById(R.id.startButton);
        stopFbutton = (Button) findViewById(R.id.stopButton);
        startRButton= (Button) findViewById(R.id.startRepeatingButton);
        stopRButton= (Button) findViewById(R.id.stopRepeatingButton);
        sendButton= (Button) findViewById(R.id.sendButton);
        //locationButton= (Button) findViewById(R.id.locationButton);
        setButton= (Button) findViewById(R.id.setButton);
        showButton= (Button) findViewById(R.id.showButton);
        useButton= (Button) findViewById(R.id.useHow);
        textViewLoc = (TextView) findViewById(R.id.textViewLoc);

        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

        useButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent11=new Intent(MainActivity.this,HowToUse.class);
                startActivity(intent11);
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedShowNumber++;

                SharedPreferences sharedPreferences=getSharedPreferences("MyData", Context.MODE_PRIVATE);
                String phoneNumber=sharedPreferences.getString("setPhoneNumber",DEFAULT);

                if(phoneNumber.equals(DEFAULT))
                {
                    Toast.makeText(MainActivity.this,"No Phone Number was Found Set",Toast.LENGTH_SHORT).show();
                }
                else if(phoneNumber.length()==0)
                {
                    Toast.makeText(MainActivity.this,"No Phone Number was Set Previous Time",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this,""+phoneNumber+" was Found Set as PhoneNumber",Toast.LENGTH_LONG).show();
                }
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedSetNumber++;

                Intent intent=new Intent(MainActivity.this,SetActivity.class);
                startActivity(intent);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedSendSmsNow++;
                    if(messageBody1!=null)
                        {
                            sendSMS();
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Location Not Fetched Yet.Try Again Later", Toast.LENGTH_SHORT).show();
            }
        });

        startRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedStartPeriodicSms++;

                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    int interval = 2*60*1000;

                    manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+(2*60*1000), interval, pendingIntent);
                    // Toast.makeText(getApplicationContext(), "Alarm Set", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Starting the Process of Sending Periodic SMS", Toast.LENGTH_SHORT).show();

            }
        });


        stopRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedStopPeriodicSms++;

                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(pendingIntent);
                    clickedStartPeriodicSms--;
                    Toast.makeText(getApplicationContext(), "Stopping the Process of Sending Periodic SMS", Toast.LENGTH_SHORT).show();


            }
        });

        /*locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageBody1!=null)
                {
                    Toast.makeText(getApplicationContext(),"Current Location:-\n"+messageBody1, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Fetching Location....", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        sm= (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener((SensorEventListener) MainActivity.this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(!runtime_oermissions())
            enable_buttons();
    }

    private void enable_buttons() {
        startFbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedStartService++;

                Toast.makeText(getApplicationContext(),"Starting Service to Fetch Location", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(getApplicationContext(),GPS_Service.class);
                startService(i);
            }
        });
        stopFbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedStopService++;


                Toast.makeText(getApplicationContext(),"Stoping Service to Fetch Location", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(getApplicationContext(),GPS_Service.class);
                stopService(i);

            }
        });
    }

    private boolean runtime_oermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }
            else{
                runtime_oermissions();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
        /*if(event.values[0]>18)
        {
            if(messageBody1!=null)
                {
                    //Toast.makeText(getApplicationContext(),""+messageBody1, Toast.LENGTH_SHORT).show();
                    sendSMS();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Location Not Fetched Yet.Try Again Later", Toast.LENGTH_SHORT).show();
                }

        }*/
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 10) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            if(messageBody1!=null)
            {
                //Toast.makeText(getApplicationContext(),""+messageBody1, Toast.LENGTH_SHORT).show();
                sendSMS();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Location Not Fetched Yet.Try Again Later", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //Log.d("Test", "Long press!");
            //Toast.makeText(getBaseContext(), "Long Press Volume Down", Toast.LENGTH_SHORT).show();
            if(messageBody1!=null)
                //Toast.makeText(getApplicationContext(),""+messageBody1, Toast.LENGTH_LONG).show();
            sendSMS();
            else
            {
                Toast.makeText(getApplicationContext(),"Location Yet Not Fetched.Try Again Later", Toast.LENGTH_SHORT).show();
            }
            flag = false;
            flag2 = true;
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            event.startTracking();
            if (flag2 == true) {
                flag = false;
            } else {
                flag = true;
                flag2 = false;
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            event.startTracking();
            if (flag) {
                Log.d("Test", "Short");
            }
            flag = true;
            flag2 = false;
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

   /* public void sendSMS()
    {
        String phoneNumber = "01711903773";
        String message2 = "I am in danger.Need your help.Follow my location";
        String message=message2+":"+messageBody1;
        //Used to detect when the sent text message was/was not delivered.
        String actionStr = "android.provider.Telephony.SMS_DELIVERED";
        final Intent theDeliveredIntent = new Intent(actionStr);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, theDeliveredIntent, 0);
        SmsManager smsManager = SmsManager.getDefault();

        //When the SMS has been delivered, Notify the user with a simple toast.
        this.registerReceiver(new BroadcastReceiver()
        {
            //Called when the away message has gotten to the destination.
            @Override
            public void onReceive(Context arg0, Intent arg1)
            {
                int resultCode = getResultCode();

                if(resultCode == Activity.RESULT_OK)
                {
                    Toast.makeText(this.getBaseContext(), "Away message delivered.", Toast.LENGTH_SHORT).show();
                }
                else if(resultCode == Activity.RESULT_CANCELED)
                {
                    Toast.makeText(myMainActivity.getBaseContext(), "Away message could NOT be delivered.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new IntentFilter(actionStr));

        //Send the text message
        if(!myHasSentMessage)
        {
            smsManager.sendTextMessage(phoneNumber, null, message, deliveredPendingIntent, null);
            Toast.makeText(MainActivity.this,
                    "SMS sent to "+phoneNumber, Toast.LENGTH_SHORT).show();
        }

    }*/

    protected void sendSMS() {
        SharedPreferences sharedPreferences=getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String phoneNumber=sharedPreferences.getString("setPhoneNumber",DEFAULT);


        if(phoneNumber.equals(DEFAULT))
        {
            Toast.makeText(this,"No Phone Number was Found",Toast.LENGTH_SHORT).show();
        }
        else
        {
            String message2 = "I am in danger.Need your help.Follow my location";
            String message=message2+":"+messageBody1;


            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            try {
                Toast.makeText(MainActivity.this,
                        "SMS sent to "+phoneNumber, Toast.LENGTH_LONG).show();
            }
            catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this,
                        "Failed to send SMS, please try again later.", Toast.LENGTH_SHORT).show();
            }

        }
        //String phoneNumber = "01711903773";

    }
}
