package com.getbasicinfo.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShowDeviceID extends Activity {
	
	protected Button showDeviceID_to_main;
    private TextView showDeviceID;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showdeviceid);
        
        showDeviceID = (TextView) findViewById(R.id.show_deviceID);
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        showDeviceID.setText("Device ID: "+imei); 

        showDeviceID_to_main = (Button) findViewById(R.id.showDeviceID_to_main);
        showDeviceID_to_main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				intent.setClass(ShowDeviceID.this, GetBasicInfoAndroidActivity.class); 
				startActivity(intent);
				ShowDeviceID.this.finish();
			}
		});
    }    
}