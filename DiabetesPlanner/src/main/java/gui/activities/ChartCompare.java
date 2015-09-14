package gui.activities;

import gui.items.AbstractActivity;
import gui.items.BloodSugar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;

import com.example.diabetesplanner.DataExchange;
import com.example.diabetesplanner.R;
import com.example.diabetesplanner.R.id;
import com.example.diabetesplanner.R.layout;
import com.example.diabetesplanner.R.menu;

import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;



public class ChartCompare extends Activity {

	static ArrayList<AbstractActivity> allList;
	
	//data for blood sugar line in chart 1
	XYSeries bsSeriesMo = new XYSeries("Mon");
	XYSeries bsSeriesTue = new XYSeries("Tue");
	XYSeries bsSeriesWed = new XYSeries("Wed");
	XYSeries bsSeriesThu = new XYSeries("Thu");
	XYSeries bsSeriesFr = new XYSeries("Fri");
	XYSeries bsSeriesSa = new XYSeries("Sat");
	XYSeries bsSeriesSo = new XYSeries("Sun");

	
	//renderer for certain lines
	XYSeriesRenderer lineRendererMo  = new XYSeriesRenderer();
	XYSeriesRenderer lineRendererTue  = new XYSeriesRenderer();
	XYSeriesRenderer lineRendererWed  = new XYSeriesRenderer();
	XYSeriesRenderer lineRendererThu  = new XYSeriesRenderer();
	XYSeriesRenderer lineRendererFr  = new XYSeriesRenderer();
	XYSeriesRenderer lineRendererSa  = new XYSeriesRenderer();
	XYSeriesRenderer lineRendererSo  = new XYSeriesRenderer();
	
	
	//renderer for a set of bars and/or lines
	XYMultipleSeriesRenderer mRendererChart1 = new XYMultipleSeriesRenderer();
	
	//Datasets which collect the different datasets of multiple series
	XYMultipleSeriesDataset datasetChart1 = new XYMultipleSeriesDataset();
	
	/**
	 * This method is invoked immediately when the activity is created.
	 * It is comparable to the main-method in a normal java-class, but for activities.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

	    this.requestWindowFeature(Window.FEATURE_NO_TITLE); 	    //Remove title bar
	    
		setContentView(R.layout.activity_chart);

		int l = R.layout.activity_chart;
		if (l  == 0) { Log.w("ChartCompare", "ChartLayout is null"); }		
		
		//create some blood sugar and human activities
		createSampleBsLastWeek();
		//allList = DataExchange.getData();
		
		createCharts();
		
		createXLabels();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chart, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	


	/**
	 * Creates two charts which display all the recorded data, which is blood sugar devation, food and insulin consumption in chart 1 as well as
	 * performed activities with duration in chart2.
	 */
	private void createCharts() {
		
		splitData();
		
		lineRendererMo.setColor(Color.BLUE);
		lineRendererTue.setColor(Color.BLACK);
		lineRendererWed.setColor(Color.YELLOW);
		lineRendererThu.setColor(Color.RED);
		lineRendererFr.setColor(Color.GREEN);
		lineRendererSa.setColor(Color.MAGENTA);
		lineRendererSo.setColor(Color.LTGRAY);
		
//		lineRendererSo.setPointStyle(PointStyle.CIRCLE);
//		lineRendererSo.setFillPoints(true);
		
		//Main renderer to draw chart (may include more than one xyseriesrenderer)
			mRendererChart1 = new  XYMultipleSeriesRenderer();
			setupChartRenderer(mRendererChart1);
			mRendererChart1.addSeriesRenderer(lineRendererMo);
			mRendererChart1.addSeriesRenderer(lineRendererTue);
			mRendererChart1.addSeriesRenderer(lineRendererWed);
			mRendererChart1.addSeriesRenderer(lineRendererThu);
			mRendererChart1.addSeriesRenderer(lineRendererFr);
			mRendererChart1.addSeriesRenderer(lineRendererSa);
			mRendererChart1.addSeriesRenderer(lineRendererSo);


			mRendererChart1.setYTitle("mg/dl",0);
			
			mRendererChart1.setYAxisMax(300);
			mRendererChart1.setYAxisMin(0);
			mRendererChart1.setXAxisMax(6);
			mRendererChart1.setXAxisMin(0);
			
			mRendererChart1.setShowAxes(true);
			

		//add series data from above to a certain dataset (which is needed to create a chartView)
			datasetChart1.addSeries(bsSeriesMo);	
			datasetChart1.addSeries(bsSeriesTue);
			datasetChart1.addSeries(bsSeriesWed);
			datasetChart1.addSeries(bsSeriesThu);
			datasetChart1.addSeries(bsSeriesFr);
			datasetChart1.addSeries(bsSeriesSa);
			datasetChart1.addSeries(bsSeriesSo);
			
		//create view of chart1 which connects data and functions to xml file
			final GraphicalView viewChart1 = ChartFactory.getLineChartView(this.getBaseContext(), datasetChart1,mRendererChart1);
			LinearLayout chartLyt = (LinearLayout) findViewById(R.id.chart1);
			chartLyt.addView(viewChart1);
	}

