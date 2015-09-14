package recognition.model;

import gui.items.HumanActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import properties.P;


import com.example.diabetesplanner.DataExchange;
import com.example.diabetesplanner.GuiMain;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.content.*;



/**
 * This class is needed to support the creation, update and maintenance of an SQLite Database that stores activity and other data.
 *
 * @author Mats
 * @author Robert
 * @author Tobias
 */
public class MySQLiteHelper extends SQLiteOpenHelper{

	SQLiteDatabase db;

	// Database Constants
	private static final int DATABASE_VERSION = 41;
	public static final String DATABASE_NAME = "DiabetesPlanner.db";

	// Temp Activity Table
	private static final String ACTIVITIES_TABLE_NAME = "Temp_Activities";
	private static final String ACTIVITIES_CREATE_TABLE =
		    "CREATE TABLE IF NOT EXISTS " +
		    ""+ACTIVITIES_TABLE_NAME+"" +
		    "(INTEGER PRIMARY KEY, time BIGINT, activity VARCHAR(20), location VARCHAR(20));";
	public static final String ACTIVITES_QUERY =
			"SELECT time, activity FROM "+ACTIVITIES_TABLE_NAME+";";
	public static final String ACTIVITES_DELETE_TABLE =
			"DROP TABLE IF EXISTS "+ACTIVITIES_TABLE_NAME+";";

	// Aggregated Activity Table
	private static final String AGG_ACTIVITIES_TABLE_NAME = "Aggregated_Activities";
	private static final String AGG_ACTIVITIES_CREATE_TABLE =
		    "CREATE TABLE IF NOT EXISTS " +
		    ""+AGG_ACTIVITIES_TABLE_NAME+"" +
		    "(INTEGER PRIMARY KEY, time_begin BIGINT, time_end BIGINT, activity VARCHAR(20), duration BIGINT);";
	public static final String AGG_ACTIVITIES_QUERY =
			"SELECT time_begin, time_end, activity, location FROM "+AGG_ACTIVITIES_TABLE_NAME+";";
	public static final String AGG_ACTIVITIES_DELETE_TABLE =
			"DROP TABLE IF EXISTS "+AGG_ACTIVITIES_TABLE_NAME+";";

	// Location Table
	private static final String LOCATION_TABLE_NAME = "Locations";
	private static final String LOCATION_CREATE_TABLE =
		    "CREATE TABLE IF NOT EXISTS " +
		    ""+LOCATION_TABLE_NAME+"" +
		    "(time BIGINT PRIMARY KEY, latitude DOUBLE, longitude DOUBLE, location VARCHAR(20));";
	public static final String LOCATION_QUERY =
			"SELECT time, latitude, longitude, location FROM "+LOCATION_TABLE_NAME+";";
	public static final String LOCATION_DELETE_TABLE =
			"DROP TABLE IF EXISTS "+LOCATION_TABLE_NAME+";";


