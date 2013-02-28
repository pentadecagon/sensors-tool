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
 * Displays data from the sensor whose type is input by SENSOR_ID
 */

public class DefaultSensorActivity extends Activity implements SensorEventListener {

	float[] dataReadFromSensor = new float[3];

	SensorManager sensorManager;

	private Sensor sensor;

	TextView sensorDataView;
	
	private int SENSOR_TYPE_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SENSOR_TYPE_ID = Integer.parseInt(getIntent().getStringExtra(MainActivity.SENSOR_TYPE_ID));
		
		setContentView(R.layout.activity_default_sensor);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

		sensor = sensorManager.getDefaultSensor(SENSOR_TYPE_ID);

		TextView sensorNameView = (TextView) findViewById(R.id.sensor_name);
		sensorNameView.setText(sensor.getName());
		
		TextView sensorDetailsView = (TextView) findViewById(R.id.sensor_details);
		String detailsText = sensor.toString();
		sensorDetailsView.setText(detailsText);
		
		sensorDataView = (TextView) findViewById(R.id.sensor_data);
	}

	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == SENSOR_TYPE_ID)
		{
			System.arraycopy(event.values, 0, dataReadFromSensor, 0, 3);
		} else
		{
			Log.d("sensor", "onSensorChanged: unidentified sensor type!");
		}

		displaySensorData();

	}
	
	protected void displaySensorData()
	{
		//display sensor data
		String dataText;
		if (dataReadFromSensor != null) {
			switch(SENSOR_TYPE_ID)
			{
				case Sensor.TYPE_LIGHT:
				case Sensor.TYPE_PRESSURE:
				case Sensor.TYPE_PROXIMITY:
				case Sensor.TYPE_RELATIVE_HUMIDITY:
				case Sensor.TYPE_AMBIENT_TEMPERATURE:
					dataText = ""+dataReadFromSensor[0];
					break;
				default:
					dataText = "x:   " + dataReadFromSensor[0]
						+ "\ny:   " + dataReadFromSensor[1]
						+ "\nz:   " + dataReadFromSensor[2];	
			}
		} else
		{
			dataText = "could not find data from "+sensor.getName()+" sensor";
		}
		sensorDataView.setText(dataText);
		Log.d("sensor", "displaySensorData: "+sensor.getName()+":\n"+dataText);
	}

	@Override
	protected void onResume() {
		super.onResume();

	    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
