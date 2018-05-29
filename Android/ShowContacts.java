package com.getbasicinfo.android;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShowContacts extends Activity {
	
	protected Button showContacts_to_main;
	private TextView showContacts;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showcontacts);
        
        showContacts = (TextView) findViewById(R.id.show_contacts);
        String contact_list = "Contact List: \n";
        int total_number = 0;
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()) {
        	int nameFieldColumnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
            String contact = cursor.getString(nameFieldColumnIndex);
            
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            
            while(phone.moveToNext())
            {
                String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contact_list += (contact + ": " + PhoneNumber + "\n");
                total_number += 1;
            }
        }
        cursor.close();
        contact_list = "Totally "+Integer.toString(total_number)+" Phone Numbers \n\n"+contact_list;
        showContacts.setText(contact_list); 

        showContacts_to_main = (Button) findViewById(R.id.showContacts_to_main);
        showContacts_to_main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); 
				intent.setClass(ShowContacts.this, GetBasicInfoAndroidActivity.class); 
				startActivity(intent);
				ShowContacts.this.finish();
			}
		});
    }    
}