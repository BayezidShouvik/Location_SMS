package com.shouvik.tryprojectalarmgps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shouvik on 06-Aug-16.
 */
public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    protected String messageBody;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Geocoder geocoder = new Geocoder(GPS_Service.this, Locale.ENGLISH);

                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("");
                        for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(",");
                        }
                        //strReturnedAddress.append(returnedAddress.getLocality()).append(",");
                        strReturnedAddress.append(returnedAddress.getAdminArea()).append(",");
                        strReturnedAddress.append(returnedAddress.getCountryName()).append(".");
                        messageBody = strReturnedAddress.toString();

                        Intent i = new Intent("location_update");
                        i.putExtra("coordinates", messageBody);
                        sendBroadcast(i);

                        /*Intent i2 = new Intent("my.action.string");
                        i2.putExtra("extra", messageBody);
                        sendBroadcast(i2);*/
                        //t.append(messageBody);

                        //t.append("\nFound Address");
                    } else {
                        // t.append("Inside Else");
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    //t.append("Inside Catch");
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                        Toast.LENGTH_SHORT).show();

            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, listener);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager!=null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