	/**
	 * Constructor of the local SQLiteDatabase
	 *
	 * @param context
	 */
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		Log.d("Database","MySQLiteHelper Constructor Started");
	}


	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		// Create Temp Activity Table
		db.execSQL(ACTIVITIES_CREATE_TABLE);
		Log.d("Database","Temp Activity Table Created");

		// Create Aggregated Activity Table
		db.execSQL(AGG_ACTIVITIES_CREATE_TABLE);
		Log.d("Database","Aggregated Activities Table Created");

		// Create Location Table
		db.execSQL(LOCATION_CREATE_TABLE);
		Log.d("Location db", "Location Table Created");

	}


	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// Drop older tables if they exist
		db.execSQL(ACTIVITES_DELETE_TABLE);
		db.execSQL(LOCATION_DELETE_TABLE);
		db.execSQL(AGG_ACTIVITIES_DELETE_TABLE);

        this.onCreate(db);
        Log.d("Database","Database Upgraded, All Tables Dropped");
	}

	/**
	 * This method is called to write the timestamp, activity and location into the SQLite Database
	 *
	 * @param helper An instance of MySQLiteHelper is needed to add a record to the database
	 * @param time timestamp in ms to be added to Temp_Activities table
	 * @param activity activity to be added to Temp_Activities table
	 * @param location location to be added to Temp_Activities table
	 *
	 * @author Mats
	 * @author Robert
	 */
	public void addRecord(MySQLiteHelper helper, long time, String activity, String location){

		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues cvs = new ContentValues();
		cvs.put("time",time);
		cvs.put("activity",activity);
		cvs.put("location", location);
		db.insert(ACTIVITIES_TABLE_NAME, null, cvs);

		Log.d("Database","record added to temp_database:"+activity+" "+location);
		db.close();
	}

	/**
	 * This method populates the Aggregated_Activities table. It checks for changes in the activity
	 * and only then adds a record.
	 *
	 * @param helper MySQLite helper
	 * @param time timestamp (beginning) in ms of the time window
	 * @param activity activity of the user during the time window
	 * @param location location of the user during the time window (currently unused!)
	 *
	 * @author Mats
	 */
	public void addAggAct(MySQLiteHelper helper, long time, String activity, String location){

		SQLiteDatabase db = helper.getWritableDatabase();
		String q = "SELECT * FROM Aggregated_Activities ORDER BY time_begin DESC LIMIT 4";
		String u = "SELECT * FROM Temp_Activities ORDER BY time DESC LIMIT 4";
		String temp_AggActivity = "";
		String temp_act_one = "";
		String temp_act_two = "";
		ContentValues cvs = new ContentValues();

		Cursor c = db.rawQuery(q, null);
		Cursor d = db.rawQuery(u, null);

		if (c.getCount() >= 1 && d.getCount() >= 3) {

			c.moveToFirst();
			d.moveToFirst();
		    temp_AggActivity = c.getString(3);
		    temp_act_one = d.getString(2);
		    d.moveToNext();
		    temp_act_two = d.getString(2);
		    Log.d("temp_AggActivity VALUE", temp_AggActivity);
		    Log.d("temp_act_one VALUE", temp_act_one);
		    Log.d("temp_act_two VALUE", temp_act_two);
		    Log.d("activity VALUE", activity);

			if (!temp_AggActivity.equals(activity) && temp_act_one.equals(activity) && temp_act_two.equals(activity)) {

				long temp_time = time - Long.valueOf(c.getString(2));

				cvs.put("time_end", time);
				cvs.put("time_begin", Long.valueOf(c.getString(2)));
				cvs.put("activity", temp_act_two);
				cvs.put("duration", temp_time);

				db.insert(AGG_ACTIVITIES_TABLE_NAME, null, cvs);
				mapDatabase(helper);
				Log.d("Database","Record Added to Aggregated_Activities Table");
				Log.d("Database - Aggregated_Activities", "Begin Time: " + Long.valueOf(c.getString(2)) + " End Time: " + time + " Activity: " + temp_AggActivity + " Duration: " + temp_time + "ms");
			}
		}

		// If Aggregated_Activities isn't populated yet, add one record with the current time and activity.
		else if (c.getCount() < 1){
			cvs.put("time_begin",0);
			cvs.put("time_end",time);
			cvs.put("activity",activity);

			db.insert(AGG_ACTIVITIES_TABLE_NAME, null, cvs);
			Log.d("Database","Default Record Added to Aggregated_Activities Table");
		}

		c.close();
		db.close();
	}

	/**
	 * Write current location into database
	 *
	 * @param helper MySQLite helper
	 * @param time timestamp which is written into database
	 * @param lat latitude which is written into database
	 * @param lon longitude which is written into database
	 * @param location ID of location type (home, work, gym)
	 *
	 * @author Tobias
	 */
	public void addLocationRecord(MySQLiteHelper helper, double lat, double lon, String location){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues cvs = new ContentValues();
		cvs.put("time",(long)System.currentTimeMillis());
		cvs.put("latitude",lat);
		cvs.put("longitude",lon);
		cvs.put("location",location);
		db.insert(LOCATION_TABLE_NAME, null, cvs);

		//deleteDatabase(MySQLiteHelper.DATABASE_NAME);

		Log.d("Database",location +" added as location");
		db.close();
	}

	/**
	 * Returns top 3 recent locations for selection by user
	 *
	 * @param helper MySQLiteHelper
	 * @return An object of type cursor that contains all entries.
	 *
	 * @author Tobias
	 */
	public Cursor getPopularLocations (MySQLiteHelper helper){
		SQLiteDatabase db = helper.getWritableDatabase();

		//Create a Cursor that contains previous records from the locations table
		Cursor cursor = db.rawQuery("select distinct location from " + LOCATION_TABLE_NAME + " WHERE location NOT LIKE '%no match%' ORDER BY time DESC LIMIT 3", null);
		return cursor;
	}

	/**
	 * Returns names of stored locations
	 *
	 * @param helper MySQLiteHelper
	 * @return An object of type cursor that contains all entries.
	 *
	 * @author Tobias
	 */
	public Cursor getLocationNames (MySQLiteHelper helper){
		SQLiteDatabase db = helper.getWritableDatabase();

		//Create a Cursor that contains previous records from the locations table
		Cursor cursor = db.rawQuery("select location from " + LOCATION_TABLE_NAME, null);
		return cursor;
	}



	// TOBI: adapt to change by ID!!
	// then delete similar locations
	/**
	 * Updates the location provided by timestamp
	 *
	 * @param helper MySQLiteHelper
	 * @param timestamp primary key
	 * @param location name of the location
	 *
	 * @return boolean if update was successful
	 *
	 * @author Tobias
	 */
	public boolean updateLocations (MySQLiteHelper helper, String timestamp, String location) {
		SQLiteDatabase db = helper.getWritableDatabase();

		// column value that should be added
		ContentValues newValues = new ContentValues();
		newValues.put("location", location);

		try {
			// try to update the Locations table
			db.update(LOCATION_TABLE_NAME, newValues, "time IN ("+timestamp+")", null);
			return true;
		}
		catch (SQLException e) {
			// if update failed return error/false
			return false;
		}

	}




	/**
	 * Return all entries in location table. Is used later on to compute distances between current location and predefined locations
	 * @param helper MySQLiteHelper
	 * @return An object of type cursor that contains all entries.
	 * @author Robert
	 */
	public Cursor getAllLocations (MySQLiteHelper helper){
		SQLiteDatabase db = helper.getWritableDatabase();

		//Create a Cursor that contains all records from the locations table
		Cursor cursor = db.rawQuery("select * from " + LOCATION_TABLE_NAME, null);
		return cursor;
	}



	/**
	 * Create log messages to inspect the current state of the database
	 * @param helper An instance of MySQLiteHelper is needed to see the current status of the database
	 * @author Robert
	 */
	public void testDatabase (MySQLiteHelper helper){
		SQLiteDatabase db = helper.getWritableDatabase();

		//Create a Cursor that contains all records from the database
		Cursor cursor = db.rawQuery("select * from " + ACTIVITIES_TABLE_NAME + " order by time desc limit 5", null);

		//in a loop, create a log message for of the last five records in the database
		if (cursor.moveToFirst()) {
			do {
				long timeinMilliseconds = cursor.getLong(1);
				Date timeasDate = new Date(timeinMilliseconds);
				String activity = cursor.getString(2);
				String location = cursor.getString(3);
				/*Log.d("Database", "Time: " + timeasDate + " Activity: " + activity
						+ " Location: " + location);*/
				} while (cursor.moveToNext());
			}
		if (!cursor.isClosed()) {
		cursor.close();
		}

	}

	/**
	 * Access the database from the charts
	 * @param helper An instance of MySQLiteHelper is needed to see the current status of the database
	 * @author Oliver
	 * @author Mats
	 */
	public void mapDatabase (MySQLiteHelper helper){
		SQLiteDatabase db = helper.getWritableDatabase();

		//Create a Cursor that contains all records from the database
		Cursor cursor = db.rawQuery("select * from " + AGG_ACTIVITIES_TABLE_NAME, null);

		//in a loop, create a log message for every existing record
		if (cursor.moveToFirst()) {
			do {
				long timeinMilliseconds = cursor.getLong(1);
				String activity = cursor.getString(3);
				long start = cursor.getLong(1);
				long end =   cursor.getLong(2);
				int dur = (int) ((end-start)/60000);
				Calendar dateTime = new GregorianCalendar();
				dateTime.setTimeInMillis(timeinMilliseconds);
				HumanActivity entry = new HumanActivity(activity, dateTime, dur);
				//DataExchange.addItem(entry);
				new UpdateUITask().execute(entry,null,null);
				} while (cursor.moveToNext());
			}
		if (!cursor.isClosed()) {
		cursor.close();
		}
	}

	private class UpdateUITask extends AsyncTask<Object, Void, Void> {

		@Override
		protected  Void doInBackground(Object... params) {
			DataExchange.addItem((HumanActivity)params[0]);
			return null;
		}

	}

	/**
	 * Delete database table contents
	 * @param helper MySQLiteHelper
	 * @author Tobias
	 */
	public void deleteDatabase (MySQLiteHelper helper) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(ACTIVITIES_TABLE_NAME,null,null);
		db.delete(LOCATION_TABLE_NAME,null,null);
		db.close();
	}


}
