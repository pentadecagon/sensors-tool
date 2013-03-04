package com.sensors;

import java.text.DecimalFormat;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Displays data from the sensor whose type is input by SENSOR_ID
 */

public class DefaultSensorActivity extends Activity implements SensorEventListener {

	float[] dataReadFromSensor;

	SensorManager sensorManager;

	private Sensor sensor;

	TextView sensorDataViewX, sensorDataViewY, sensorDataViewZ;
	
	private int SENSOR_TYPE_ID;
	
	private int dimensions = 3;
	
	DecimalFormat df = new DecimalFormat("0.00000");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int SENSOR_POSITION_ID = Integer.parseInt(getIntent().getStringExtra(MainActivity.SENSOR_POSITION_ID));
		
		setContentView(R.layout.activity_default_sensor);

		sensor = MainActivity.sensorList.get(SENSOR_POSITION_ID);
		SENSOR_TYPE_ID = sensor.getType();
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

		((TextView) findViewById(R.id.sensor_name)).setText(sensor.getName());
		
		((TextView) findViewById(R.id.sensor_details)).setText(formatSensorDetails());

		((TextView) findViewById(R.id.sensor_data_header)).setText(formatSensorDataHeader());
		
		dimensions = getNumberOfDimensions();
		dataReadFromSensor = new float[dimensions];
		
		sensorDataViewX = (TextView) findViewById(R.id.sensor_data_x);
		if (dimensions > 1)
		{
			findViewById(R.id.table_row_y).setVisibility(View.VISIBLE);
			findViewById(R.id.table_row_z).setVisibility(View.VISIBLE);
			sensorDataViewY = (TextView) findViewById(R.id.sensor_data_y);
			sensorDataViewZ = (TextView) findViewById(R.id.sensor_data_z);
		}
	
	}

	protected int getNumberOfDimensions()
	{
		switch(SENSOR_TYPE_ID)
		{
			case Sensor.TYPE_LIGHT:
			case Sensor.TYPE_PRESSURE:
			case Sensor.TYPE_PROXIMITY:
			case Sensor.TYPE_RELATIVE_HUMIDITY:
			case Sensor.TYPE_AMBIENT_TEMPERATURE:
				return 1;
			default:
				return 3;
		}
	}
	
	protected String formatSensorDetails()
	{
		String details = "vendor=\""+sensor.getVendor();
		details += "\", version="+sensor.getVersion();
		details += ", type ID="+sensor.getType();
		details += ", maxRange="+sensor.getMaximumRange();
		details += ", resolution="+sensor.getResolution();
		details += ", power="+sensor.getPower();
		details += ", minDelay="+sensor.getMinDelay();  // exists only for API>=9
		return details;

	}

	protected String formatSensorDataHeader()
	{
		String header = "Sensor data";
		switch(SENSOR_TYPE_ID)
		{
			case Sensor.TYPE_ACCELEROMETER:
			case Sensor.TYPE_GRAVITY:
			case Sensor.TYPE_LINEAR_ACCELERATION:
				header += " (m/s²)";
				break;
			case Sensor.TYPE_AMBIENT_TEMPERATURE:
				header += " (°C)";
				break;
			case Sensor.TYPE_GYROSCOPE:
				header += " (rad/s)";
				break;
			case Sensor.TYPE_LIGHT:
				header += " (lux)";
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				header += " (μT)";
				break;
			case Sensor.TYPE_ORIENTATION:
				header += " (degrees)";
				break;
			case Sensor.TYPE_PRESSURE:
				header += " (hPa)";
				break;
			case Sensor.TYPE_PROXIMITY:
				header += " (cm)";
				break;
			case Sensor.TYPE_RELATIVE_HUMIDITY:
				header += " (%)";
				break;
			case Sensor.TYPE_ROTATION_VECTOR:
				header += ""; // unitless
				break;
			default:
				//do nothing
		}
		return header;
	}
	
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == SENSOR_TYPE_ID)
		{
			System.arraycopy(event.values, 0, dataReadFromSensor, 0, dimensions);
		} else
		{
			Log.d("sensor", "onSensorChanged: unidentified sensor type!");
		}

		displaySensorData();

	}
	
	protected void displaySensorData()
	{
		//display sensor data
		String dataX = "No data found", dataY = "No data found", dataZ = "No data found";
		if (dataReadFromSensor != null) {
			dataX = df.format(dataReadFromSensor[0]);
			if (dimensions > 1)
			{
				dataY = df.format(dataReadFromSensor[1]);
				dataZ = df.format(dataReadFromSensor[2]);
			}
		}
		
		sensorDataViewX.setText(dataX);
		if (dimensions > 1)
		{
			sensorDataViewY.setText(dataY);
			sensorDataViewZ.setText(dataZ);
			Log.d("sensor", "displaySensorData: "+sensor.getName()+":\nx: "+dataX+"\ny: "+dataY+"\nz: "+dataZ);
		} else
		{
			Log.d("sensor", "displaySensorData: "+sensor.getName()+": "+dataX);
		}
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
