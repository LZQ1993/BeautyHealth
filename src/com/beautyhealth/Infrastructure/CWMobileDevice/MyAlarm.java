package com.beautyhealth.Infrastructure.CWMobileDevice;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.beautyhealth.Infrastructure.CWDomain.MyDate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.format.DateFormat;
import android.widget.Toast;


public class MyAlarm implements Serializable{
	
		public String alertID;
	    public String enabled;
	    public String timespan;
	    public String count; //��׼����
	    public String starttime;
	    public String title;
	    public String sampleCount; //ʵ������
	    public String musicfile; ////////新增的音乐部分
	
	    public MyAlarm() {
	    	alertID = "ddddd";
	    	SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        	Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��
	        timespan = "30";
	        count = "3";
	        starttime = formatter.format(curDate);
	        enabled = "1";
	        title = "sit down";
	        sampleCount="0";
	        musicfile="默认铃声";////////新增的音乐部分
	    }

	public final static void setAlartTip(Context ctx,Class<?> desti_act,MyDate _mydate){
		Intent _intent=new Intent();
		_intent.setClass(ctx,desti_act);		
		PendingIntent pi = PendingIntent.getActivity(ctx, 0, _intent, 0);	
		Calendar c = Calendar.getInstance();
		c.setTime(_mydate.getDate());
		Toast.makeText(ctx, "提醒设置成功", Toast.LENGTH_LONG).show();
		AlarmManager aManager = (AlarmManager) ctx.getSystemService(Service.ALARM_SERVICE);
		aManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), pi);				
		
	}
	
	public  boolean setRepeatAlarm(Context ctx,Class<?> desti_act){
		boolean result = false;
		try {
			Intent _intent = new Intent();
			_intent.setClass(ctx, desti_act);
			_intent.setAction(alertID);
			PendingIntent pi = PendingIntent.getActivity(ctx, 0, _intent, 0);
			long ltimespan = Long.valueOf(timespan) * 60 * 1000;
			AlarmManager aManager = (AlarmManager) ctx
					.getSystemService(Service.ALARM_SERVICE);
			aManager.setRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + ltimespan, ltimespan, pi);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	
	
	public final static void stopAlartTip(Context ctx,PendingIntent pi){
		AlarmManager aManager = (AlarmManager) ctx.getSystemService(Service.ALARM_SERVICE);
		aManager.cancel(pi);
	}
	
	public  boolean cancelRepeatAlarm(Context ctx,Class<?> desti_act){
		boolean result = false;
		try {
			Intent _intent = new Intent();
			_intent.setClass(ctx, desti_act);
			_intent.setAction(alertID);
			PendingIntent pi = PendingIntent.getActivity(ctx, 0, _intent, 0);
			AlarmManager aManager = (AlarmManager) ctx
					.getSystemService(Service.ALARM_SERVICE);
			aManager.cancel(pi);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	
}
