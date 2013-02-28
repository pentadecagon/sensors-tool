package com.sensors;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays a list of sensors that user can click on to see the data from that sensor
 */

public class MainActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    String[] values = new String[] { "Acceleration & Gravity", "Gyro", "Linear Acceleration", "Rotation Vector"};
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	        android.R.layout.simple_list_item_1, values);
	    setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onListItemClick(ListView list, View view,
	int position, long id)
	{
		Intent i;
		switch(position)
		{
			case 0:
				i = new Intent(MainActivity.this, AccelerationActivity.class);
				startActivity(i);	
				break;
			case 1:
				i = new Intent(MainActivity.this, GyroActivity.class);
				startActivity(i);
				break;
			case 2:
				i = new Intent(MainActivity.this, LinearAccelerationActivity.class);
				startActivity(i);
				break;
			case 3:
				i = new Intent(MainActivity.this, RotationVectorActivity.class);
				startActivity(i);
				break;
		}

	}
}
