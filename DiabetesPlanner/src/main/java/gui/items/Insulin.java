package gui.items;

import java.util.Calendar;

import com.example.diabetesplanner.DataExchange;

public class Insulin extends MeasuringActivity{

	public Insulin(Calendar time, int val) {
		super(time, val, DataExchange.iUnit);
	}
}
