
package gui.activities;

import gui.items.AbstractActivity;
import gui.items.BloodSugar;
import gui.items.Carb;
import gui.items.HumanActivity;
import gui.items.Insulin;
import gui.items.MeasuringActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.XYChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;

import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;

import com.example.diabetesplanner.DataExchange;
import com.example.diabetesplanner.GuiMain;
import com.example.diabetesplanner.R;
import com.example.diabetesplanner.R.id;
import com.example.diabetesplanner.R.layout;
import com.example.diabetesplanner.R.menu;

public class ChartActivity extends Activity {
	
	//timewindow (in min) in which blood sugar,insulin and carb is labeled together
	int timeWindow = 30;

//	ArrayList<MeasuringActivity> bsList;
//	ArrayList<HumanActivity> hList;	
	ArrayList<AbstractActivity> allList;
	
	String acts[];
	
	//data for activity bars in chart 2
	XYSeries haSeries1;
	XYSeries haSeries2;
	XYSeries haSeries3;
	XYSeries haSeries4; 
	XYSeries haSeries5; 
	XYSeries haSeries6; 
	XYSeries haSeries7; 

	
	//data for blood sugar line in chart 1
	XYSeries bsSeries = new XYSeries("Blood Sugar Distribution",0);

	//data for insulin and carb bars in chart 1
	XYSeries iSeries = new XYSeries("Insulin",1); 
	XYSeries cSeries = new XYSeries("Carbohydrates",1);
	
	//renderer for certain bars/lines
	XYSeriesRenderer lineRenderer  = new XYSeriesRenderer();
	XYSeriesRenderer barRendererInsulin = new XYSeriesRenderer();
	XYSeriesRenderer barRendererCarbs = new XYSeriesRenderer();
	
	XYSeriesRenderer barRendererA1 = new XYSeriesRenderer();
	XYSeriesRenderer barRendererA2 = new XYSeriesRenderer();
	XYSeriesRenderer barRendererA3 = new XYSeriesRenderer();
	XYSeriesRenderer barRendererA4 = new XYSeriesRenderer();
	XYSeriesRenderer barRendererA5 = new XYSeriesRenderer();
	XYSeriesRenderer barRendererA6 = new XYSeriesRenderer();
	XYSeriesRenderer barRendererA7 = new XYSeriesRenderer();
	XYSeriesRenderer barRendererA8 = new XYSeriesRenderer();
	XYSeriesRenderer barRendererA9 = new XYSeriesRenderer();
	
	//renderer for a set of bars and/or lines
	XYMultipleSeriesRenderer mRendererChart1;
	XYMultipleSeriesRenderer mRendererChart2;
	
	//Datasets which collect the different datasets of multiple series
	XYMultipleSeriesDataset datasetChart2 = new XYMultipleSeriesDataset();
	XYMultipleSeriesDataset datasetChart1 = new XYMultipleSeriesDataset();
	
	//condition for grouping insulin,carb and bloodsugar together
	boolean sameTimeAsActBefore=false;
	
