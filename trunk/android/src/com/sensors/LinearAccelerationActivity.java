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
 * Displays data from linear acceleration sensor
 */

public class LinearAccelerationActivity extends Activity implements SensorEventListener {

	float[] linearAccData = new float[3];

	SensorManager sensorManager;

	private Sensor linearAccDetector;

	TextView linearAccDataView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_linear_acceleration);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

		linearAccDetector = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		linearAccDataView = (TextView) findViewById(R.id.linear_acc_data);
	}

	public void onSensorChanged(SensorEvent event) {

		switch (event.sensor.getType()) {
			case Sensor.TYPE_LINEAR_ACCELERATION:
				System.arraycopy(event.values, 0, linearAccData, 0, 3);
				break;
           default:
        	   Log.d("sensor", "unidentified sensor type!");
                    return;
		}

		//linear acceleration
		String linearAccText;
		if (linearAccData != null) {
			linearAccText = "x:   " + linearAccData[0]
					+ "\ny:   " + linearAccData[1]
					+ "\nz:   " + linearAccData[2];	
		} else
		{
			linearAccText = "could not find linear acceleration data";
		}
		linearAccDataView.setText(linearAccText);
		Log.d("sensor", "onSensorChanged: linear acc data:\n"+linearAccText);
	}

	@Override
	protected void onResume() {
		super.onResume();

	    sensorManager.registerListener(this, linearAccDetector, SensorManager.SENSOR_DELAY_NORMAL);
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
