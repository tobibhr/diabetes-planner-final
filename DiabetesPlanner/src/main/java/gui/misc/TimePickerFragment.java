package gui.misc;

import gui.activities.AddActivity;

import java.util.Calendar;

import com.example.diabetesplanner.R;
import com.example.diabetesplanner.R.id;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.widget.TimePicker;
/**
 * This class is used as a fragment in the AddActivity class to manage the selection of a date using a Datepicker.
 * @author Oliver
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
    /**
     * Overwrite the current text in the timefield displaying the currently selected time in AddActivity.
     */
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	
    	AddActivity a = (AddActivity) getActivity();
    	a.selectedHour = hourOfDay;
    	a.selectedMinute = minute;
    	
    	EditText timefield = (EditText) getActivity().findViewById(R.id.tfCurrentTime);
    	timefield.setText(GeneralAlgorithms.DisplayTimeZeros(a.selectedHour, a.selectedMinute));
    }
 
  

}