	/**
	 * This method detects the type of each item in the arraylist allList and sorts it into the corresponding
	 * series.
	 * @param a 
	 */
	private void splitData() {
		
		int entryCount = 0;
	
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(cal.getTimeInMillis()-604800000);
		
		Log.d("ChartCompare - Timelog", "CurrentDate - 1 week :" + cal.getTime());
		int c=0;
		
		for(AbstractActivity a: allList){

				if(a instanceof BloodSugar){
				
				Log.d("ChartCompare - Timelog", "Date of Activity " +c +": " + a.startTime.getTime());
					
				 if(a.startTime.after(cal)){

					BloodSugar b = (BloodSugar) a; 
					

					
					int startHour = b.startTime.get(Calendar.HOUR_OF_DAY);
					
					if(0<=startHour && startHour<10){
						entryCount=0;
					}
					else if (10<=startHour && startHour<12) {
						entryCount=1;
					}
					else if (12<=startHour && startHour<15) {
						entryCount=2;
					}
					else if (15<=startHour && startHour<18) {
						entryCount=3;
					}
					else if (18<=startHour && startHour<21) {
						entryCount=4;
					}
					else if(21<=startHour) {
						entryCount=5;
					}
					else {
						Log.d("ChartCompare - Wrong Bucketing ","startHour " +startHour+ " does not fit in any bucket");
					}
					
					int weekDay = b.startTime.get(Calendar.DAY_OF_WEEK);

					if(weekDay == Calendar.MONDAY)   { bsSeriesMo.add(entryCount,b.value); }
					else if(weekDay == Calendar.TUESDAY)  { bsSeriesTue.add(entryCount,b.value); }
					else if(weekDay == Calendar.WEDNESDAY){ bsSeriesWed.add(entryCount,b.value); }
					else if(weekDay == Calendar.THURSDAY) { bsSeriesThu.add(entryCount,b.value); }
					else if(weekDay == Calendar.FRIDAY)   { bsSeriesFr.add(entryCount,b.value); }
					else if(weekDay == Calendar.SATURDAY) { bsSeriesSa.add(entryCount,b.value); }
					else if(weekDay == Calendar.SUNDAY)   { bsSeriesSo.add(entryCount,b.value); }
					else {
						Log.d("ChartCompare - Wrong Bucketing ","Weekday-Number " +weekDay+ " does not fit in any bucket");
					}
				}
			}
		 }
	}
		
