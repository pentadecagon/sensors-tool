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
 * Displays data from rotation sensor
 */

public class RotationVectorActivity extends Activity implements SensorEventListener {

	float[] rotationData = new float[3];

	SensorManager sensorManager;

	private Sensor rotationDetector;

	TextView rotationDataView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rotation_vector);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

		rotationDetector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		rotationDataView = (TextView) findViewById(R.id.rotation_data);
	}

	public void onSensorChanged(SensorEvent event) {

		switch (event.sensor.getType()) {
			case Sensor.TYPE_ROTATION_VECTOR:
				System.arraycopy(event.values, 0, rotationData, 0, 3);
				break;
           default:
        	   Log.d("sensor", "unidentified sensor type!");
                    return;
		}

		//rotation
		String rotationText;
		if (rotationData != null) {
			rotationText = "x:   " + rotationData[0]
					+ "\ny:   " + rotationData[1]
					+ "\nz:   " + rotationData[2];	
		} else
		{
			rotationText = "could not find rotation data";
		}
		rotationDataView.setText(rotationText);
		Log.d("sensor", "onSensorChanged: rotation data:\n"+rotationText);
	}

	@Override
	protected void onResume() {
		super.onResume();

	    sensorManager.registerListener(this, rotationDetector, SensorManager.SENSOR_DELAY_NORMAL);
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
