package recognition.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.diabetesplanner.GuiMain;
import com.example.diabetesplanner.PopUps;

import properties.P;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;

/**
 * LocationLogic contains the logic to manage the location
 * 
 * @author Tobias
 * 
 */
public class LocationLogic {
	
	// String which is written into database if no location is known
	private static String noMatchLocation = "No Match";
	
	/**
	 * getNoMatchLocationString can be called from other methods to get
	 * the location tuples that are undefined
	 * HINT: this method is used in other classes and should not be renamed
	 * 
	 * @return noMatchLocation string
	 * 
	 * @author Tobias
	 */
	public static String getNoMatchLocation () {
		return noMatchLocation;
	}
	
	// counter for unknown previous location
	private static int noMatchLocationsCounter;
	
	
	/**
	 * Checks if the current location is very near to a predefined location. If yes, the String of the nearest location is returned.
	 * @param helper
	 * @param long1
	 * @param lat1
	 * @return
	 * @author Robert
	 */
	public static String compareWithPredefLocations(MySQLiteHelper helper, double long1, double lat1) {
		String location = noMatchLocation;
		double long2, lat2, distance;
		double shortestDistance = 0.05;

		Cursor cursor = helper.getAllLocations(helper);

		// in a loop, compute distance of current locations and locations in the database
		if (cursor.moveToFirst()) {
			do {
				long2 = cursor.getDouble(1);
				lat2 = cursor.getDouble(2);
				distance = distance(long1, lat1, long2, lat2);
				
				// if distance < 50m, use predefined location label
				if ( distance < 0.05 && distance < shortestDistance) {
					location = cursor.getString(3);
					shortestDistance=distance;
				}
			} while (cursor.moveToNext());
		}if (!cursor.isClosed()) {
			cursor.close();
		}
		Log.d("LOCATION",location);
		return location;
	}
  
	/**
	 * Computes the distance between two points using lat and long of both
	 * points
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 * @author Robert
	 */
	static double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;
		return dist;
	}

	static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
	
	/**
	 * getPopularLocations returns a list of previous locations
	 * Excludes the "no matches"
	 * 
	 * @param helper MySQLite helper
	 * @return CharSequence with the previous locations
	 * 
	 * @author Tobias
	 */
	public static CharSequence[] getPopularLocations (MySQLiteHelper helper) {
		Cursor cursor = helper.getPopularLocations(helper);
		
		CharSequence[] locas = new CharSequence[4];
		
		// default values
		locas[0] = "home";
		locas[1] = "work";
		locas[2] = "gym";
		locas[3] = "Other location";
		
		// check if there are more than three locations in the database to not
		// get a null pointer exception
		if (cursor.getCount()  >= 3) {
		if (cursor.moveToFirst()) {
			for (int i=0; i<=2; i++) {
				if (cursor.getString(0) != null && !cursor.getString(0).isEmpty()) {
					locas[i] = cursor.getString(0);
					cursor.moveToNext();
				}
				else break;
			}
		}
		}
		
		if (!cursor.isClosed()) {
			cursor.close();
		}

		return locas;
		
	}
	
	/**
	 * askForLocation is called if a user is in an unknown location
	 * for a certain period. This amount is specified in P.askUserForLocationAfter
	 * 
	 * This function needs to be enabled in P
	 * 
	 * @param String of previously stored location
	 * 
	 * @author Tobias
	 */
	public static void askForLocation (String location) {
		
		// check if this function is enabled
		if (P.askUserForLocation > 0) {
			Log.d("locationlogic","enabled");
			
			// check if the last stored location was unknown
			// if yes, increase the noMatchLocationsCounter
			if (location == noMatchLocation)
				noMatchLocationsCounter++;
			else 
				noMatchLocationsCounter = 0;
		
			Log.d("locationLogic count",Integer.toString(noMatchLocationsCounter));
			
			// check if the noMatchLocationsCounter reached the maximum amount
			// of unknown location entries which are stored in the database
			if 	(noMatchLocationsCounter >= P.askUserForLocationAfter) {
				
				// ask for location								
				PopUps.addLocation();
				
				// reset the counter
				noMatchLocationsCounter = 0;
				
			}
			
		}

	}

	/**
	 * checks if a location should be added to the locations table
	 * 
	 * @param helper
	 * @param location
	 * @param lat
	 * @param lon
	 * @return boolean with 1=add location and 0=location exists
	 * 
	 * @author Tobias
	 */
	public static boolean checkAndAddLocation (MySQLiteHelper helper, String location, double lat, double lon) {
		boolean nameExists = false;
		
		// get names of stored locations
		// check if location already exists
		
		// download all stored location names first
		Cursor cursor = helper.getLocationNames(helper);
		if (cursor.moveToFirst()) {
			do {
				Log.d("LOCATION entry", cursor.getString(0));
				if(cursor.getString(0).equals(location)) {	
					nameExists = true;
					break;
				}
			} while (cursor.moveToNext());
		}
		// close cursor
		if (!cursor.isClosed()) {
			cursor.close();
		}			

		if (!nameExists) {
			// add location to database
			helper.addLocationRecord(helper, lat, lon, location);
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * returns the timestamp of similar locations to the requested 
	 * longitude and latitude
	 * 
	 * @param helper
	 * @param askedLat
	 * @param askedLon
	 * @return String with timestamps
	 * 
	 * @author Tobias
	 */
	public static String similarLocations (MySQLiteHelper helper, double askedLat, double askedLon) {
		String locations = "";
		double distance;
		
		// get timestamps of similar locations
		
		// download all locations first
		Cursor cursor = helper.getAllLocations(helper);
		
		// in a loop, compute distance of current locations and locations in the database
			if (cursor.moveToFirst()) {
				do {
					distance = distance(askedLat, askedLon, cursor.getDouble(1), cursor.getDouble(2));
					
					// if distance < 30m, the location is considered as the same
					if ( distance < 0.03) {
						locations  += cursor.getString(0)+",";
						Log.d("DISTANCE", Double.toString(distance)+" "+cursor.getString(0));
					}
				} while (cursor.moveToNext());
			}if (!cursor.isClosed()) {
				cursor.close();
			}
		
		locations += "0";
		return locations;
		
	}


	
}