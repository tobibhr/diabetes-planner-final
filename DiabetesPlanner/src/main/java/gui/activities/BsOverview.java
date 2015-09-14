package gui.activities;

import gui.items.AbstractActivity;
import gui.items.BloodSugar;
import gui.items.Carb;
import gui.items.Insulin;
import gui.items.MeasuringActivity;
import gui.misc.ActivityListAdapter;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.diabetesplanner.DataExchange;
import com.example.diabetesplanner.GuiMain;
import com.example.diabetesplanner.R;
import com.example.diabetesplanner.R.id;
import com.example.diabetesplanner.R.layout;
import com.example.diabetesplanner.R.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * This class is responsible for the blood sugar view and the list of the blood sugar readings
 * @author Oliver
 */
public class BsOverview extends Activity {
	
	ArrayList<AbstractActivity> lastV = new ArrayList<AbstractActivity>();

	/**
	   * sets the content by setting the layout of the list
	   * @param savedInstanceState the save blood sugar
	   */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	

		
	    //Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_bs_overview);
		

		
		updateBsList();
		initView();

		setUnitTextFields();		
	}

	private void setUnitTextFields() {
		TextView bsu = (TextView) findViewById(R.id.tvUnitBs);
		TextView cu = (TextView) findViewById(R.id.tvCUnit);
		
		bsu.setText(DataExchange.bsUnit);
		cu.setText(DataExchange.cUnit);
	}

	/**
	   *  Inflates the menu; this adds items to the action bar if it is present.
	   * @param menu the view menu
	   */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bs_overview, menu);
		return true;
	}

	/**
	   * Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button,
	   *  so long as you specify a parent activity in AndroidManifest.xml.
	   * @param item the activity item 
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
	   * Handles the blood sugar activity data and the view of the activity list
	   */
	public void initView(){

		OnEditorActionListener ealBS = new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				
				try {
					int val =Integer.parseInt(v.getText()+"");	
					BloodSugar bsa = new BloodSugar(Calendar.getInstance(),val);
					lastV.add(bsa);
					//DataExchange.sort(lastV);	//TODO: Fix bug with sorting			
				} catch (Exception e) {	}
									
				v.setText("");
				hideKeyboard();
				

				
				return false;
			}
		};		EditText tfBsEntry = (EditText) findViewById(R.id.tfBsEntry);;
		tfBsEntry.setOnEditorActionListener(ealBS);
		
		
		ListView bsLView = (ListView)findViewById(R.id.lastEntriesList);
		ActivityListAdapter myLa = new ActivityListAdapter(this, R.layout.list_item_layout, lastV);		
		bsLView.setAdapter(myLa);
		myLa.registerDataSetObserver(new DataSetObserver() {});
		
		
		
		
		OnEditorActionListener ealI = new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				
				try {
					int val =Integer.parseInt(v.getText()+"");	
					Insulin bsa = new Insulin(Calendar.getInstance(),val);
					lastV.add(bsa);
					//DataExchange.sort(lastV);	//TODO: Fix bug with sorting			
				} catch (Exception e) {	}
									
				v.setText("");
				hideKeyboard();
				return false;
			}
		};
		EditText tfIEntry = (EditText) findViewById(R.id.tfIEntry);;
		tfIEntry.setOnEditorActionListener(ealI);
			
		ListView ILView = (ListView)findViewById(R.id.lastEntriesList);
		ActivityListAdapter myLaI = new ActivityListAdapter(this, R.layout.list_item_layout, lastV);		
		ILView.setAdapter(myLaI);
		myLaI.registerDataSetObserver(new DataSetObserver() {});
		
		
		
		
		OnEditorActionListener ealC = new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				
				try {
					int val =Integer.parseInt(v.getText()+"");	
					Carb c = new Carb(Calendar.getInstance(),val);
					lastV.add(c);
					//DataExchange.sort(lastV);	//TODO: Fix bug with sorting			
				} catch (Exception e) {	}
									
				v.setText("");
				hideKeyboard();
				

				
				return false;
			}
		};
		EditText tfCEntry = (EditText) findViewById(R.id.tfCEntry);;
		tfCEntry.setOnEditorActionListener(ealC);
		
		
		ListView cLView = (ListView)findViewById(R.id.lastEntriesList);
		ActivityListAdapter myLa3 = new ActivityListAdapter(this, R.layout.list_item_layout, lastV);		
		cLView.setAdapter(myLa3);
		myLa3.registerDataSetObserver(new DataSetObserver() {});
	}
	/**
	 * This method will make the software keyboard invisible to the user.
	 * It is typically invoked when the text input is done, so that the whole screen is available to display the
	 * whole content again.
	 * 
	 */
	private void hideKeyboard() {   
	    // Check if no view has focus:
	    View view = this.getCurrentFocus();
	    if (view != null) {
	        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
	
	/**
	 *Updates the blood sugar list on the BsOverview with the overall consistent list in the DataExchange class.
	 *It is invoked after entering the BsOverview-Activity.
	 */
	public void updateBsList() {
		ArrayList<AbstractActivity> a = DataExchange.getData();
		for(int i=0;i<a.size();i++){
			if(a.get(i) instanceof MeasuringActivity){
				lastV.add((AbstractActivity) a.get(i));
			}
		}	
	}
	
	/**
	 * Return to the GuiMain-Activity.
	 * Is invoked when the user presses the button on the bottom of the BsOverview-Activity.
	 * @param view the list view
	   */
	public void backToMain(View view){
		Intent i = new Intent(this,GuiMain.class);
		startActivity(i);
		lastV = new ArrayList<AbstractActivity>();
	}
}
