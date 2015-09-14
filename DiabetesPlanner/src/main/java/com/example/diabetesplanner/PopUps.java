package com.example.diabetesplanner;

import recognition.model.GPSTracker;
import recognition.model.LocationLogic;
import recognition.model.MySQLiteHelper;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

/**
 * PopUps shows Alert Dialogs where the user can 
 * select or enter data
 * 
 * @author Tobias
 * 
 */
public class PopUps implements Runnable {

	private static Context mContext;
	public static Context getAppContext(){
	      return mContext;
	} 
	public static void setAppContext(Context con){
	      mContext = con;
	} 

	
	/** 
	 * With enterLocation a user can enter a location manually
	 * 
	 * @author Robert
	 */
	public static void enterLocation () {
		//Context here = GuiMain.getAppContext();
		final MySQLiteHelper dbHelper = new MySQLiteHelper (mContext);
		
		final EditText input = new EditText(mContext);
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Choose your location");
		builder.setView(input);	
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				
				// get GPS coordinates
				// GPS tracker was initialized in the GuiMain.java
				double lat = GuiMain.track.getLatitude();
				double lon = GuiMain.track.getLongitude();
				
				// add self-defined location record to the location table in the database
				if (LocationLogic.checkAndAddLocation(dbHelper, value, lat, lon)) 
					Toast.makeText(mContext, "Location was added", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(mContext, "Location with this name exists already", Toast.LENGTH_SHORT).show();
			  }
			});

			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					
			  }
			});

			builder.show();
	}
	
	
	/** 
	 * addLocation opens a popup window where a user can select
	 * his current location
	 * 
	 * @author Tobias
	 */
		public static void addLocation(){			
		
		// let user enter the current location
		enterLocation();
			
	/* -------
     * previous locations to show are currently disabled
     * -------
		 
			// create new MySQLlite helper
		final MySQLiteHelper dbHelper = new MySQLiteHelper (mContext);

		// get the previous/popular locations from the database
		final CharSequence[] popularLocations = LocationLogic.getPopularLocations(dbHelper);
		
		// define Thread/ Runnable
		final Handler guiHandler = new Handler();
		final Runnable runner = new Runnable() { public void run() {
			
			// string entering will be shown if selected
			final EditText input = new EditText(mContext);
			final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Choose your location");
			builder.setView(input);	
			
		
			// selection list will be shown first
			AlertDialog.Builder builderselection = new AlertDialog.Builder(mContext);
			builderselection.setTitle("Choose Location");
			builderselection.setItems(popularLocations, new DialogInterface.OnClickListener(){
			
				@Override
				public void onClick(DialogInterface dialog, int locaClicked) {	
				
					// locationToEnter is initially a undefined value
					// this value is set in LocationLogic
					String locationToEnter = LocationLogic.getNoMatchLocation();
				
					switch(locaClicked){
					case 0:
						locationToEnter = popularLocations[0].toString();
						break;
					
					case 1:
						locationToEnter = popularLocations[1].toString();
						break;
					
					case 2:
						locationToEnter = popularLocations[2].toString();
						break;
					
					case 3:					
						enterLocation();
						break;
				
					}
				
					if (locaClicked < 3) {
						// get GPS coordinates
						// GPS tracker was initialized in the GuiMain.java
						double lat = GuiMain.track.getLatitude();
						double lon = GuiMain.track.getLongitude();
				
						//add location record to the location table in the database
						if (LocationLogic.checkAndAddLocation(dbHelper, locationToEnter, lat, lon)) 
							Toast.makeText(mContext, "Location was added", Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(mContext, "Location with this name exists already", Toast.LENGTH_SHORT).show();
					}
			
				}
			
			});
			builderselection.show();
		
		}}; // end of Thread definition
		
		// start Thread
		try {
			guiHandler.post(runner);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Thread","no");
        }
   -------
	*/	
	}
			
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
		
}
