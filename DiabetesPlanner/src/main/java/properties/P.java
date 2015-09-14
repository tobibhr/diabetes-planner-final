package properties;

public abstract class P {
	// Values in seconds
	// conditions for 100% Reliability: The calculation "(finTimeWindow - initTimeWindow)/overlapWindow" must lead to an integer
	public static final int initTimeWindow = 6;
	public static final int overlapWindow = 3;
	public static final int finTimeWindow = 12;
	
	// For Calculation.java methods
	public static final int arraySize = (int) ((finTimeWindow - initTimeWindow)/overlapWindow+1);
	
	// Value in Hz
	public static final int sampleRate = 50;
	
	// Calculate the size of accelerometerCache, how many sensor data points in the array
	static double x = (finTimeWindow * sampleRate);
	
	// Array to store the sensor data for processing
	public static double[][] accelerometerCache = new double[(int) x][4];
	
	// File to be used for classification. Enables to use individual models or generic models.
	public static String modelDataFileName = "J48.generic";

	// Amount of unkown location until user is asked (this function can be enabled)
	public static final int askUserForLocation = 0; // 1-on; 0-off
	public static final int askUserForLocationAfter = 30;


}