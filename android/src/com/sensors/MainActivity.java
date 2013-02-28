package com.sensors;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays a list of sensors that user can click on to see the data from that sensor
 */

public class MainActivity extends ListActivity {

	//list of sensors retrieved from SensorManager
	private List<Sensor> sensorList;
	
	public final static String SENSOR_TYPE_ID = "com.sensors.SENSOR_TYPE_ID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
	    String[] sensorNames = new String[sensorList.size()];
	    Sensor sensor;
	    for (int i = 0; i < sensorList.size(); i++)
	    {
	    	sensor = sensorList.get(i);
	    	sensorNames[i] = sensor.getName();
	    	
	    }
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_list_item_1, sensorNames);
		    setListAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id)
	{
		Sensor sensor = sensorList.get(position);
		Intent i = new Intent(MainActivity.this, DefaultSensorActivity.class);
		i.putExtra(SENSOR_TYPE_ID, String.valueOf(sensor.getType()));
		startActivity(i);	
	}
}
