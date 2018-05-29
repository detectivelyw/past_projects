package com.getbasicinfo.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GetBasicInfoAndroidActivity extends Activity {		
	protected Button retrieveLocationButton;
	protected Button retrieveDeviceIDButton;
	protected Button retrievePhoneNumberButton;
	protected Button retrieveContacts;
	protected Button sendMessageButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        retrieveLocationButton = (Button) findViewById(R.id.retrieve_location_button);
        retrieveDeviceIDButton = (Button) findViewById(R.id.retrieve_deviceid_button);
        retrievePhoneNumberButton = (Button) findViewById(R.id.retrieve_phonenumber_button);
        retrieveContacts = (Button) findViewById(R.id.retrieve_contacts_button);
        sendMessageButton = (Button) findViewById(R.id.send_message_button);

		retrieveLocationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				intent.setClass(GetBasicInfoAndroidActivity.this, ShowLocation.class); 
				startActivity(intent);
				GetBasicInfoAndroidActivity.this.finish();
			}
		});   
		
		retrieveDeviceIDButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				intent.setClass(GetBasicInfoAndroidActivity.this, ShowDeviceID.class); 
				startActivity(intent);
				GetBasicInfoAndroidActivity.this.finish();
			}
		}); 
		
		retrievePhoneNumberButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				intent.setClass(GetBasicInfoAndroidActivity.this, ShowPhoneNumber.class); 
				startActivity(intent);
				GetBasicInfoAndroidActivity.this.finish();
			}
		});
		
		retrieveContacts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				intent.setClass(GetBasicInfoAndroidActivity.this, ShowContacts.class); 
				startActivity(intent);
				GetBasicInfoAndroidActivity.this.finish();
			}
		});
		
		sendMessageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				intent.setClass(GetBasicInfoAndroidActivity.this, SendMessage.class); 
				startActivity(intent);
				GetBasicInfoAndroidActivity.this.finish();
			}
		});
    }    
}