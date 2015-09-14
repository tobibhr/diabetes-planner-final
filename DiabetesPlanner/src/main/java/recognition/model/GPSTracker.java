package recognition.model;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

/** GPS tracker class that is used to provide a GPS tracking service for our android application.
 *  Providing the location constantly on the request of the user.
 * 	@author mahmoud
 *  @version 1.0
 * 
 */
public class GPSTracker extends Service implements LocationListener {
	
	private final Context mContext;
	 
    // flag for GPS status if its enabled or not to be used if not as an alert message
    boolean isGPSEnabled = false;
 
    // flag for network status if available or not
    boolean isNetworkEnabled = false;
 
    boolean canGetLocation = false;
 
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
 
    // The minimum distance to change Updates in meters (can be changed as we please)
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
 
    // Declaring a Location Manager
    protected LocationManager locationManager;
 
   /**this is the class constructor for the GPS tracker which creates the GPS tracking object
    * @param context
    * 
    * @author mahmoud
    * @version 1.0
    */
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    /** method that returns the geolocation of the android phone during the moment its called.
     *  The location object returned has longitudes and latitudes with a timestamp which are the attributes
     *  of the object returned.
     * 
     * @return location	the current location 
     * @author mahmoud
     * 
     */
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
 
            // getting GPS status (changed to true if the location is activated on the phone)
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
 
            // getting network status (changed to true if network is available)
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }
    
    /** method that returns the Latitude of the location
     * @see package com.example.diabetesplanner {@link #getLocation()}
     * @return latitude number in a double formate indicating the latitude of the location
     * @author mahmoud
     */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
         
        // return latitude
        return latitude;
    }
     
    /** method that returns the Longitude of the location
     * @see package com.example.diabetesplanner {@link #getLocation()} 
     * @return Longitude number in a double format indicating the Longitude of the location
     * @author mahmoud
     */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
         
        // return longitude
        return longitude;
    }
    
    /**
     * method to check if the location could actually be processed as in the location service is activated 
     * as well as there is a network provider.
     * @see package com.example.diabetesplanner {@link #getLocation()}
     * @return boolean returns true if can get location
     * @author mahmoud
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
     
 
    /** method that creates an alert dialog box to activated the GPS location service on the android phone showed it be
     * closed by the user in order to collect the data. it directs the user to the location settings to activated the 
     * location service. 
     * 
     *@author mahmoud
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }
    
    /** method that stops the GPS listener in the application when needed
     * @author mahmoud 
     */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }       
    }
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}

