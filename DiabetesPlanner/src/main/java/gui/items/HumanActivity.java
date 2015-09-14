package gui.items;

import java.util.Calendar;
import java.util.Date;

import com.example.diabetesplanner.DataExchange;

/**
 * This class contains all relevant attributes of an activity(movement,sitting,sleep) which has a certain duration. 
 * It is used to represent such an action in the application.
 * @author Oliver
 * @author Mats
 */
public class HumanActivity extends AbstractActivity {
	

	boolean highlvl;
	public int durationMinutes;
	public Calendar endTime;

	
	public HumanActivity(String name, Calendar start,int duration){
		super(name,start);
		highlvl=false;
		durationMinutes=duration;
	}


	/**
	 * Returns the duration as charsequence, typically invoked by methods which want to display it in a textview.
	 */
	public CharSequence getDurationCS() {
		CharSequence durationCS = durationMinutes+"";
		return durationCS;
	}


	public int getDurationMinutes() {
		return durationMinutes;
	}

	public int getEndTime() {
		return durationMinutes;
	}
	
	public void setEndTime(long endTime) {
		
		// Convert long to Calendar
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(endTime);
		
		this.endTime = cal;
	}

	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}



}
