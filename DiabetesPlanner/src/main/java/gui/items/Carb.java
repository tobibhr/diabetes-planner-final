package gui.items;

import java.util.Calendar;

import com.example.diabetesplanner.DataExchange;

public class Carb extends MeasuringActivity{

	public Carb(Calendar time, int val) {
		super(time, val, DataExchange.cUnit);
	}
}
