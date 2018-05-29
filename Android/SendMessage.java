package com.getbasicinfo.android;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendMessage extends Activity {
	
	protected Button sendMessage_to_main;
	protected Button sendTextMessageButton;
	protected EditText txtPhoneNo;
    protected EditText txtMessage;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmessage);
        
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        
        sendTextMessageButton = (Button) findViewById(R.id.send_textmessage_button);
        sendTextMessageButton.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			 String phoneNo = txtPhoneNo.getText().toString();
                 String message = txtMessage.getText().toString();                 
                 if (phoneNo.length()>0 && message.length()>0) {
                	 sendSMS(phoneNo, message);
                 }                              
                 else {
                	 Toast.makeText(getBaseContext(), 
                             "Please enter both phone number and message.", 
                             Toast.LENGTH_SHORT).show();
                 }
    		}
    	});
        
        sendMessage_to_main = (Button) findViewById(R.id.sendMessage_to_main);
        sendMessage_to_main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				intent.setClass(SendMessage.this, GetBasicInfoAndroidActivity.class); 
				startActivity(intent);
				SendMessage.this.finish();
			}
		});
    } 
    
    private void sendSMS(String phoneNumber, String message)
    {        
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, SendMessage.class), 0);                
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);        
    }
}