package com.example.diabetesplanner;

import gui.activities.ChartCompare;
import gui.items.AbstractActivity;
import gui.items.BloodSugar;
import gui.items.Carb;
import gui.items.HumanActivity;
import gui.items.Insulin;
import gui.misc.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.app.Application;
import android.util.Log;
/**
 * This class is responsible for data exchange in the array list
 * @author Oliver
 */

public  class DataExchange extends Application {
	
	  
	public static ArrayList<AbstractActivity> actList = new ArrayList<AbstractActivity>();
	static ArrayList<AbstractActivity> fractions = new ArrayList<AbstractActivity>();
	static ArrayList<Location> locationList = new ArrayList<Location>();  
	
	static HumanActivity current= null;
	
	
	
	public static int mainInvocation = 0;
	  
	public static String bsUnit = "mg/dl";  
	public static String cUnit = "KE";  
	public static String iUnit = "doses";
	
	  
	  public static ArrayList<AbstractActivity> getData() {Collections.sort(actList); return actList;}
	  
	  public static void setData(ArrayList<AbstractActivity> input) {actList = input;}
	  
	  public static void addItem(AbstractActivity act) { actList.add(act); Collections.sort(actList);}
	  
	  public static void addListOfItems(ArrayList<AbstractActivity> acts) { 
		   for(int i=0;i<acts.size();i++){ actList.add(acts.get(i));}		  
		  }

	  /**
	   * Sorts the list Abstract activity
	   * gets the parameters and puts them in a sorted order
	   * @param list the activity list
	   */
//	  public static ArrayList<AbstractActivity> sort(ArrayList<AbstractActivity> list){
//		  
//		  System.out.println("sort invoked");
//		  for(int x=0;x<list.size()-1;x++){
//		  for(int i=x;i<list.size()-1;i++){
//			  int j=i+1;
//				  AbstractActivity a= list.get(i);
//				  AbstractActivity b= list.get(j);
//				  int posA = list.indexOf(a);
//				  int posB = list.indexOf(b);
//				  
//				  if(b.getStartTime().before(a.getStartTime())){
//					  list_a.set(posA, b);
//					  list_a.set(posB, a);
//					  System.out.println("switched");
//				  }
//			  }
//		  }
//		  return list;
//	  }
//}
	  

	  

	  //eigener Algo
//  public static ArrayList<AbstractActivity> sort(ArrayList<AbstractActivity> list){
//	  for(int i=0;i<list.size()-1;i++){
//		  for(int j=i+1;j<list.size();j++){
//		  
//			  AbstractActivity a= list.get(i);
//			  AbstractActivity b= list.get(j);
//		  
//			  int posA = list.indexOf(a);
//		  	  int posB = list.indexOf(b);
//		  
//		  	  if(a.getStartTime().before(b.getStartTime())){
//		  		  list_a.set(posA, b);
//		  		  list_a.set(posB, a);					  
//		  	  }
//	  	  }
//	   }
//	  return list;
//	}
  
  public static void addItemFraction(AbstractActivity fraction){
	  //TODO enable proper db reading
	  
	  fractions.add(fraction);
	  
	  String nameCurr = fraction.getName();
	  String namePrevious = "";
	  if(current!=null){ 
		  namePrevious = current.getName();
	  }
	  
	  
	  if(nameCurr.equals(namePrevious)){
		  current.setDurationMinutes(current.durationMinutes+2);
	  }
	  else{
		  if(current!=null){ //check here
			  actList.add(current);
		  }

		  current = (HumanActivity) fraction;
		  Log.d("DataExchange - fraction", "New Activity recognized");
	  }
	  Log.d("DataExchange -fractionlog",(fraction.getName() + ", " + fraction.getStartTimeAsString()));
  }

	public static ArrayList<Location> getLocations() {
		return locationList;
	}
	
