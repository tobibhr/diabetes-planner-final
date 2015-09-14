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

public class ActivityListAdapter extends ArrayAdapter<AbstractActivity>{

	private Context context;
	private ArrayList<AbstractActivity> objects;
	
	private ArrayList<DataSetObserver> observers = new ArrayList<DataSetObserver>();
	
	/**
	 * This method creates the list adapter and customize it
	 * @param context the context of the list
	 * @param ListItemLayout the layout of the activity list
	 * @param activities they are the activities that will be put in the list
	 */

	public ActivityListAdapter(Context context, int listItemLayout,
			ArrayList<AbstractActivity> activities) {
		super(context, listItemLayout, activities);
		this.context = context;
		objects = activities;
	}



	//for lists
	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int pos,View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_item_layout, parent, false);
		
		if(objects.get(pos) instanceof HumanActivity) {
			HumanActivity hA = 	(HumanActivity) objects.get(pos);
			
			TextView tvDuration = (TextView) rowView.findViewById(R.id.tvDuration);		
			tvDuration.setText(hA.getDurationCS()+ " min");
			
			rowView.setBackgroundColor(Color.rgb(228,103,100));		
			
		}
		else if (objects.get(pos) instanceof BloodSugar) {
			rowView.setBackgroundColor(Color.rgb(102,102,255));
		}
		else if (objects.get(pos) instanceof Carb) {
			rowView.setBackgroundColor(Color.rgb(0,102,255));
		}
		else if (objects.get(pos) instanceof Insulin) {
			rowView.setBackgroundColor(Color.rgb(102,0,255));
		}
		else{
			rowView.setBackgroundColor(Color.rgb(255,255,255));
		}
		
		TextView tvName = (TextView) rowView.findViewById(R.id.tvActName);		
		tvName.setText(objects.get(pos).getName());
		
		TextView tvStart = (TextView) rowView.findViewById(R.id.tvactStart);		
		tvStart.setText(objects.get(pos).getStartTimeAsString());
		
		TextView tvDate = (TextView) rowView.findViewById(R.id.tvActDate);		
		tvDate.setText(objects.get(pos).getDateasString());
	
		return rowView;
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer){
		observers.add(observer);
	}
	
	public void notifyDataSetChanged(){
		for(DataSetObserver o: observers){
			o.onChanged();
		}
	}
}
	
