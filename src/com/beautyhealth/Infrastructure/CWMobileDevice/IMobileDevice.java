package com.beautyhealth.Infrastructure.CWMobileDevice;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public interface IMobileDevice {
	    
	    //�������
		public void SMSSend(Activity act,String address,String content);
		public void sendMessage(String number, String message);
		//�κ�
		public  Intent DialToNumber(String PhoneNumber);
		
		//��������HOME
		public  Intent BackToSysHom();
		
		//��������HOME
		public  String Orientation();
		
		

	
}
