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

public class ShowPhoneNumber extends Activity {
	
	protected Button showPhoneNumber_to_main;
    private TextView showPhoneNumber;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showphonenumber);
        
        showPhoneNumber = (TextView) findViewById(R.id.show_phoneNumber);
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String phone_number = telephonyManager.getLine1Number();      
        showPhoneNumber.setText("Phone Number: "+phone_number); 
        
        showPhoneNumber_to_main = (Button) findViewById(R.id.showPhoneNumber_to_main);
        showPhoneNumber_to_main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				intent.setClass(ShowPhoneNumber.this, GetBasicInfoAndroidActivity.class); 
				startActivity(intent);
				ShowPhoneNumber.this.finish();
			}
		});
    }    
}