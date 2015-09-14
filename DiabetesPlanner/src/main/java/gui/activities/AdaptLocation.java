

package gui.activities;

import recognition.model.LocationLogic;
import recognition.model.MySQLiteHelper;

import com.example.diabetesplanner.GuiMain;
import com.example.diabetesplanner.R;
import com.example.diabetesplanner.R.id;
import com.example.diabetesplanner.R.layout;
import com.example.diabetesplanner.R.menu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * AdaptLocation shows previously mapped locations and makes it possible
 * to adapt previous locations
 * 
 * @author Tobias
 *
 */
public class AdaptLocation extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adapt_location);
		
		// update location function
		// !!! frontend needs to be implemented
		// data is sample data
		updateLocation("kitchen", GuiMain.track.getLatitude(), GuiMain.track.getLongitude());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.adapt_location, menu);
		return true;
	}
	
	/**
	 * updateLocation updates previous locations that are close to each other
	 * 
	 * @param newValue which is updated into the database
	 * @param lat latitude GPS coordinates
	 * @param lon longitude GPS coordinates
	 * 
	 * @author Tobias
	 *
	 */
	public void updateLocation(String newValue, double lat, double lon) {
		// connect to the SQLite database
		MySQLiteHelper dbHelper = new MySQLiteHelper (this);
		
		// get the primary key (time) of close locations in the database 
		String times = LocationLogic.similarLocations(dbHelper, lat, lon);
		Log.d("LOCATIONS",times);
				
		// update the previous locations
		if(dbHelper.updateLocations(dbHelper, times, newValue))
				Toast.makeText(this, "Location was updated", Toast.LENGTH_SHORT).show();
		else
				Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
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
