package com.beautyhealth.PersonHealth.MyLocation.Activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWAndroidSystem.CWAlarmManager;

public class GDGpsService extends Service{
	// 更新时间 1000ms
	private long mintime;
	private AlarmManager am;
	PendingIntent sender;
	private String time;
	@Override
	public void onCreate() {
	
	}

	// 此方法是为了可以在Acitity中获得服务的实例
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		time = intent.getStringExtra("Time");
		mintime =Long.valueOf(time);
		//启动前先消掉以前的
		CWAlarmManager.stopSystemAlarm(getApplicationContext(),LocationService.class,"com.chinawit.locationuploading");
		CWAlarmManager.startSystemAlarm(getApplicationContext(),LocationService.class, mintime, "com.chinawit.locationuploading");
		//设置 前台服务 
		Notification notification = new Notification(R.drawable.ic_launcher,"wf update service is running",System.currentTimeMillis()); 
		PendingIntent pintent=PendingIntent.getService(this, 0, intent, 0);  
		notification.setLatestEventInfo(this, "WF Update Service","wf update service is running！", pintent); 
		//让该service前台运行，避免手机休眠时系统自动杀掉该服务  
		//如果 id 为 0 ，那么状态栏的 notification 将不会显示。  
		startForeground(0, notification);  
		//flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		ServiceState aService=new ServiceState();
		aService.ServiceName="LocationUploading";
		if(aService.requireStart(getApplicationContext())){		
			//启动一个后台的服务
			Intent intentService = new Intent(getApplicationContext(),GDGpsService.class);
        	intentService.setAction(aService.ServiceName);
        	intentService.putExtra("Time",time); 
        	intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	getApplicationContext().startService(intentService);
        	Toast.makeText(getApplicationContext(), "服务 启动", Toast.LENGTH_SHORT).show();
		}
		else{
			CWAlarmManager.stopSystemAlarm(getApplicationContext(),LocationService.class,"com.chinawit.locationuploading");
			super.onDestroy();
		}
	}

}
