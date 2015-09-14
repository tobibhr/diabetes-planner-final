package gui.misc;

/**This class stores some methods which are used in various other classes 
 * and do not really belong to a certain one of them
 * @author Oliver
 */
public class GeneralAlgorithms {

	/**Returns a string which is representing a particular time in the conventional manner (e.g. 08:04)
	 */
	public static String DisplayTimeZeros(int hour,int minute){
		String hourZero="";
		String minuteZero="";
		
		if(hour<10){
			hourZero="0";
		}
		if(minute<10){
			minuteZero="0";
		}
		
		String time="";
		time = hourZero + hour + ":" + minuteZero + minute;
		
		return time;
	}

	/**Returns a string which is representing a particular date in the conventional manner (e.g. 09/04/1992)
	 */
	public static String DisplayDateZeros(int day,int month, int year){
		
		String dayZero="";
		String monthZero="";
		
		if(day<10){ dayZero="0"; }
		if(month<10){ monthZero="0"; }
		
		String date="";
		date = dayZero + day + "/" + monthZero + (month+1) + "/" + year;
		
		return date;
	}
	
}