	/**
	 * This method is invoked immediately when the activity is created.
	 * It is comparable to the main-method in a normal java-class, but for activities.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		acts = getResources().getStringArray(R.array.activities);

		//data for activity bars in chart 2
		haSeries1 = new XYSeries(acts[0]);
		haSeries2 = new XYSeries(acts[1]);
		haSeries3 = new XYSeries(acts[2]);
		haSeries4 = new XYSeries(acts[3]);
		haSeries5 = new XYSeries(acts[4]);
		haSeries6 = new XYSeries(acts[5]);
		haSeries7 = new XYSeries(acts[6]);
		
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE); 	    //Remove title bar
	    
		setContentView(R.layout.activity_chart);
		
		//create some blood sugar and human activities
		allList = DataExchange.createSampleData();
		//allList = DataExchange.getData(); TODO change to this when Fraction Aggregation ready
		
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
	 * 
	 * @author Oliver
	 */
	private void createCharts() {
		
		splitData();
		
		//set the lineRenderer Settings
			lineRenderer.setPointStyle(PointStyle.CIRCLE);
			lineRenderer.setFillPoints(true);
		
		//set the barRenderer settings of bars in chart1			
			barRendererInsulin.setColor(Color.MAGENTA);
			barRendererCarbs.setColor(Color.GREEN);
			
		//set the barRenderer Settings of bars in chart2 		
			barRendererA1.setColor(Color.BLACK);
			barRendererA2.setColor(Color.BLUE);
			barRendererA3.setColor(Color.RED);
			barRendererA4.setColor(Color.LTGRAY);
			barRendererA5.setColor(Color.GRAY);
			barRendererA6.setColor(Color.MAGENTA);
			barRendererA7.setColor(Color.GREEN);
			
		//set a multiple renderer for the chart1 and one for chart2, settings are done in a seperated method
			mRendererChart1= new XYMultipleSeriesRenderer(2);
			mRendererChart1=setupCombiChartRenderer(mRendererChart1);
			mRendererChart1.setYTitle(DataExchange.bsUnit);
			mRendererChart1.setXAxisMin(0);
			mRendererChart1.addSeriesRenderer(lineRenderer);
			mRendererChart1.addSeriesRenderer(barRendererCarbs);
			mRendererChart1.addSeriesRenderer(barRendererInsulin);
			
			mRendererChart2= new XYMultipleSeriesRenderer();
			mRendererChart2= setupBarChartRenderer(mRendererChart2);
			mRendererChart2.setYTitle("minutes");
			mRendererChart2.addSeriesRenderer(barRendererA1);
			mRendererChart2.addSeriesRenderer(barRendererA2);
			mRendererChart2.addSeriesRenderer(barRendererA3);
			mRendererChart2.addSeriesRenderer(barRendererA4);
			mRendererChart2.addSeriesRenderer(barRendererA5);
			mRendererChart2.addSeriesRenderer(barRendererA6);
			mRendererChart2.addSeriesRenderer(barRendererA7);
			


		//add series data from above to a certain dataset (which is needed to create a chartView)
			datasetChart1.addSeries(bsSeries);	
			datasetChart1.addSeries(iSeries);
			datasetChart1.addSeries(cSeries);
			
			
			datasetChart2.addSeries(haSeries1);	
			datasetChart2.addSeries(haSeries2);	
			datasetChart2.addSeries(haSeries3);	
			datasetChart2.addSeries(haSeries4);
			datasetChart2.addSeries(haSeries5);
			datasetChart2.addSeries(haSeries6);
			datasetChart2.addSeries(haSeries7);
			
		
		 String[] types={LineChart.TYPE,BarChart.TYPE,BarChart.TYPE};
		//create view of chart1 which connects data and functions to xml file
			final GraphicalView viewChart1 = ChartFactory.getCombinedXYChartView(this.getBaseContext(), datasetChart1,mRendererChart1,types);
			LinearLayout chartLyt = (LinearLayout) findViewById(R.id.chart1);
			chartLyt.addView(viewChart1);

			//create view of chart2 which connects data and functions to xml file
			final GraphicalView viewChart2 = ChartFactory.getBarChartView(this.getBaseContext(), datasetChart2,mRendererChart2,BarChart.Type.DEFAULT);
			LinearLayout chartLyt2 = (LinearLayout) findViewById(R.id.chart2);
			chartLyt2.addView(viewChart2);
			
			synchronizeScroll(viewChart2,viewChart1);
	}

	/**
	 * This method detects the type of each item in the arraylist allList and sorts it into the corresponding
	 * series.
	 * 
	 * @author Oliver
	 */
	private void splitData() {
		
		int mCount = 0;
		int aCount = 0;
		
		Collections.sort(allList, Collections.reverseOrder());
		
		for(AbstractActivity a: allList){
			
			if(a instanceof HumanActivity) {
				HumanActivity h = (HumanActivity) a;
				if(h.getName().equals(acts[0])) {haSeries1.add(++aCount,h.durationMinutes); mCount++;}
				if(h.getName().equals(acts[1])) {haSeries2.add(++aCount,h.durationMinutes); mCount++;}
				if(h.getName().equals(acts[2])) {haSeries3.add(++aCount,h.durationMinutes); mCount++;}
				if(h.getName().equals(acts[3])) {haSeries4.add(++aCount,h.durationMinutes); mCount++;}
				if(h.getName().equals(acts[4])) {haSeries5.add(++aCount,h.durationMinutes); mCount++;}
				if(h.getName().equals(acts[5])) {haSeries6.add(++aCount,h.durationMinutes); mCount++;}
				if(h.getName().equals(acts[6])) {haSeries7.add(++aCount,h.durationMinutes); mCount++;}
			}
			else if(a instanceof MeasuringActivity) {
				
			sameTimeAsActBefore = checkCommonStartTime(a,mCount);
			
			if(!sameTimeAsActBefore){
				mCount++;
				aCount++;
			}
			 
				if(a instanceof BloodSugar){
						BloodSugar b = (BloodSugar) a; bsSeries.add(mCount,b.value);
				}
				else if(a instanceof Carb){
						Carb c = (Carb) a; cSeries.add(mCount,c.value);
				}
				else if(a instanceof Insulin){
						Insulin i = (Insulin) a; iSeries.add(mCount,i.value);
					//TODO: Group carb,ins & bloodsugar  together 
				}
			}
		}
		
	}