	/**Used to simulate some entries to test functionality of the GUI. 
	 * @return arraylist containing sample data (bloodsugar,insulin,carb and activities)
	 * 
	 * @author Oliver
	 */
	public static ArrayList<AbstractActivity> createSampleData() {

		ArrayList<AbstractActivity> samples = new ArrayList<AbstractActivity>();

		//today's year
		int y= Calendar.getInstance().get(Calendar.YEAR);
		//today's month
		int m= Calendar.getInstance().get(Calendar.MONTH);
		//today's day
		int d= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

		
		Calendar day1_1 = Calendar.getInstance(); day1_1.set(y, m, d-3, 8, 9);
		Calendar day1_2 = Calendar.getInstance(); day1_2.set(y, m, d-3, 16, 5);
		Calendar day1_3 = Calendar.getInstance(); day1_3.set(y, m, d-3, 20, 0);
		
		Calendar day2_1 = Calendar.getInstance(); day2_1.set(y, m, d-2, 8, 00);
		Calendar day2_2 = Calendar.getInstance(); day2_2.set(y, m, d-2, 13, 30);
		
		Calendar day3_1 = Calendar.getInstance(); day3_1.set(y, m, d-1, 10, 15);
		Calendar day3_2 = Calendar.getInstance(); day3_2.set(y, m, d-1, 12, 11);
		Calendar day3_3 = Calendar.getInstance(); day3_3.set(y, m, d-1, 15, 19);
		Calendar day3_4 = Calendar.getInstance(); day3_4.set(y, m, d-1, 17, 11);
		
		Calendar day4_1 = Calendar.getInstance(); day4_1.set(y, m, d, 12, 50);
		Calendar day4_2= Calendar.getInstance(); day4_2.set(y, m, d, 20, 11);

		
		BloodSugar bsa1 = new BloodSugar(day1_1, 100);
		Carb c1 = new Carb(day1_1, 1);		
		Insulin i1 = new Insulin(day1_1, 2);		
		
		BloodSugar bsa2 = new BloodSugar(day1_2, 80);
		Carb c2 = new Carb(day1_2, 2);		
		Insulin i2 = new Insulin(day1_2, 3);		
			
		BloodSugar bsa3 = new BloodSugar(day1_3, 170);
		Carb c3 = new Carb(day1_3, 4);
		Insulin i3 = new Insulin(day1_3, 1);
		
		
		BloodSugar bsa4 = new BloodSugar(day2_1, 160);
		Carb c4 = new Carb(day2_1, 3);		
		Insulin i4 = new Insulin(day2_1, 8);			
		
		BloodSugar bsa5 = new BloodSugar(day2_2, 100);
		Carb c5 = new Carb(day2_2, 6);	
		Insulin i5 = new Insulin(day2_2, 2);
		
		
		BloodSugar bsa6 = new BloodSugar(day3_1, 110);
		Carb c6 = new Carb(day3_1, 2);
		Insulin i6 = new Insulin(day3_1, 3);
		
		BloodSugar bsa7 = new BloodSugar(day3_2, 90);
		Carb c7 = new Carb(day3_2, 8);
		Insulin i7 = new Insulin(day3_2, 2);
		
		BloodSugar bsa8 = new BloodSugar(day3_3, 180);
		BloodSugar bsa9 = new BloodSugar(day3_4, 210);
		
		
		BloodSugar bsa10 = new BloodSugar(day4_1, 280);
		BloodSugar bsa11 = new BloodSugar(day4_2, 210);

		samples.add(c1);
		samples.add(c2);
		samples.add(c3);
		samples.add(c4);
		samples.add(c5);
		samples.add(c6);
		samples.add(c7);

		samples.add(i1);
		samples.add(i2);
		samples.add(i3);
		samples.add(i4);
		samples.add(i5);
		samples.add(i6);
		samples.add(i7);

		samples.add(bsa1); samples.add(bsa2);
		samples.add(bsa3); samples.add(bsa4);
		samples.add(bsa5); samples.add(bsa6);
		samples.add(bsa7); samples.add(bsa8);
		samples.add(bsa9); samples.add(bsa10);
		samples.add(bsa11);		
		
		Calendar day1_1_a = Calendar.getInstance(); day1_1_a.set(y, m, d-3, 8, 11);
		Calendar day1_2_a = Calendar.getInstance(); day1_2_a.set(y, m, d-3, 13, 11);
		Calendar day1_3_a = Calendar.getInstance(); day1_3_a.set(y, m, d-3, 16, 11);
		Calendar day2_1_a = Calendar.getInstance(); day2_1_a.set(y, m, d-2, 8, 11);
		Calendar day2_2_a = Calendar.getInstance(); day2_2_a.set(y, m, d-2, 14, 11);
		Calendar day3_2_a = Calendar.getInstance(); day3_2_a.set(y, m, d-1, 15, 11);
		Calendar day3_3_a = Calendar.getInstance(); day3_3_a.set(y, m, d-1, 16, 11);
		Calendar day3_4_a = Calendar.getInstance(); day3_4_a.set(y, m, d-1, 10, 11);
		Calendar day4_1_a = Calendar.getInstance(); day4_1_a.set(y, m, d-1, 15, 11);
		

		HumanActivity ha1 = new HumanActivity("Running", day1_1_a, 80);
		HumanActivity ha2 = new HumanActivity("Walking", day1_2_a, 90);
		HumanActivity ha3 = new HumanActivity("Sitting", day1_3_a, 75);
		HumanActivity ha4 = new HumanActivity("Walking", day2_1_a, 80);
		HumanActivity ha5 = new HumanActivity("Sitting", day2_2_a, 18);
		HumanActivity ha7 = new HumanActivity("Sitting", day3_2_a, 50);
		HumanActivity ha8 = new HumanActivity("Recumbency", day3_2_a, 210);
		HumanActivity ha9 = new HumanActivity("Sitting", day3_3_a, 30);


		samples.add(ha1); samples.add(ha2);
		samples.add(ha3); samples.add(ha4);
		samples.add(ha5); 
		samples.add(ha7); samples.add(ha8);
		samples.add(ha9);

		Collections.sort(samples);

		return samples;
	}
}



