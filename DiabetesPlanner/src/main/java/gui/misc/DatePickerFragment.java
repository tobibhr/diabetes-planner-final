package gui.misc;

import gui.activities.AddActivity;

import java.util.Calendar;

import com.example.diabetesplanner.R;
import com.example.diabetesplanner.R.id;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

/**
 * This class is used as a fragment in the AddActivity class to manage the selection of a date using a Datepicker.
 * @author Oliver
 */
public class DatePickerFragment extends DialogFragment implements OnDateSetListener{
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month,day);
    }

    /**
     * Overwrite the current text in the datefield displaying the currently selected date in AddActivity.
     */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
    	AddActivity a = (AddActivity) getActivity();
    	a.selectedYear = year;
    	a.selectedMonth = monthOfYear;
    	a.selectedDay = dayOfMonth;
    	
    	EditText datefield = (EditText) getActivity().findViewById(R.id.tfCurrentDate);
		String dateString = GeneralAlgorithms.DisplayDateZeros(a.selectedDay, a.selectedMonth+1, a.selectedYear);
    	datefield.setText(dateString);
		
	}
 

}
