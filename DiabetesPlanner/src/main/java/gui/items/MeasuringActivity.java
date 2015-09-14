package gui.items;

import java.util.Calendar;

import com.example.diabetesplanner.DataExchange;

/**
 * This class contains all relevant attributes of a blood sugar measuring action and is used to represent such an
 * action in the application.
 * @author Oliver
 */

public class MeasuringActivity extends AbstractActivity {

	public int value = -1;
	String unit = null;
	
	public MeasuringActivity(Calendar start, int val,String unit) {
		super(val +" " + unit, start);
		this.value = val;
		this.unit = unit;
		DataExchange.addItem(this);
		//DataExchange.sort(DataExchange.actList);
	}
	
}
