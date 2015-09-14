package com.example.diabetesplanner;

import gui.misc.ActivityListAdapter;
import gui.misc.LocListAdapter;
import gui.misc.Location;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class LocationList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_list);
		
		ArrayList<Location> locList = DataExchange.getLocations();
		
		ListView locListView = (ListView) findViewById(R.id.locationList);

		LocListAdapter locLA = new LocListAdapter(this, R.layout.loc_list_item_layout, DataExchange.locationList);
		
		locListView.setAdapter(locLA);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