	/**Used to check if a measuring activity belongs to another one. Typically measuring of blood sugar,
	 * insulin and carbohydrates belongs together and hence it is grouped if it is a small distance inbetween.
	 * 
	 * @param a an activity,which should be compared to the one at mcount in the dataset of mRendererChart1
	 * @param mCount should be the countnumber of the measuring activity which is preceeding activity a
	 * @return true if there was another measurement in the last half hour
	 * 
	 * @author Oliver
	 */
	private boolean checkCommonStartTime(AbstractActivity a,int mCount) {
		
		if(mCount==0){return false;}
		
		AbstractActivity lastAct = allList.get(mCount);
		Calendar lastStart = lastAct.getStartTime();
		
		int minDiff = a.startTime.get(Calendar.MINUTE) - lastStart.get(Calendar.MINUTE);
		int hourDiff = a.startTime.get(Calendar.HOUR_OF_DAY) - lastStart.get(Calendar.HOUR_OF_DAY);
		
		boolean result = minDiff<30 && hourDiff==0 && lastAct instanceof MeasuringActivity;
		return result;
	}

	/**
	 * This method synchronizes the (horizontal) scrolling between two charts.
	 * @param chart1
	 * @param chart2
	 * 
	 * @author Oliver
	 */
	private void synchronizeScroll(final GraphicalView chart1,final GraphicalView chart2) {
		chart1.addPanListener(new PanListener() {
		    public void panApplied() {
		    	mRendererChart1.setRange(new double[] { 
		    			mRendererChart2.getXAxisMin(), 
		    			mRendererChart2.getXAxisMax(),
		    			mRendererChart1.getYAxisMin(0),
		    			mRendererChart1.getYAxisMax(0)
		        },0);
		    	mRendererChart1.setRange(new double[] { 
		    			mRendererChart2.getXAxisMin(), 
		    			mRendererChart2.getXAxisMax(),
		    			mRendererChart1.getYAxisMin(1),
		    			mRendererChart1.getYAxisMax(1)
		        },1);
		    	chart2.repaint();
			    }
			  });
		

			
			chart2.addPanListener(new PanListener() {
			    public void panApplied() {
			    	mRendererChart2.setRange(new double[] { 
			    			mRendererChart1.getXAxisMin(), 
			    			mRendererChart1.getXAxisMax(),
			    			mRendererChart2.getYAxisMin(),
			    			mRendererChart2.getYAxisMax()
			        });
			    	chart1.repaint();
			    }
			  });
		
	}

