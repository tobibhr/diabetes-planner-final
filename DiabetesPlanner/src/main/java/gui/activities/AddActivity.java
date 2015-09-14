package gui.activities;

import gui.items.HumanActivity;
import gui.misc.DatePickerFragment;
import gui.misc.GeneralAlgorithms;
import gui.misc.TimePickerFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

import recognition.model.MySQLiteHelper;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.diabetesplanner.GuiMain;
import com.example.diabetesplanner.R;
/**
 * This class is The add activity class which allows the user to add activity in the application
 * @author Oliver
 */

public class AddActivity extends Activity {
	
	Calendar c;
	
	String activityName;
	int duration;
	Calendar startTime;
	
	Spinner spActivities;

	public int selectedYear;
	public int selectedMonth;
	public int selectedDay;
	public int selectedHour;
	public int selectedMinute;
	
	EditText selectedTime;
	EditText selectedDate;
	
	EditText tfDuration;

	private String[] activityArray;

	/**
	 * An override method for onCreate to add the activity to the layout
	 * @param SavedIntanceState the saved activity 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//initialize var
		activityName = "";
		duration=-1;
		startTime = Calendar.getInstance();
		Context context= getApplicationContext();
		activityArray = context.getResources().getStringArray(R.id.activityList) ;
		
		
		
	    //Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_add_activity);
		init();
		setupSpinner();
		setuptextfield();
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			activityName = savedInstanceState.getString("name");
			startTime = (Calendar) savedInstanceState.get("time");
		}
		
		if(!activityName.equals(spActivities.getSelectedItem())){
			int pos = -1;
			for(int x=0;x<activityArray.length;x++){
				if(activityArray[x].equals(activityName)){
					pos = x;
				}
			spActivities.setSelection(pos);
			System.out.println(pos);
			spActivities.findViewById(R.id.spActivities).refreshDrawableState();
			
			}
		}

		
		//TODO implement editing of activities correctly
	}

	private void setuptextfield() {

		OnEditorActionListener eal = new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				
				duration = Integer.parseInt(v.getText()+"");
				
				return false;
			}
		};
		
		tfDuration.setOnEditorActionListener(eal);
		
	}
	/**
	 * An override method for adding items to the action bar if it is present.
	 * @param menu the menu of the activities 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}

	/**
	 * An override method for Handling action bar item clicks here. The action bar will 
	 * automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
	 * @param item the item in the menu
	 * @return the selected item
	 */
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
	
	/**
	 * This method organizes the menu of the activity or rather initiate it by viewing the activities and putting the selected item
	 * with the day, month, year, minute and hour in the list in the add activity.xml
	 */
	public void init(){
		
		spActivities = (Spinner) findViewById(R.id.spActivities);
		
		selectedTime = (EditText) findViewById(R.id.tfCurrentTime);
		selectedDate = (EditText) findViewById(R.id.tfCurrentDate);
		
		tfDuration = (EditText) findViewById(R.id.tfDuration);
		
		c = Calendar.getInstance();
		selectedDay= c.get(Calendar.DAY_OF_MONTH);
		selectedMonth= c.get(Calendar.MONTH); //+1 because it starts with 0
		selectedYear= c.get(Calendar.YEAR);
		selectedMinute= c.get(Calendar.MINUTE);
		selectedHour= c.get(Calendar.HOUR_OF_DAY);
		
		
		
		String dateString = GeneralAlgorithms.DisplayDateZeros(selectedDay, selectedMonth+1, selectedYear);
		String timeString = GeneralAlgorithms.DisplayTimeZeros(selectedHour, selectedMinute);
			
		
		selectedDate.setText(dateString);
		selectedTime.setText(timeString);
	
	}
	
	/**
	 * This method creates the time dialog in the GUI
	 * @param v it is the view containing containing the GUI in add activity xml
	 */
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	
	/**
	 * This method creates the date catalog in the GUI 
	 * @param v the view containing the GUI in add activity xml
	 */
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	/**
	 * This is a customization method that takes the data inserted in the AddActivity.xml and adds it to the activity list
	 * It also organizes the layout of the list
	 */
	public void setupSpinner(){
	    ArrayAdapter<CharSequence> adapterActivity = ArrayAdapter.createFromResource(this,R.array.activities,android.R.layout.simple_spinner_item);
	    adapterActivity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spActivities.setAdapter(adapterActivity);
	    spActivities.setOnItemSelectedListener( new OnItemSelectedListener(){
	    	//TODO implement Edit of Activity
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(activityName.equals("")){
					activityName = (String) spActivities.getItemAtPosition(position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
	    	
	    });
	}
	
	/**
	 * This method calculates the calendar and sets it and then adds the activity with the corresponding calendar times.
	 * @param view the view containing the GUI data for this class
	 */
	public void submit(View view){
		//TODO enable proper db writing
		Calendar cal = new GregorianCalendar(selectedYear, selectedMonth , selectedDay, selectedHour, selectedMinute, Calendar.getInstance().get(Calendar.SECOND)); 
		
		HumanActivity ha = new HumanActivity(activityName, cal, duration);
		//DataExchange.addItem(ha);
		
		
		MySQLiteHelper h = new MySQLiteHelper(this);
		//h.addRecord(h, ha.getStartTimeAsLong(), ha.getName(), "No Location entered");
		
		Intent i = new Intent(this,GuiMain.class);
		startActivity(i);
	} 
	
}
