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
 * Displays data from acceleration & gravity sensors
 */

public class AccelerationActivity extends Activity implements SensorEventListener {

	float[] accelerationData = new float[3];
	
	float[] gravityData = new float[3];

	SensorManager sensorManager;
	
	private Sensor accelerometer;
	
	private Sensor gravityDetector;

	TextView accDataView;
	
	TextView gravDataView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acceleration);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);	
		gravityDetector = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

		accDataView = (TextView) findViewById(R.id.acc_data);
		gravDataView = (TextView) findViewById(R.id.grav_data);
	}

	public void onSensorChanged(SensorEvent event) {
		Log.d("sensor", "called onSensorChanged");

		switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				System.arraycopy(event.values, 0, accelerationData, 0, 3);
				break;
			case Sensor.TYPE_GRAVITY:
				System.arraycopy(event.values, 0, gravityData, 0, 3);
				break;
           default:
        	   Log.d("sensor", "unidentified sensor type!");
                    return;
		}
		
		//total acceleration
		String accText;
		if (accelerationData != null) {
			accText = "x:   " + accelerationData[0]
					+ "\ny:   " + accelerationData[1]
					+ "\nz:   " + accelerationData[2];	
		} else
		{
			accText = "could not find acceleration data";
		}
		accDataView.setText(accText);
		Log.d("sensor", "onSensorChanged: acceleration data:\n"+accText);

		//acceleration due to gravity
		String gravText;
		if (gravityData != null) {
			gravText = "x:   " + gravityData[0]
					+ "\ny:   " + gravityData[1]
					+ "\nz:   " + gravityData[2];	
		} else
		{
			gravText = "could not find gravity data";
		}
		gravDataView.setText(gravText);
		Log.d("sensor", "onSensorChanged: gravity data:\n"+gravText);
	}

	@Override
	protected void onResume() {
		super.onResume();
	    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	    sensorManager.registerListener(this, gravityDetector, SensorManager.SENSOR_DELAY_NORMAL);
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