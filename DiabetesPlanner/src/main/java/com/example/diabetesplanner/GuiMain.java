package com.example.diabetesplanner;

import gui.activities.AdaptLocation;
import gui.activities.AddActivity;
import gui.activities.BsOverview;
import gui.activities.ChartActivity;
import gui.activities.ChartCompare;
import gui.items.AbstractActivity;
import gui.items.BloodSugar;
import gui.items.Carb;
import gui.items.HumanActivity;
import gui.items.Insulin;
import gui.misc.ActivityListAdapter;
import gui.misc.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import properties.P;
import recognition.model.DataCollector;
import recognition.model.GPSTracker;
import recognition.model.LocationLogic;
import recognition.model.MySQLiteHelper;
import weka.core.converters.DatabaseLoader;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**This class is the start activity of the application (defined in androidmanifest.xml).
 * It displays a list of all recently recorded activities performed by the user and
 * provide buttons to enter other activities.
 *
 * @author Oliver
 *
 */


public class GuiMain extends Activity {
	
	static ArrayList<AbstractActivity> content;

	public static ActivityListAdapter myLa;
	
	public static ListView aList;

	private static Context mainContext;
	public static Context getContext(){
	      return mainContext;
	} 
	

	/**
	 * The Intent dataCollection makes it possible to start and stop the
	 * data collection Service from the whole class.
	 * 
	 * @author Tobias
	 *
	 */
	private Intent dataCollection; 
	
	/** Variables for the current GPS location
	 *  Declaration of GPS class
	 * 
	 * @author Mahmoud, Tobias
	 */
	static double lat = 0; // latitude
	static double lon = 0; // longitude	
	public static GPSTracker track;

	/**
	 * The context of the MainActivity GuiMain is stored 
	 * in this object and can be called from every other
	 * class by calling 
	 * 
	 * @author Tobias
	 *
	 */
	/*private static Context mContext;
	public static Context getAppContext(){
	      return mContext;
	} 
	public static Context setAppContext(Context con){
	      mContext = con;
	} */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("guimain", "onCreate() invoked");
		Toast.makeText(this, "ON CREATE", 500).show();;
		
		setContentView(R.layout.activity_main);
		
		content = DataExchange.actList;
		
		//establish connection listview - adapter - arraylist
		aList = new ListView(getApplicationContext());
		aList = (ListView) findViewById(R.id.activityList);
		myLa = new ActivityListAdapter(this, R.layout.list_item_layout, content);
		aList.setAdapter(myLa);		
			
			if(DataExchange.mainInvocation==0){
			//DataExchange.actList = createSampleData();

			// start DataCollector Service to access Accelerometer data
			dataCollection = new Intent();
			dataCollection.setClass(getApplicationContext(), DataCollector.class);
			startService(dataCollection);
			
			// enable GPS for the location tracking
			track = new GPSTracker(this);
			
				// If GPS is not enabled the app asks the user to enable it
			if (!track.canGetLocation())
				track.showSettingsAlert();
		}
			content= DataExchange.actList;
			myLa.registerDataSetObserver(
					new DataSetObserver() {
					} );
			//myLa.notifyDataSetChanged();
		
		//added as samples representing usual locations
		Location l1 = new Location(49.24124,1.213242,"Home");
		Location l2 = new Location(49.1245,1.235435,"Gym");
		Location l3 = new Location(49.326347,1.123245,"University");
		DataExchange.locationList.add(l1);
		DataExchange.locationList.add(l2);
		DataExchange.locationList.add(l3);


		mainContext = this;
		
		super.onCreate(savedInstanceState);

		DataExchange.mainInvocation++;

	    //Remove title bar
	    //this.requestWindowFeature(Window.FEATURE_NO_TITLE);


		

		
		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				content = DataExchange.actList;
				myLa.notifyDataSetChanged();
			}
		});



		 aList.setOnItemLongClickListener(new OnItemLongClickListener() {
			   @Override
			   public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long arg) {
			      Intent appInfo = new Intent(GuiMain.this, AddActivity.class);
			      appInfo.putExtra("name", DataExchange.actList.get(position).getName());
			      appInfo.putExtra("time", DataExchange.actList.get(position).getStartTime());
			      //appInfo.putExtra("dur", DataExchange.actList.get(position).getDuration());
			      startActivity(appInfo);
				return false;
			   }
			});
	}

	//	@Override
