package com.beautyhealth.Infrastructure.CWComponent;

import java.util.List;

import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.PersonHealth.MyLocation.Activity.GpsLocationReceiver;
import com.beautyhealth.PersonHealth.MyLocation.Activity.GDGpsService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.LocationService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.ServiceState;
import com.beautyhealth.Setting.GPSSetting.Entity.GPSStatueAndTime;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;

public class App extends Application {
	private AppInfo appInfo;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		appInfo = new AppInfo(getApplicationContext());
		if (!appInfo.isNewVersion()) {
			ISqlHelper iSqlHelper = new SqliteHelper(null,
					getApplicationContext());
			List<Object> ls1 = iSqlHelper.Query("com.beautyhealth.Setting.GPSSetting.Entity.GPSStatueAndTime",null);
			Intent intentService ;
			ServiceState aService=new ServiceState();
			aService.ServiceName="LocationUploading";
			Intent _Intent = new Intent(this,LocationService.class);
			stopService(_Intent);
			if (ls1.size() > 0) {
				GPSStatueAndTime gat = (GPSStatueAndTime) ls1.get(0);
				if (gat.isUpload.equals("1")) {
					
					//停止这个后台的服务 
			    	aService.setCurrentServiceStateInDB("0", this);
			    	//停止这个后台的服务	
			    	intentService = new Intent(this,GDGpsService.class);
			    	intentService.setAction(aService.ServiceName);
			    	intentService.putExtra("Time",gat.Time); 
			    	intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    	stopService(intentService);
			    
					aService.setCurrentServiceStateInDB("1",this);
			    	//启动一个后台的服务
			    	intentService = new Intent(this,GDGpsService.class);
			    	intentService.setAction(aService.ServiceName);
			    	intentService.putExtra("Time",gat.Time); 
			    	intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    	startService(intentService);
					
				} else {
					//停止这个后台的服务 
			    	aService.setCurrentServiceStateInDB("0", this);
			    	//停止这个后台的服务		    	
			    	intentService = new Intent(this,GDGpsService.class);
			    	intentService.setAction(aService.ServiceName);
			    	intentService.putExtra("Time",gat.Time); 
			    	intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    	stopService(intentService);
			    	
				}
			} else {
				//停止这个后台的服务 
		    	aService.setCurrentServiceStateInDB("0", this);
		    	//停止这个后台的服务	
		    	intentService = new Intent(this,GDGpsService.class);
		    	intentService.setAction(aService.ServiceName);
		    	intentService.putExtra("Time","0"); 
		    	intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	stopService(intentService);
		    	
			}
			
		}
	}

}
