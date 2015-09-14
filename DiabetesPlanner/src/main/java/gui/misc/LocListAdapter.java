package gui.misc;

import gui.activities.AddActivity;
import gui.items.AbstractActivity;
import gui.items.BloodSugar;
import gui.items.Carb;
import gui.items.HumanActivity;
import gui.items.Insulin;

import java.util.ArrayList;

import com.example.diabetesplanner.R;
import com.example.diabetesplanner.R.id;
import com.example.diabetesplanner.R.layout;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * This class is a customized class that allows us to customize the layout of the activity list and it imports the array list of java
 * @author Oliver
 */

public class LocListAdapter extends ArrayAdapter<Location>{

	private Context context;
	private ArrayList<Location> objects;
	
	/**
	 * This method creates the list adapter and customize it
	 * @param context the context of the list
	 * @param ListItemLayout the layout of the activity list
	 * @param activities they are the activities that will be put in the list
	 */

	public LocListAdapter(Context context, int listItemLayout,
			ArrayList<Location> locations) {
		super(context, listItemLayout, locations);
		this.context = context;
		objects = locations;
	}



	//for lists
	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int pos,View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.loc_list_item_layout, parent, false);
		

			Location l = objects.get(pos);
			
			TextView tvX = (TextView) rowView.findViewById(R.id.tvX);		
			tvX.setText("X: "+ l.X);
			
			TextView tvY = (TextView) rowView.findViewById(R.id.tvY);		
			tvY.setText("Y: "+ l.Y);
			
			TextView tvname = (TextView) rowView.findViewById(R.id.tvLocName);		
			tvname.setText(l.name);
			
			rowView.setBackgroundColor(Color.rgb(228,103,100));		
				
		return rowView;
	}
}
