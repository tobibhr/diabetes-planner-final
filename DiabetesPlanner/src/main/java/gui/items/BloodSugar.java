package gui.items;

import java.util.Calendar;

import com.example.diabetesplanner.DataExchange;

public class BloodSugar extends MeasuringActivity{

	public BloodSugar(Calendar time, int val) {
		super(time, val, DataExchange.bsUnit);
	}
}
