package com.beautyhealth.Setting.GPSSetting;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.PersonHealth.MyLocation.Activity.BackSave;
import com.beautyhealth.PersonHealth.MyLocation.Activity.GDGpsService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.LocationService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.ServiceState;
import com.beautyhealth.Setting.GPSSetting.Entity.GPSStatueAndTime;

public class SettingGPSActivity extends NavAndTabBarActivity implements OnClickListener {

	private Switch sw_isupload;
	private Spinner spinner_min;
	private boolean gpsEnabled;
	private Switch sw_gpsEnabled;
	private String time;
	private Boolean statue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settinggps);
		initNavBar("GPS设置", "<返回", null);
		fetchUIFromLayout();
		setListener();

	}

	private void fetchUIFromLayout() {
		sw_isupload = (Switch) findViewById(R.id.sw_isupload);
		spinner_min = (Spinner) findViewById(R.id.spinner_min);
		sw_gpsEnabled = (Switch) findViewById(R.id.sw_gpsEnabled);
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 获得手机是不是设置了GPS开启状态true：gps开启，false：GPS未开启
		statue = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (statue == true) {
			sw_gpsEnabled.setChecked(true);
			ISqlHelper iSqlHelper = new SqliteHelper(null,
					SettingGPSActivity.this);
			List<Object> ls = iSqlHelper
					.Query("com.beautyhealth.Setting.GPSSetting.Entity.GPSStatueAndTime",
							null);
			if (ls.size() > 0) {
				GPSStatueAndTime gPSStatueAndTime = (GPSStatueAndTime) ls
						.get(0);
				if (gPSStatueAndTime.isUpload.equals("0")) {
					sw_isupload.setChecked(false);
					spinner_min.setSelection(0);
				} else {
					sw_isupload.setChecked(true);
					String dbtime[] = getResources().getStringArray(
							R.array.time);
					for (int i = 0; i < dbtime.length; i++) {
						if (gPSStatueAndTime.Time.equals(dbtime[i])) {
							spinner_min.setSelection(i);
						}
					}
				}

			} else {
				sw_isupload.setChecked(false);
				spinner_min.setSelection(0);
			}
		}else {
			sw_gpsEnabled.setChecked(false);
			sw_isupload.setChecked(false);
			spinner_min.setSelection(0);
		}

	}

	private void setListener() {
		sw_gpsEnabled.setOnClickListener(this);
		sw_isupload.setOnClickListener(this);

	}
	
	@Override
	public void onClick(View v) {
		if (v == sw_gpsEnabled) {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 1);			
		}
		if (v == sw_isupload) {
			if(sw_gpsEnabled.isChecked()){			
				ISqlHelper iSqlHelper = new SqliteHelper(null,
						SettingGPSActivity.this);
				GPSStatueAndTime gPSStatueAndTime = new GPSStatueAndTime();
				if (sw_isupload.isChecked()) {
					gPSStatueAndTime.isUpload = "1";
				} else {
					gPSStatueAndTime.isUpload = "0";	
				}
				gPSStatueAndTime.Time = spinner_min.getSelectedItem().toString();
				gPSStatueAndTime.key = "1";
				iSqlHelper.SQLExec("delete from GPSStatueAndTime where key='1'");// 删除表中原有的数据，保证只有一条
				iSqlHelper.Insert(gPSStatueAndTime);
			}else{
				 Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				 startActivityForResult(intent, 3);	
			}
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Boolean gpsstate;
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 获得手机是不是设置了GPS开启状态true：gps开启，false：GPS未开启
		gpsstate = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (requestCode == 1) {
			if (gpsstate == true) {
				sw_gpsEnabled.setChecked(true);
				if(sw_isupload.isChecked()){
					sw_isupload.setChecked(true);		
				}else{
					sw_isupload.setChecked(false);
				}
				ToastUtil.show(getApplicationContext(), "GPS处于开启状态");
			} else {
				sw_gpsEnabled.setChecked(false);
				sw_isupload.setChecked(false);
				ToastUtil.show(getApplicationContext(), "GPS处于关闭状态");
			}
		}
		
		if (requestCode == 3) {
			if (gpsstate == true) {
				sw_gpsEnabled.setChecked(true);
				ISqlHelper iSqlHelper = new SqliteHelper(null,
						SettingGPSActivity.this);
				GPSStatueAndTime gPSStatueAndTime = new GPSStatueAndTime();
				if (sw_isupload.isChecked()) {
					gPSStatueAndTime.isUpload = "1";
				} else {
					gPSStatueAndTime.isUpload = "0";	
				}
				gPSStatueAndTime.Time = spinner_min.getSelectedItem().toString();
				gPSStatueAndTime.key = "1";
				iSqlHelper.SQLExec("delete from GPSStatueAndTime where key='1'");// 删除表中原有的数据，保证只有一条
				iSqlHelper.Insert(gPSStatueAndTime);
				ToastUtil.show(getApplicationContext(), "GPS处于开启状态");
			}else{
				sw_gpsEnabled.setChecked(false);
				sw_isupload.setChecked(false);
				ISqlHelper iSqlHelper = new SqliteHelper(null,SettingGPSActivity.this);
				GPSStatueAndTime gPSStatueAndTime = new GPSStatueAndTime();
				gPSStatueAndTime.isUpload = "0";	
				gPSStatueAndTime.Time = spinner_min.getSelectedItem().toString();
				gPSStatueAndTime.key = "1";
				iSqlHelper.SQLExec("delete from GPSStatueAndTime where key='1'");// 删除表中原有的数据，保证只有一条
				iSqlHelper.Insert(gPSStatueAndTime);
				ToastUtil.show(getApplicationContext(), "GPS处于关闭状态,不能打开GPS定位服务");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent intentService ;
		ServiceState aService=new ServiceState();
		aService.ServiceName="LocationUploading";
		ISqlHelper iSqlHelper = new SqliteHelper(null,SettingGPSActivity.this);
		GPSStatueAndTime gPSStatueAndTime = new GPSStatueAndTime();
		if (sw_isupload.isChecked()) {
			gPSStatueAndTime.isUpload = "1";
		} else {
			gPSStatueAndTime.isUpload = "0";	
		}
		gPSStatueAndTime.Time = spinner_min.getSelectedItem().toString();
		gPSStatueAndTime.key = "1";
		iSqlHelper.SQLExec("delete from GPSStatueAndTime where key='1'");// 删除表中原有的数据，保证只有一条
		iSqlHelper.Insert(gPSStatueAndTime);
		
		
		
    	
    	if(sw_isupload.isChecked() == true){
	    	//停止这个后台的服务 
	    	aService.setCurrentServiceStateInDB("0", this);
	    	//停止这个后台的服务	
	    	intentService = new Intent(this,GDGpsService.class);
	    	intentService.setAction(aService.ServiceName);
	    	intentService.putExtra("Time",spinner_min.getSelectedItem().toString());
	    	intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	stopService(intentService);
	    	Intent _Intent = new Intent(this,LocationService.class);
			stopService(_Intent);
	    	Toast.makeText(this, "服务 停止", Toast.LENGTH_SHORT).show();
	
	    	aService.setCurrentServiceStateInDB("1",this);
	    	//启动一个后台的服务
	    	intentService = new Intent(this,GDGpsService.class);
	    	intentService.setAction(aService.ServiceName);
	    	intentService.putExtra("Time",spinner_min.getSelectedItem().toString()); 
	    	intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startService(intentService);
	    	Toast.makeText(this, "服务 启动", Toast.LENGTH_SHORT).show();
    	}else{
    		//停止这个后台的服务 
	    	aService.setCurrentServiceStateInDB("0", this);
	    	//停止这个后台的服务	
	    	intentService = new Intent(this,GDGpsService.class);
	    	intentService.setAction(aService.ServiceName);
	    	intentService.putExtra("Time",spinner_min.getSelectedItem().toString());
	    	intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	Toast.makeText(this, "服务 停止", Toast.LENGTH_SHORT).show();
	    	stopService(intentService);
	    	Intent _Intent = new Intent(this,LocationService.class);
			stopService(_Intent);
	    	//testResponse();
    	}
	}
	private void testResponse() {
		ISqlHelper mysqlhelper=new SqliteHelper(null,this);
		List<Object> users=mysqlhelper.Query("com.beautyhealth.PersonHealth.MyLocation.Activity.BackSave", null);
		
		for(int j=0;j<users.size();j++){
			BackSave aUser=(BackSave)users.get(j);
			Toast.makeText(this, 
					"ID:"+aUser.AutoID+" Name:"+aUser.Time, 
					Toast.LENGTH_SHORT).show();
		}
		//clearData();
	}
	private void clearData() {
		ISqlHelper mysqlhelper=new SqliteHelper(null,this);
		mysqlhelper.SQLExec("delete from BackSave where AutoID<>'0'");
	}
}