//	protected void onResume(){
//		super.onResume();
//		Log.d("guimain", "onResume() invoked");
//
//		Toast.makeText(this, "ON RESUME", 500).show();
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.turn_off) {
			shutDown(null);
		}
		if (id == R.id.select_model) {
			modelSelection(null);
		}
		if (id == R.id.enter_location) {
			locationFunction(null);
		}
		if (id == R.id.see_locations){
			showLocationsFunction(null);			
		}
		if(id == R.id.settings){
			showSettings(null);
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**Invoked when the user presses a menu item. 
	 * Leads to an activity where the user can set the units for bloodsugar and carbohydrate values. 
	 * 
	 * @author Oliver
	 */
	public void showSettings(View view) {
		
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
     }
	


	/**Invoked when the user presses a certain button.
	 * Leads to an activity where the user can enter his current blood sugar value and see the past values.
	 *
	 *@author Oliver
	 */
	public void goToBsOverview(View view){

        Intent intent = new Intent(this, BsOverview.class);
        startActivity(intent);
	}
	
	/**Turns off data collection if app is closed explicitly with the button "Turn off"
	 * 
	 * @author Tobias
	 */
	public void shutDown(View view){

		// stop the service of the dataCollection
		stopService(dataCollection);
		
		// export db to SD card
		Export.exportDB();
		
		// shut down the app itself
		finish();
		
	}
	
	/**
	 * Opens activity to edit previous locations
	 * 
	 * @author Tobias
	 */
	public void editLocations(View view){

		Intent intent = new Intent(this, AdaptLocation.class);
        startActivity(intent); 		
		
	}
	
	/** 
	 * locationFunction is called after the location
	 * button is pressed
	 * 
	 * @param view the current view
	 * 
	 * @author Tobias
	 */
	public void locationFunction(View view){
		
		//lat = track.getLatitude();
		//lon = track.getLongitude();
		
		//addLocation();
		
		// provide current context to PopUps
		PopUps.setAppContext(this);
		PopUps.addLocation();			
	}

	
	/**
	 * Changes the string of the model file which is called in the calculation class. This way we can distinguish between using the generic or individual models
	 * @author Robert
	 * @param view
	 */
	public void modelSelection(View view){

		CharSequence models[] = new CharSequence[] {"Generic", "Mats", "Timo", "Oliver", "Robert", "Tobi"};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose Model");
		builder.setItems(models, new DialogInterface.OnClickListener(){
			
			@Override
		    public void onClick(DialogInterface dialog, int modelClicked) {		    			    	
				
				switch(modelClicked){
				case 0:
					P.modelDataFileName = "J48.generic";
					break;
					
				case 1:
					P.modelDataFileName = "J48.mats";
					break;
					
				case 2:
					P.modelDataFileName = "J48.timo";
					break;
					
				case 3:
					P.modelDataFileName = "J48.oliver";
					break;
					
				case 4:
					P.modelDataFileName = "J48.robert";
					break;
					
				case 5:
					P.modelDataFileName = "J48.tobias";
					break;
				}
				Log.d("ModelChange", "Model was changed to " + P.modelDataFileName);
			}
		});
		builder.show();
	}
	
	/**Invoked when the user presses a certain button.
	 * Leads to an activity where the user can enter an activity manually.
	 * 
	 * @author Oliver
	 */
	public void goToAddActivity(View view){

        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
	}

	/**Invoked when the user presses a certain button.
	 * Leads to an activity where the user can see all recorded data in a graphical way.
	 * 
	 * @author Oliver
	 */
	public void goToChartView(View view){

        Intent intent = new Intent(this, ChartActivity.class);
        //intent.putExtra("adapter", myLa); //TODO: find out how
        startActivity(intent);
	}

	/**Invoked when the user presses a certain button.
	 * Leads to an activity where the user can see his bloodsugar deviation within the last week.
	 *
	 *@author Oliver
	 */
	public void goToWeekChart(View view){

        Intent intent = new Intent(this, ChartCompare.class);
        startActivity(intent);
	}
	
	/**Invoked when the user presses a certain button.
	 * Leads to an activity where the user can see all the locations he decided to track with title and X-& Y-Values.
	 *
	 *@author Oliver
	 */	
	public void showLocationsFunction(View view){

        Intent intent = new Intent(this, LocationList.class);
        startActivity(intent);
	}

}
