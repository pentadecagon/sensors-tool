package de.karbid.sensors;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Displays data from the sensor whose type is input by SENSOR_ID
 */

public class DefaultSensorActivity extends Activity implements SensorEventListener {

	float[] dataReadFromSensor;
    GLRenderer glRenderer;
	SensorManager sensorManager;

	private Sensor sensor;

	TextView sensorDataViewX, sensorDataViewY, sensorDataViewZ;
	
	private int SENSOR_TYPE_ID;

	DecimalFormat df = new DecimalFormat("0.00000");

    private boolean checkFor3d(int sensor_type) {
        int [] good = new int[]{1, 2, 9, 10, 11, 14, 15, 20};
        for (int a : good) {
            if (a == sensor_type)
                return true;
        }
        return false;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int SENSOR_POSITION_ID = Integer.parseInt(getIntent().getStringExtra(MainActivity.SENSOR_POSITION_ID));
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_default_sensor);
		sensor = MainActivity.sensorList.get(SENSOR_POSITION_ID);
		SENSOR_TYPE_ID = sensor.getType();
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

		((TextView) findViewById(R.id.sensor_name)).setText(sensor.getName());
		
		((TextView) findViewById(R.id.sensor_details)).setText(formatSensorDetails());

		((TextView) findViewById(R.id.sensor_data_header)).setText(formatSensorDataHeader());


        GLSurfaceView glView = (GLSurfaceView) findViewById(R.id.surfaceView);
        if (checkFor3d(SENSOR_TYPE_ID)) {
            glView.setEGLContextClientVersion(3);
            glRenderer = new GLRenderer(this, SENSOR_TYPE_ID);
            glView.setRenderer(glRenderer);
        } else {
            ((LinearLayout)glView.getParent()).removeView(glView);
        }
		
		sensorDataViewX = (TextView) findViewById(R.id.sensor_data_x);
        sensorDataViewY = (TextView) findViewById(R.id.sensor_data_y);
        sensorDataViewZ = (TextView) findViewById(R.id.sensor_data_z);
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
			dataReadFromSensor = event.values.clone();
            if (glRenderer != null)
                glRenderer.setSensor(event.values[0], event.values[1], event.values[2]);
		} else
		{
			Log.d("sensor", "onSensorChanged: unidentified sensor type!");
		}

		displaySensorData();

	}
	private boolean invisible = true;

	protected void displaySensorData()
	{
		if (dataReadFromSensor != null) {
			String dataX = df.format(dataReadFromSensor[0]);
            sensorDataViewX.setText(dataX);
			if (dataReadFromSensor.length > 1)
			{
				String dataY = df.format(dataReadFromSensor[1]);
				String dataZ = df.format(dataReadFromSensor[2]);
                if (invisible) {
                    findViewById(R.id.table_row_y).setVisibility(View.VISIBLE);
                    findViewById(R.id.table_row_z).setVisibility(View.VISIBLE);
                    invisible = false;
                }
                sensorDataViewY.setText(dataY);
                sensorDataViewZ.setText(dataZ);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	    sensorManager.registerListener(this, sensor, 33000);
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
