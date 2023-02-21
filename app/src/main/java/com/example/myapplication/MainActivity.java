package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.myapplication.database.AccelDatabase;
import com.example.myapplication.model.AccelModel;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  SensorEventListener{

    ToggleButton gyroTBtn,accTBtn,proxiTbtn;
    TextView acc_x,acc_y,acc_z,gyro_x,gyro_y,gyro_z,proxi_value,dbentries,latlong;
    private static final String TAG = "MainActivity";
    private static final DecimalFormat df = new DecimalFormat("0.0000");
    SensorManager sensorManager;
    Sensor linear_acc,gyroscope,proximity;
    AccelDatabase accelDatabase;
    Button dbButton,getLocation;
    double latitude,longitude;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gyroTBtn = findViewById(R.id.toggleButton_gyr);
        accTBtn = findViewById(R.id.toggleButton_acc);
        proxiTbtn = findViewById(R.id.toggleButton_proxi);
//        getLocation = findViewById(R.id.getLocation);

        acc_x=findViewById(R.id.tv_accx);
        acc_y=findViewById(R.id.tv_accy);
        acc_z=findViewById(R.id.tv_accz);

        dbButton = findViewById(R.id.dbButton);

        gyro_x = findViewById(R.id.tv_gyx);
        gyro_y = findViewById(R.id.tv_gyy);
        gyro_z = findViewById(R.id.tv_gyz);
        dbentries = findViewById(R.id.db_entries);
//        latlong = findViewById(R.id.latlong);

        proxi_value = findViewById(R.id.tv_proxi);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        linear_acc = sensorManager.getDefaultSensor(Sensor.TY)

        accTBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    linear_acc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

                    if(linear_acc!=null){
                        sensorManager.registerListener((SensorEventListener) MainActivity.this,linear_acc,SensorManager.SENSOR_DELAY_NORMAL);
                        Toast.makeText(MainActivity.this, "Linear Acceleration Sesnor Started", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.i(TAG, "Acceleration Not Supported");
                    }
                }
                else{
                    stop(1);
                    Toast.makeText(MainActivity.this, "Linear Acceleration Sensor Stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });

        gyroTBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                    if(gyroscope!=null){
                        sensorManager.registerListener((SensorEventListener) MainActivity.this,gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
                        Toast.makeText(MainActivity.this, "Gyroscope Started", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.i(TAG, "Gyroscope Not Supported");
                    }
                }
                else{
                    stop(2);
                    Toast.makeText(MainActivity.this, "Gyroscope Sensor Stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });

        proxiTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                    if(proximity!=null){
                        sensorManager.registerListener((SensorEventListener) MainActivity.this,proximity,SensorManager.SENSOR_DELAY_NORMAL);
                        Toast.makeText(MainActivity.this, "Proximity Sensor Started", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.i(TAG, "Proximity Not Suported");
                    }
                }
                else{
                    stop(3);
                    Toast.makeText(MainActivity.this, "Proximity Sensor Stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Button CLicked", Toast.LENGTH_SHORT).show();
//                Log.i("MA", "onClick: ");
                List<AccelModel> myEntries = accelDatabase.accelDAO().getAccelList();
                String output = "";
                for(AccelModel m:myEntries){
                    output += "x:"+m.getX() + ", y:" + m.getY() + ", z:" + m.getZ() + "\n";
                }
                Log.i("MA", "Output:"+ output);
                dbentries.setText(output);
            }
        });


    }



    private void stop(int option){
        if(option ==1)
            sensorManager.unregisterListener((SensorEventListener) this,sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));

        else if(option ==2)
            sensorManager.unregisterListener((SensorEventListener) this,sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));

        else if(option ==3)
            sensorManager.unregisterListener((SensorEventListener) this,sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        Sensor sensor = sensorEvent.sensor;
        if(sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
            accelDatabase = AccelDatabase.getInstance(this);
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            Log.i(TAG, "onSensorChanged: Acceleration"+x+","+y+","+z);
            String x_f = Float.toString(Float.parseFloat(df.format(x)));
            String y_f = Float.toString(Float.parseFloat(df.format(y)));
            String z_f = Float.toString(Float.parseFloat(df.format(z)));
            acc_x.setText(x_f);
            acc_y.setText(y_f);
            acc_z.setText(z_f);

            AccelModel accelModel = new AccelModel(Float.parseFloat(x_f),Float.parseFloat(y_f),Float.parseFloat(z_f));
            accelDatabase.accelDAO().insert(accelModel);
        }
        else if(sensor.getType()== Sensor.TYPE_GYROSCOPE){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            Log.i(TAG, "onSensorChanged: Gyroscope"+x+","+y+","+z);
            gyro_x.setText(Float.toString(Float.parseFloat(df.format(x))));
            gyro_y.setText(Float.toString(Float.parseFloat(df.format(y))));
            gyro_z.setText(Float.toString(Float.parseFloat(df.format(z))));
//            gyro_y.setText(Float.toString(y));
//            gyro_z.setText(Float.toString(z));
        }

        else if(sensor.getType()==Sensor.TYPE_PROXIMITY){
            //DB Code
            float proxiValue = sensorEvent.values[0];
            Log.i(TAG, "onSensorChanged: Proximity"+proxiValue);
            proxi_value.setText(Float.toString(proxiValue));
        }

    }





    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}


    class MyLocationListerner implements LocationListener {


        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.i(TAG, String.valueOf(df.format(location.getLatitude())) + ", " + String.valueOf(df.format(location.getLongitude())));
            Log.i(TAG, String.valueOf(location.getTime()));

//            myName = findViewById(R.id.myName);
//            myAddress = findViewById(R.id.myAddress);

//            positionDatabase = PositionDatabase.getInstance(MainActivity.this);
//            String name = (myName.getText()).toString(); //"Rocky";
//            String address = (myAddress.getText()).toString(); //"He lives in IIITD";
////
//            PositionModel positionModel = new PositionModel(location.getTime(), name, address, location.getLatitude(), location.getLongitude());
//            positionDatabase.positionDAO().insert(positionModel);
//            positionDatabase.positionDAO().deleteDuplicates();
//            locationManager.removeUpdates(locListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            LocationListener.super.onStatusChanged(provider, status, extras);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
//            LocationListener.super.onProviderDisabled(provider);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
//            LocationListener.super.onProviderEnabled(provider);
        }
    }


}