	/**
	 * Creates a new XYMultipleSeriesRenderer and do all the settings.
	 * 
	 */
	private XYMultipleSeriesRenderer setupBarChartRenderer(XYMultipleSeriesRenderer mRenderer) {
		
		mRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01)); //transparent margins to avoid a black border
		mRenderer.setShowGrid(true);
		
		//set sizes,color,padding and align of the lables of the labels(=values on the axis)
		mRenderer.setLabelsTextSize(13);		

		mRenderer.setXLabelsAlign(Align.CENTER);
		mRenderer.setXLabelsPadding(15);
		mRenderer.setXLabelsColor(Color.BLUE);
		
		mRenderer.setXAxisMin(0); mRenderer.setXAxisMax(5);		
		mRenderer.setYAxisMax(400);
		mRenderer.setYAxisMin(0);
		mRenderer.setYLabelsAlign(Align.RIGHT, 0);
		mRenderer.setYLabels(15);
		mRenderer.setYLabelsColor(0,Color.BLUE);
		mRenderer.setYLabelsPadding(10);

		mRenderer.setShowAxes(true);
		mRenderer.setAxesColor(Color.BLACK);
		
		mRenderer.setAxisTitleTextSize(15);
		mRenderer.setMargins(new int[]{0,60,0,0});
		
		mRenderer.setPointSize(5);
		
		//disable zoom functionality
		mRenderer.setZoomRate(0.2f);
		mRenderer.setZoomEnabled(false, true);

		//hide legend
		mRenderer.setShowLegend(true);	
	
		
		//avoid cutting off xlabels by hiding legend
		int[] margins = mRenderer.getMargins();
		margins[2] = (int) mRenderer.getLabelsTextSize() + 9;
		mRenderer.setMargins(margins);
		
		//enable horizontal scroll
		mRenderer.setPanEnabled(true, false);
		mRenderer.setInScroll(true);
		mRenderer.setClickEnabled(false);
		
		//limits for scrolling (-1 instead of 0 to display first label, )
		mRenderer.setPanLimits(new double[]{-1,allList.size(),0,0});

		//delete original labels and replace them by customized ones
		mRenderer.setXLabels(0);
		
		//bar settings
	    mRenderer.setBarSpacing(2); //5 //0
		mRenderer.setBarWidth(4); //8

		return mRenderer;
	}
	
	private XYMultipleSeriesRenderer setupCombiChartRenderer(XYMultipleSeriesRenderer mRenderer) {
		
		
		mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); //transparent margins to avoid a black border
		mRenderer.setShowGrid(true);
		
		//set sizes,color,padding and align of the lables of the labels(=values on the axis)
		mRenderer.setLabelsTextSize(13);		

		mRenderer.setXLabelsAlign(Align.CENTER);
		mRenderer.setXLabelsPadding(15);
		mRenderer.setXLabelsColor(Color.BLUE);
		mRenderer.setXAxisMin(0); mRenderer.setXAxisMax(5);	
		
		mRenderer.setYAxisMax(300,0);
		mRenderer.setYAxisMin(60,0);
		mRenderer.setYLabelsAlign(Align.RIGHT, 0);
		mRenderer.setYAxisAlign(Align.LEFT, 0);
		
		mRenderer.setYAxisMax(16,1);
		mRenderer.setYAxisMin(0,1);
		mRenderer.setYLabelsAlign(Align.LEFT, 1);
		mRenderer.setYAxisAlign(Align.RIGHT, 1);
		mRenderer.setShowAxes(true);
		mRenderer.setDisplayValues(true);

		
		mRenderer.setYLabels(15);
		mRenderer.setYLabelsColor(0,Color.BLUE);
		mRenderer.setYLabelsPadding(10);

		mRenderer.setShowAxes(true);
		mRenderer.setAxesColor(Color.BLACK);
		
		mRenderer.setAxisTitleTextSize(15);
		mRenderer.setMargins(new int[]{0,60,0,0});
		
		mRenderer.setPointSize(5);
		
		//disable zoom functionality
		mRenderer.setZoomRate(0.2f);
		mRenderer.setZoomEnabled(false, false);

		//hide legend
		mRenderer.setShowLegend(true);	
	
		
		//avoid cutting off xlabels by hiding legend
		int[] margins = mRenderer.getMargins();
		margins[2] = (int) mRenderer.getLabelsTextSize() + 9;
		mRenderer.setMargins(margins);
		
		//enable horizontal scroll
		mRenderer.setPanEnabled(true, false);
		mRenderer.setInScroll(true);
		mRenderer.setClickEnabled(false);
		
		//limits for scrolling (-1 instead of 0 to display first label, )
		mRenderer.setPanLimits(new double[]{-1,allList.size(),0,0});

		//delete original labels and replace them by customized ones
		mRenderer.setXLabels(0);
		
		//bar settings
	    mRenderer.setBarSpacing(14); // previously 5
		//mRenderer.setBarWidth(4); //previously 8

		return mRenderer;
	}

	/**
	 * This method creates customized x-labels which display time and date of each activity and empty space in the other chart.
	 */
	private void createXLabels(){

		int entryNr= 0;
		
		for(AbstractActivity a: allList){
		
			Date d = a.startTime.getTime();
			String dayString =  new SimpleDateFormat("EE",Locale.GERMANY).format(d);

			
			int monthNumber = a.startTime.get(Calendar.MONTH);
			monthNumber+=1;
			String monthNumberS = String.format(Locale.GERMANY,"%02d", monthNumber);
		
			
			int dayNumber = a.startTime.get(Calendar.DAY_OF_MONTH);
			String dayNumberS = String.format(Locale.GERMANY,"%02d", dayNumber);
			
			int hourNumber = a.startTime.get(Calendar.HOUR_OF_DAY);
			String hourNumberS = String.format(Locale.GERMANY,"%02d", hourNumber);
			
			int minuteNumber = a.startTime.get(Calendar.MINUTE);
			String minuteNumberS = String.format(Locale.GERMANY,"%02d", minuteNumber);
			
			String eol = System.getProperty("line.separator"); 
			
			if(a instanceof MeasuringActivity){
				
				
				sameTimeAsActBefore= checkCommonStartTime(a,entryNr);
				
				 
				 if(!sameTimeAsActBefore){
					 entryNr+=1;
				mRendererChart1.addXTextLabel(entryNr,dayString + eol +
					dayNumberS + ". " + 	monthNumberS  + eol
					+ 	hourNumberS  +":" + 	minuteNumberS);
				
				mRendererChart2.addXTextLabel(entryNr,"");
				 }
			}else{
				    entryNr+=1;
				mRendererChart2.addXTextLabel(entryNr,dayString + eol +
						dayNumberS + ". " + 	monthNumberS  + eol
						+ 	hourNumberS  +":" + 	minuteNumberS);
				
				mRendererChart1.addXTextLabel(entryNr,"");
			}
		}
	}
}


