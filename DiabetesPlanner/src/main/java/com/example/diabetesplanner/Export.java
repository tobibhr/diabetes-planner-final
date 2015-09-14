package com.example.diabetesplanner;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.io.IOException;

import recognition.model.MySQLiteHelper;


/**
 * Export contains methods to export data
 * 
 * @author Tobias
 */
public class Export {

	/**
	 * exportDB exports SQLite database to SD card
	 * 
	 * @author Tobias
	 */
	public static void exportDB(){
		File sd = Environment.getExternalStorageDirectory();
	      File data = Environment.getDataDirectory();
	       FileChannel source=null;
	       FileChannel destination=null;
	       String currentDBPath = "/data/"+ "com.example.diabetesplanner" +"/databases/"+MySQLiteHelper.DATABASE_NAME;
	       String backupDBPath = MySQLiteHelper.DATABASE_NAME;
	       File currentDB = new File(data, currentDBPath);
	       File backupDB = new File(sd, backupDBPath);
	       try {
	            source = new FileInputStream(currentDB).getChannel();
	            destination = new FileOutputStream(backupDB).getChannel();
	            destination.transferFrom(source, 0, source.size());
	            source.close();
	            destination.close();
	            //Toast.makeText(this, "Database exported", Toast.LENGTH_LONG).show();
	            Log.d("DB Export","successful");
	        } catch(IOException e) {
	        	e.printStackTrace();
	        }
	}
	
}