	/**
	 * Creates a new chart renderer and define some Settings how the rendered Chart should look like.
	 */
	private XYMultipleSeriesRenderer setupChartRenderer(XYMultipleSeriesRenderer mRenderer) {
		


		
		mRenderer.setMarginsColor(Color.WHITE); //transparent margins to avoid a black border
		mRenderer.setShowGrid(true);
		
		//set sizes,color,padding and align of the lables of the labels(=values on the axis)
		mRenderer.setLabelsTextSize(13);		

		mRenderer.setXLabelsAlign(Align.CENTER);
		mRenderer.setXLabelsPadding(15);
		mRenderer.setXLabelsColor(Color.BLUE);
		
		mRenderer.setYLabelsAlign(Align.RIGHT, 0);
		mRenderer.setYLabels(10);
		mRenderer.setYLabelsColor(0,Color.BLUE);
		mRenderer.setYLabelsPadding(12);

		mRenderer.setAxesColor(Color.BLACK);
		
		mRenderer.setAxisTitleTextSize(15);
		mRenderer.setMargins(new int[]{40,60,160,10});
		

		mRenderer.setPointSize(5);
		
		//disable zoom functionality
		mRenderer.setZoomRate(0.2f);
		mRenderer.setZoomEnabled(false, false);

		//hide legend
		mRenderer.setShowLegend(true);	
		mRenderer.setLegendHeight(80);
	
		//avoid cutting off xlabels by hiding legend
		int[] margins = mRenderer.getMargins();
		margins[2] = (int) mRenderer.getLabelsTextSize() + 9;
		mRenderer.setMargins(margins);
		
		
		//limits for scrolling (-1 instead of 0 to display first label, )
		mRenderer.setPanLimits(new double[]{-1,allList.size(),0,10});

		//delete original labels, replace them later by customized ones
		mRenderer.setXLabels(0);
		
	    mRenderer.setDisplayValues(true);

		return mRenderer;
	}

	/**
	 * This method creates customized x-labels which display time and date of each activity and empty space in the other chart.
	 */
	private void createXLabels() {

//		int horLabelSpacing=0;
//	
//		String currentweekDay;
//		String baseWeekDay;
//		SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US);
//
//		Calendar firstElem= allList.get(0).startTime;
//		
//		baseWeekDay = dayFormat.format(firstElem.getTime());
//		
//		for(AbstractActivity a:allList){
//
//		  if(a instanceof BloodSugar){
//			BloodSugar b = (BloodSugar) a;
//			Calendar calendar = b.startTime;
//			currentweekDay = dayFormat.format(calendar.getTime());
//			
//			if(!baseWeekDay.equals(currentweekDay)){return;}
//			
//			Date d = a.startTime.getTime();
//			String dayString =  new SimpleDateFormat("E",Locale.GERMANY).format(d);
//
//			int hourNumber = a.startTime.get(Calendar.HOUR_OF_DAY);
//			String hourNumberS = String.format(Locale.GERMANY,"%02d", hourNumber);
//			
//			int minuteNumber = a.startTime.get(Calendar.MINUTE);
//			String minuteNumberS = String.format(Locale.GERMANY,"%02d", minuteNumber);
//			
//			
//			String eol = System.getProperty("line.separator"); 
//			
//			hourNumberS
//				mRendererChart1.addXTextLabel(horLabelSpacing,
//					hourNumberS  +":" + minuteNumberS);
//				horLabelSpacing=horLabelSpacing+1;
//		 }
//		}
		
		
		mRendererChart1.addXTextLabel(0, "Breakfast");
		
		mRendererChart1.addXTextLabel(1, "Morning");
		
		mRendererChart1.addXTextLabel(2, "Lunch");
		
		mRendererChart1.addXTextLabel(3, "Afternoon");
		
		mRendererChart1.addXTextLabel(4, "Dinner");
		
		mRendererChart1.addXTextLabel(5, "Night");
	}
	

