package com.beautyhealth.Infrastructure.CWMobileDevice;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

public class MobilePhone422 extends AbsMobilePhone {
	
	//�������
		public void SMSSend(Activity act,String address,String content){
			SmsManager sms=SmsManager.getDefault();
			PendingIntent sendIntent=PendingIntent.getBroadcast(act, 0, new Intent(), 0);
			sms.sendTextMessage(address, null, content, sendIntent, null);
		}	
		public void sendMessage(String number, String message) {  		  
			SmsManager smsManager =SmsManager.getDefault();  
			smsManager.sendTextMessage(number, null,message, null, null);  		 
		}
		
		//�κ�
		public  Intent DialToNumber(String PhoneNumber){
			Intent _intent=new Intent();
			_intent.setAction(Intent.ACTION_DIAL);
			String data="tel:"+PhoneNumber;
			Uri auri=Uri.parse(data);
			_intent.setData(auri);
			return _intent;
		}
		
		//��������HOME
		public Intent BackToSysHom(){
			Intent _intent=new Intent();
			_intent.setAction(Intent.ACTION_MAIN);
			_intent.addCategory(Intent.CATEGORY_HOME);
			return _intent;
		}
		
		//��������HOME
		public  String Orientation(){
			//Configuration config=getResources().getConfiguration();
			//String screen=
			return null;
		}
	


		
}
