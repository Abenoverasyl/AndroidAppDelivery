package ru.ereke.appsalem;

import android.app.Activity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

public class WellcomeActivity extends Activity implements View.OnClickListener {

    private TextView tvUserName;
    private Button btnDelivered;
    private Button btnError;
    private String userName;
    private String userCode1C;
    private TextView tvEnabledGPS;
    private TextView tvEnabledNet;
    private TextView tvLocationNet;
    private String locationData = "";
    private LocationManager locationManager;
    private Button btnVideoViewWellcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // присваиваем значения
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        btnDelivered = (Button) findViewById(R.id.btnDelivered);
        btnError = (Button) findViewById(R.id.btnError);
        btnVideoViewWellcome = (Button) findViewById(R.id.btnVideoViewWellcome);

        // берем от МainActivity имя пользователя
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        userCode1C = intent.getStringExtra("userCode1C");

        tvUserName.setText(userName);
        btnDelivered.setOnClickListener(this);
        btnError.setOnClickListener(this);
        btnVideoViewWellcome.setOnClickListener(this);
        // location
        tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }


    // когда пользователь нажмет кнопку, смотрим какую кнопку нажал и даем дейтвия
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v.getId() == R.id.btnVideoViewWellcome) {
            intent = new Intent(this, VideoActivity.class);
            startActivity(intent);
        } else if (locationData.length() > 2) {
            switch (v.getId()) {
                case R.id.btnDelivered:
                    intent = new Intent(this, DeliveredActivity.class);
                    break;
                case R.id.btnError:
                    intent = new Intent(this, ErrorActivity.class);
                    break;
            }
            intent.putExtra("userCode1C", userCode1C);
            intent.putExtra("locationData", locationData);
            startActivity(intent);
        } else {
            Toast.makeText(WellcomeActivity.this,"Местоположения не найдено!",Toast.LENGTH_LONG ).show();
        }
    }
    // получения Геоданные
    //********************************************************************************************
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 1, 3, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 1, 3,
                locationListener);
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
//                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
//                tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            locationData = formatLocation(location);
//            tvLocationGPS.setText(locationData);
            tvLocationNet.setText("Найдено");
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            locationData = formatLocation(location);
//            tvLocationNet.setText(locationData);
            tvLocationNet.setText("Найдено");
        }
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "la:%1$.8f,lo:%2$.8f,d:%3$tF,t:%3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private void checkEnabled() {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };

    // *******************************************************************************
    @Override
    protected void onStart() {
        super.onStart();
        String photoFilePath = "sdcard/MobDelImages";
        deleteDirectory(new File(photoFilePath));
    }

    static public boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }
}