	public static void createSampleBsLastWeek() {
		
		int y = 2015;
		//m = 2 means actually month 3 = march !
		int m = 5;
		
		//breakfast time
		int b_h = 8;
		int b_m = 11;
		
		//morning time
		int m_h = 10;
		int m_m = 06;
		
		//lunch time
		int l_h = 8;
		int l_m = 11;
		
		//afternoon
		int a_h = 16;
		int a_m = 0;
		
		//dinner
		int d_h = 20;
		int d_m = 30;
		
		//night
		int n_h = 22;
		int n_m = 30;
		
		int d7 = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		Calendar breakfast7  = new GregorianCalendar(y, m, d7, b_h, b_m-20);
		Calendar morning7 	 = new GregorianCalendar(y, m, d7, m_h, m_m+40);
		Calendar lunch7		 = new GregorianCalendar(y, m, d7, l_h, l_m-12);
		Calendar afternoon7  = new GregorianCalendar(y, m, d7, a_h, a_m-17);
		Calendar dinner7	 = new GregorianCalendar(y, m, d7, d_h, d_m-23);
		Calendar night7 	 = new GregorianCalendar(y, m, d7, n_h, n_m+29);
		
		int d6 = d7-1;
		Calendar breakfast6  = new GregorianCalendar(y, m, d6, b_h, b_m+30);
		Calendar morning6 	 = new GregorianCalendar(y, m, d6, m_h, m_m);
		Calendar lunch6		 = new GregorianCalendar(y, m, d6, l_h, l_m);
		Calendar afternoon6  = new GregorianCalendar(y, m, d6, a_h, a_m+2);
		Calendar dinner6 	 = new GregorianCalendar(y, m, d6, d_h, d_m);
		Calendar night6      = new GregorianCalendar(y, m, d6, n_h, n_m+5);
		
		int d5 = d7-2 ;
		Calendar breakfast5  = new GregorianCalendar(y, m, d5, b_h, b_m+2);
		Calendar morning5    = new GregorianCalendar(y, m, d5, m_h, m_m);
		Calendar lunch5		 = new GregorianCalendar(y, m, d5, l_h, l_m+12);
		Calendar afternoon5  = new GregorianCalendar(y, m, d5, a_h, a_m);
		Calendar dinner5	 = new GregorianCalendar(y, m, d5, d_h, d_m);
		Calendar night5		 = new GregorianCalendar(y, m, d5, n_h, n_m+8);
		
		int d4 = d7-3;
		Calendar breakfast4  = new GregorianCalendar(y, m, d4, b_h, b_m+14);
		Calendar morning4 	 = new GregorianCalendar(y, m, d4, m_h, m_m);
		Calendar lunch4		 = new GregorianCalendar(y, m, d4, l_h, l_m);
		Calendar afternoon4  = new GregorianCalendar(y, m, d4, a_h, a_m);
		Calendar dinner4	 = new GregorianCalendar(y, m, d4, d_h, d_m);
		Calendar night4 	 = new GregorianCalendar(y, m, d4, n_h, n_m-12);
		
		int d3 = d7-4;
		Calendar breakfast3  = new GregorianCalendar(y, m, d3, b_h, b_m);
		Calendar morning3 	 = new GregorianCalendar(y, m, d3, m_h, m_m);
		Calendar lunch3		 = new GregorianCalendar(y, m, d3, l_h, l_m);
		Calendar afternoon3  = new GregorianCalendar(y, m, d3, a_h, a_m);
		Calendar dinner3	 = new GregorianCalendar(y, m, d3, d_h, d_m-32);
		Calendar night3 	 = new GregorianCalendar(y, m, d3, n_h, n_m);
		
		int d2 = d7-5;
		Calendar breakfast2  = new GregorianCalendar(y, m, d2, b_h, b_m-30);
		Calendar morning2 	 = new GregorianCalendar(y, m, d2, m_h, m_m);
		Calendar lunch2		 = new GregorianCalendar(y, m, d2, l_h, l_m);
		Calendar afternoon2  = new GregorianCalendar(y, m, d2, a_h, a_m);
		Calendar dinner2	 = new GregorianCalendar(y, m, d2, d_h, d_m);
		Calendar night2 	 = new GregorianCalendar(y, m, d2, n_h, n_m);
		
		int d1 = d7-6;
		Calendar breakfast1 = new GregorianCalendar(y, m, d1, b_h, b_m);
		Calendar morning1 	= new GregorianCalendar(y, m, d1, m_h, m_m);
		Calendar lunch1	    = new GregorianCalendar(y, m, d1, l_h, l_m);
		Calendar afternoon1	= new GregorianCalendar(y, m, d1, a_h, a_m);
		Calendar dinner1	= new GregorianCalendar(y, m, d1, d_h, d_m);
		Calendar night1 	= new GregorianCalendar(y, m, d1, n_h, n_m);
		
		
	
		
		BloodSugar bsa1 = new BloodSugar(breakfast1, 110);
		BloodSugar bsa1b = new BloodSugar(morning1, 160);
		BloodSugar bsa2 = new BloodSugar(lunch1, 60);
		BloodSugar bsa3 = new BloodSugar(afternoon1, 150);
		BloodSugar bsa4 = new BloodSugar(dinner1, 180);
		BloodSugar bsa5 = new BloodSugar(night1, 120);
		
		BloodSugar bsa6 = new BloodSugar(breakfast2, 100);
		BloodSugar bsa6b = new BloodSugar(morning2, 160);
		BloodSugar bsa7 = new BloodSugar(lunch2, 80);
		BloodSugar bsa8 = new BloodSugar(afternoon2, 170);
		BloodSugar bsa9 = new BloodSugar(dinner2, 160);
		BloodSugar bsa10 = new BloodSugar(night2, 100);
		
		BloodSugar bsa11 = new BloodSugar(breakfast3, 80);
		BloodSugar bsa11b = new BloodSugar(morning3, 160);
		BloodSugar bsa12 = new BloodSugar(lunch3, 110);
		BloodSugar bsa13 = new BloodSugar(afternoon3, 170);
		BloodSugar bsa14 = new BloodSugar(dinner3, 290);
		BloodSugar bsa15 = new BloodSugar(night3, 110);
		
		BloodSugar bsa16 = new BloodSugar(breakfast4, 80);
		BloodSugar bsa17 = new BloodSugar(morning4, 160);
		BloodSugar bsa18 = new BloodSugar(lunch4, 110);
		BloodSugar bsa19 = new BloodSugar(afternoon4, 170);
		BloodSugar bsa20 = new BloodSugar(dinner4, 167);
		BloodSugar bsa21 = new BloodSugar(night4, 109);
		
		BloodSugar bsa22 = new BloodSugar(breakfast5, 90);
		BloodSugar bsa23 = new BloodSugar(morning5, 145);
		BloodSugar bsa24 = new BloodSugar(lunch5, 123);
		BloodSugar bsa25 = new BloodSugar(afternoon5, 156);
		BloodSugar bsa26 = new BloodSugar(dinner5, 170);
		BloodSugar bsa27 = new BloodSugar(night5, 110);
		
		allList = new ArrayList<AbstractActivity>();

		
		allList.add(bsa1); allList.add(bsa1b); allList.add(bsa2);
		allList.add(bsa3); allList.add(bsa4);allList.add(bsa5); 
		
		allList.add(bsa6); allList.add(bsa6b);
		allList.add(bsa7); allList.add(bsa8);
		allList.add(bsa9); allList.add(bsa10);
		
		allList.add(bsa11); allList.add(bsa11b);
		allList.add(bsa12);		
		allList.add(bsa13); allList.add(bsa14);
		allList.add(bsa15);
		
		allList.add(bsa16); allList.add(bsa17);
		allList.add(bsa18); allList.add(bsa19);
		allList.add(bsa20); allList.add(bsa21);
		
		allList.add(bsa22); allList.add(bsa23);
		allList.add(bsa24); allList.add(bsa25);
		allList.add(bsa26); allList.add(bsa27);
		
	}
	
}
