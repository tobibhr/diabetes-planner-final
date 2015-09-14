package recognition.model;

import java.io.InputStream;

import com.example.diabetesplanner.PopUps;

import properties.P;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * DataCollector is a Service that accesses Sensor Data and stores it in a collection
 * The Sensor Data is accessed all the time even when another app starts.
 * 
 * @author Tobias
 *
 */
public class DataCollector extends Service implements SensorEventListener, Runnable {
	
	private SensorManager sensorManager;
	private Sensor sensor;
		
	// counter for the array	
	private int accelerometerCacheCounter = 0;
	
	// Handlers to manage Thread/ Runnable
	final Handler threadHandler = new Handler();

		
	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 * 
	 * @author Tobias Baehr
	 */
	 @Override
	 public void onCreate() {
		 
		 // select the Accelerometer sensor
		 sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		 
		 // check if the Accelerometer is available
		 if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			 sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		 }
		 else {
			 Log.d("ACCELROMETER", "not available");
		 }
		 
	 }
	 
	 /*
	  * (non-Javadoc)
	  * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	  * 
	  * @author Tobias Baehr
	  */
	 @Override
	 public int onStartCommand(Intent intent, int flags, int startId) {
		 
		// register Listener
	    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
	    
	    // display that the Accelerometer is accessed
	    // on the screen and in the Log Cat
	    Toast.makeText(this, "Accelerometer data is accessed!", Toast.LENGTH_SHORT).show();
		Log.d("ACCELEROMETER", "accessed and ready to start!");
	    
		return Service.START_STICKY;
	 }
	 
	 /*
	  * (non-Javadoc)
	  * @see android.app.Service#onDestroy()
	  * 
	  * @author Tobias Baehr
	  */
	 @Override
	 public void onDestroy() {
		
		 // unregister sensorManager for Accelerometer
	     sensorManager.unregisterListener(this);
		 
		 Toast.makeText(this, "Diabetes Planner stopped! Please restart!", Toast.LENGTH_SHORT).show();
		 Log.d("ACCELEROMETER", "access killed!");
	 }


	/**
	 *  onSensorChanged is called every time when the sensor gets new data
	 *  (several times per second)
	 *  
	 *  @author Tobias Baehr
	 *  @param event Managed by the Event handler
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values.clone();
	
		
		// check how many data per second should be accessed
		if (PauseSystem.gapSuitable()) {
			
			// write Accelerometer data into Log Cat
			//Log.d("x y z time", values[0]+" "+values[1]+" "+values[2]+" "+System.currentTimeMillis());
		
			// create collection of P.finTimeWindow * P.sampleRate values and send it to the Calculation class
			if (this.accelerometerCacheCounter < P.accelerometerCache.length) {
			
				// add Accelerometer data to the array/ collection
				P.accelerometerCache[this.accelerometerCacheCounter][0] = values[0];
				P.accelerometerCache[this.accelerometerCacheCounter][1] = values[1];
				P.accelerometerCache[this.accelerometerCacheCounter][2] = values[2];
				P.accelerometerCache[this.accelerometerCacheCounter][3] = System.currentTimeMillis();
				//Log.d("time in DataCollector", "" + P.accelerometerCache[this.accelerometerCacheCounter][3]);
			
				// increase counter of array
				this.accelerometerCacheCounter++;			
			}
			else {
				// start a new array/collection
				this.accelerometerCacheCounter = 0;
				
				// deliver the collection to the Calculation class
				// to process the Accelerometer data
				Log.d("COLLECTION OF SIZE " + P.accelerometerCache.length,"sent");
				 
				// call function to process the data -> preprocessRecords()
			    
				/*
			     * START CLASSIFICATION
			     * @author Mats
			     * @author Robert
			     */

			    final MySQLiteHelper testHelper = new MySQLiteHelper (this);	    
		        try {
		    	    InputStream model = getAssets().open(P.modelDataFileName);
		    	    
					double [][] preprocessedRecords = Calculation.preprocessRecords(P.accelerometerCache);
					String [][] labeledRecords      = Calculation.labelRecords(preprocessedRecords, model, this);
					final String []   aggregatedRecords   = Calculation.aggregateRecords(labeledRecords);
					
					//Generate Location String
					GPSTracker track = new GPSTracker(this);
					
					//String location = LocationLogic.getNoMatchLocation();
					final String location = LocationLogic.compareWithPredefLocations(testHelper, 
					track.getLatitude(), track.getLongitude());
									
					// if the location is unknown for a while (askUserForLocationAfter)
					// ask the user
					// (needs to be enabled in P)
					final Runnable runnerAskLoca = new Runnable() { public void run() {
					    	LocationLogic.askForLocation(location);
					}};
			
					try {
						threadHandler.post(runnerAskLoca);
						Log.d("THREAD askForLocation","started");
			        } catch (Exception e) {
			            e.printStackTrace();
			            Log.d("THREAD askForLocation","error");
			        }
			        

					//write aggregated records and location label to database
					final Runnable runnerDatabase = new Runnable() { public void run() {
						Calculation.writetoDatabase(testHelper, aggregatedRecords, location);	
					}};
		
					try {
						threadHandler.post(runnerDatabase);
						Log.d("THREAD writetoDatabase","started");
					} catch (Exception e) {
						e.printStackTrace();
						Log.d("THREAD writetoDatabase","error");
					}
					
		        } 
		        catch (Exception e) {
					Log.wtf("Diabetes Planner", "Something went wrong with classification!", e);
				}	

		        /*
		         * END CLASSIFICATION
		         */ 
			}
		
		}
		
	}
	
	/*
	 * (non-javadoc)
	 * 
	 * onAccuracyChanged is called every time the accuracy of the sensors changes
	 * (never called normally) 
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	
	}

	/*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// from Runnable
		
	}

}
