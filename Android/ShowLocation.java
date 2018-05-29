package com.getbasicinfo.android;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShowLocation extends Activity {
	
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 10000; // in Milliseconds
	
	protected LocationManager locationManager;	
	protected Button getLocationButton;
	protected Button showLogContent;
	protected Button showLocation_to_main;
    private TextView output_location;
    private TextView showSavedContent;
    private String fileName = "location_log.txt"; 
	private FileOutputStream outputStream;
	protected SimpleDateFormat formatter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showlocation);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        open_file();
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(
        		LocationManager.GPS_PROVIDER, 
        		MINIMUM_TIME_BETWEEN_UPDATES, 
        		MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
        		new MyLocationListener()
        );

        output_location = (TextView) findViewById(R.id.show_location_text);
        showSavedContent = (TextView) findViewById(R.id.show_saved_content);
        showLogContent = (Button) findViewById(R.id.show_log_content_button);
        showLogContent.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			read();
    		}
    	});
        
        getLocationButton = (Button) findViewById(R.id.show_location_button);
        getLocationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showCurrentLocation();
			}
		});
 
        showLocation_to_main = (Button) findViewById(R.id.showLocation_to_main);
        showLocation_to_main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				intent.setClass(ShowLocation.this, GetBasicInfoAndroidActivity.class); 
				startActivity(intent);
				ShowLocation.this.finish();
			}
		});
    }    
    
    protected void showCurrentLocation() {
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location != null) {
			String message = String.format(
					"Current Location: \n Longitude: %1$s \n Latitude: %2$s",
					location.getLongitude(), location.getLatitude()
			);
			output_location.setText(message);
		}
	} 
    
    private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			String message = String.format(
					"New Location: \n Longitude: %1$s \n Latitude: %2$s",
					location.getLongitude(), location.getLatitude()
			);
			
			Date curDate = new Date(System.currentTimeMillis());
        	String date = formatter.format(curDate); 
        	String write_to_log = date+": "+Double.toString((double)(Math.round(location.getLongitude()*1000000)/1000000.0))+", "+
        	Double.toString((double)(Math.round(location.getLatitude()*1000000)/1000000.0))+"\n";
        	
        	try {   
        		outputStream.write(write_to_log.getBytes());  
                outputStream.flush();
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }
			
			output_location.setText(message);
		}

		public void onStatusChanged(String s, int i, Bundle b) {
			Toast.makeText(ShowLocation.this, "Provider status changed",
					Toast.LENGTH_LONG).show();
		}

		public void onProviderDisabled(String s) {
			Toast.makeText(ShowLocation.this,
					"Provider disabled by the user. GPS turned off",
					Toast.LENGTH_LONG).show();
		}

		public void onProviderEnabled(String s) {
			Toast.makeText(ShowLocation.this,
					"Provider enabled by the user. GPS turned on",
					Toast.LENGTH_LONG).show();
		}
	}
    
    private void open_file() {  
        try {   
        	    outputStream = openFileOutput(fileName,  
                Activity.MODE_APPEND);    
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }    
    }  
    
    private void close_file() {  
        try {   
            outputStream.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }    
    }
    
    private void read() {  
        try {  
            FileInputStream inputStream = this.openFileInput(fileName);  
            byte[] bytes = new byte[1024];  
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();  
            while (inputStream.read(bytes) != -1) {  
                arrayOutputStream.write(bytes, 0, bytes.length);  
            }  
            inputStream.close();  
            arrayOutputStream.close();  
            String content = new String(arrayOutputStream.toByteArray());  
            showSavedContent.setText(content);  
  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }    
    } 
}