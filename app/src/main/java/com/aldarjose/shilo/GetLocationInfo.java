package com.aldarjose.shilo;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;

public class GetLocationInfo {

    /*
    ///0
private LocationManager mLocationManager;
    private double longitude, latitude;
    TextView mTextView = findViewById(R.id.postTextView);

    ///1
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(mLocationManager.NETWORK_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        loc_func(location);

    ///2

     private void loc_func(Location location){
        try{
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(latitude, longitude,1);
            String adminArea = addresses.get(0).getAdminArea();
            mTextView.setText("Location: " + adminArea);

        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error"+e,Toast.LENGTH_SHORT).show();
        }
    }

    */


}
