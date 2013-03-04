package com.sensors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays a list of sensors that user can click on to see the data from that sensor
 */

public class MainActivity extends ListActivity {

	//list of sensors retrieved from SensorManager
	private ArrayList<Sensor> sensorList;
	
	public final static String SENSOR_TYPE_ID = "com.sensors.SENSOR_TYPE_ID";
	
	protected class CustomComparator implements Comparator<Sensor> {
	    @Override
	    public int compare(Sensor s1, Sensor s2) {
	    	return (s1.getType() - s2.getType());
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    List<Sensor> sensorListOrig = sensorManager.getSensorList(Sensor.TYPE_ALL);
	    sensorList = new ArrayList<Sensor>(sensorListOrig);
	    Collections.sort(sensorList, new CustomComparator());
	    String[] sensorNames = new String[sensorList.size()];
	    Sensor sensor;
	    for (int i = 0; i < sensorList.size(); i++)
	    {
	    	sensor = sensorList.get(i);
	    	sensorNames[i] = sensor.getName() + " ("+sensor.getType()+")";
	    	
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
