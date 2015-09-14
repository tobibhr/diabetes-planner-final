package gui.items;

import gui.misc.GeneralAlgorithms;

import java.util.Calendar;

/**
 * This abstract class is made as a super class for MeasuringActivity and HumanActivity.
 * Since every activity has a name and a date it is a concise way to model this.
 * Furthermore it can be used to combine them in a common list using this supertype
 * and differentiate them by there subtypes. 
 * 
 * It implements Comparable<AbstractActivity> in order to be sortable
 * with the predefined method compareTo(Object o) that is overwritten in this class.
 * @author Oliver
 */

public abstract class AbstractActivity implements Comparable<AbstractActivity> {
	
	String name;
	public Calendar startTime;

	public AbstractActivity(String name,Calendar start){
		this.name = name;
		startTime = start;
	}
	
	/** This method defines how two objects of type AbstractActivity can be compared to each other
	 * to sort them appropriatly. It helps to order the items in descending order by date.
	 * 
	 */
	@Override
	public int compareTo(AbstractActivity a2){
		if(this.startTime.before(a2.startTime)){
			return 1;
		}
		else if(this.startTime.after(a2.startTime)){
			return -1;			
		}
		else{
			return 0;
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Calendar getStartTime(){
		return startTime;
	}
	
	
	public long getStartTimeAsLong(){
		long result= startTime.getTimeInMillis();
		return result;
	}
	
	/**
	 * Returns a string which represents the startTime of an activity in a conventional manner (e.g. 08:04)
	 */
	public String getStartTimeAsString(){
		
		int h = startTime.get(startTime.HOUR_OF_DAY);
		int m = startTime.get(startTime.MINUTE);
		
		String out = GeneralAlgorithms.DisplayTimeZeros(h, m);
		return out;
	}
	
	public String getDateasString(){
		
		int d = startTime.get(startTime.DAY_OF_MONTH);
		int m = startTime.get(startTime.MONTH);
		
		String dayZero="";
		String monthZero="";
		
		if(d<10){ dayZero="0"; }
		if(m<10){ monthZero="0"; }
		
		String out = dayZero + d + "/" + monthZero + (m+1);
		
		return out;
	}

}
