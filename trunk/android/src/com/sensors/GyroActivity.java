package com.sensors;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Displays data from gyroscope sensor
 */

public class GyroActivity extends Activity implements SensorEventListener {

	float[] gyroData = new float[3];
	
	SensorManager sensorManager;
	
	private Sensor gyro;
	
	TextView gyroDataView;
	TextView gyroSpeedView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gyro);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);	
		
		gyroDataView = (TextView) findViewById(R.id.gyro_data);
		gyroSpeedView = (TextView) findViewById(R.id.gyro_speed);
	}


	public void onSensorChanged(SensorEvent event) {

		switch (event.sensor.getType()) {
			case Sensor.TYPE_GYROSCOPE:
				System.arraycopy(event.values, 0, gyroData, 0, 3);
				break;
           default:
        	   Log.d("sensor", "unidentified sensor type!");
                    return;
		}
		
		String gyroText;
		String gyroSpeedText;
		if (gyroData != null) {
			
			float omegaMagnitude = (float) Math.sqrt(gyroData[0]*gyroData[0] + gyroData[1]*gyroData[1] + gyroData[2]*gyroData[2]);
			
			gyroText = "x:   " + gyroData[0] + "\n"
					+ "y:   " + gyroData[1] + "\n"
					+ "z:   " + gyroData[2] + "\n";
			gyroSpeedText = "angular speed: " + omegaMagnitude;
		} else
		{
			gyroText = "could not find gyro data";
			gyroSpeedText = "";
		}
		gyroDataView.setText(gyroText);
		gyroSpeedView.setText(gyroSpeedText);
		Log.d("sensor", "onSensorChanged: gyro data:\n"+gyroText+"\n"+gyroSpeedText);
	}


	@Override
	protected void onResume() {
		super.onResume();
	    sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
	}
	

	@Override
	protected void onPause() {
		//unregister the listeners to avoid running down the battery
		sensorManager.unregisterListener(this);
		super.onPause();
	}
	  
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
