package com.beautyhealth.UserCenter;

import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.PersonHealth.MyLocation.Activity.GDGpsService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.LocationService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.ServiceState;
import com.beautyhealth.Setting.BluetoothSetting;
import com.beautyhealth.Setting.CallNumSetting.CallNumberListViewAcitity;
import com.beautyhealth.Setting.GPSSetting.SettingGPSActivity;
import com.beautyhealth.UserCenter.Activity.FamilyNumberActivity;
import com.beautyhealth.UserCenter.Activity.LoginActivity;
import com.beautyhealth.UserCenter.Activity.MessageBindingActivity;
import com.beautyhealth.UserCenter.Activity.PersonalInformationManagerActivity;
import com.beautyhealth.UserCenter.Activity.UserFeedBackActivity;
import com.beautyhealth.UserCenter.Activity.UserManagerActivity;

public class MeActivity extends NavAndTabBarActivity implements OnClickListener {

	private TableRow bluetoothsetting, gpssetting, callcentersetting,
			usermessage, userback, callnumber, beijianhuren,usermanager;
	private TextView bluetoothstate;
	
	private List<Object> list ;
	private long firstBackKeyTime = 0;
	private BluetoothAdapter bluetoothAdapter;
	private ISqlHelper iSqlHelper;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me);
		initNavBar("我", null, null);
		init();
		setListener();
	}

	private void init() {
		usermanager= (TableRow) findViewById(R.id.more_page_row0);
		bluetoothsetting = (TableRow) findViewById(R.id.more_page_row1);
		gpssetting = (TableRow) findViewById(R.id.more_page_row2);
		callcentersetting = (TableRow) findViewById(R.id.more_page_row3);
		usermessage = (TableRow) findViewById(R.id.more_page_row4);
		userback = (TableRow) findViewById(R.id.more_page_row5);
		callnumber = (TableRow) findViewById(R.id.more_page_row6);
		beijianhuren = (TableRow) findViewById(R.id.more_page_row7);
		bluetoothstate=(TextView)findViewById(R.id.bluetoothstate);
		
		bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
	    iSqlHelper= new SqliteHelper(null, getApplicationContext());
		list = iSqlHelper.Query("com.beautyhealth.Setting.BluetoothSetting", null);
    	if(list.size()>0){               	
        	 BluetoothSetting bs=(BluetoothSetting)list.get(0);
        	 if(bs.State.equals("1")){
        		 bluetoothAdapter.enable();
        		 bluetoothstate.setText("蓝牙已打开");
        		 
        	 }else{
        		 bluetoothAdapter.disable();
        		 bluetoothstate.setText("蓝牙已关闭");
        	 }
    	}else{
    		 bluetoothAdapter.disable();
    		 bluetoothstate.setText("蓝牙已关闭");
    	}

	}

	private void setListener() {
		usermanager.setOnClickListener((OnClickListener) this);
		bluetoothsetting.setOnClickListener((OnClickListener) this);
		gpssetting.setOnClickListener((OnClickListener) this);
		callcentersetting.setOnClickListener((OnClickListener) this);
		usermessage.setOnClickListener((OnClickListener) this);
		userback.setOnClickListener((OnClickListener) this);
		callnumber.setOnClickListener((OnClickListener) this);
		beijianhuren.setOnClickListener((OnClickListener) this);
		

	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent();
		BluetoothSetting  bs =new BluetoothSetting();
		switch (view.getId()) {
		case R.id.more_page_row0:
			intent.setClass(MeActivity.this, UserManagerActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			
			break;
		case R.id.more_page_row1:
			if(bluetoothAdapter.getState()==10){
				//打开蓝牙
				bluetoothAdapter.enable();
				bs.key="1";		
				bs.State="1";
				iSqlHelper.Delete(bs,new String[]{"key"});
				iSqlHelper.Insert(bs);
				bluetoothstate.setText("蓝牙已打开");
			}
			else {
				//关闭蓝牙
				bluetoothAdapter.disable();
				bs.key="1";		
				bs.State="0";
				iSqlHelper.Delete(bs,new String[]{"key"});
				iSqlHelper.Insert(bs);
				bluetoothstate.setText("蓝牙已关闭");
			}
			break;
		case R.id.more_page_row2:
			intent.setClass(MeActivity.this, SettingGPSActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			// finish();
			break;
		case R.id.more_page_row3:
			intent.setClass(MeActivity.this, CallNumberListViewAcitity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			break;
		case R.id.more_page_row4:
			intent.setClass(MeActivity.this,
					PersonalInformationManagerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			break;
		case R.id.more_page_row5:
			intent.setClass(MeActivity.this, UserFeedBackActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			break;
		case R.id.more_page_row6:
			iSqlHelper = new SqliteHelper(null, getApplicationContext());
			list = iSqlHelper.Query(
					"com.beautyhealth.UserCenter.Entity.UserMessage", null);
			if (list.size() == 0) {
				ToastUtil.show(getApplicationContext(), "请先登录");
			} else {
				intent.setClass(MeActivity.this, FamilyNumberActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_up_in,
						R.anim.slide_down_out);
				
			}
			break;
		case R.id.more_page_row7:
			iSqlHelper = new SqliteHelper(null, getApplicationContext());
			list = iSqlHelper.Query(
					"com.beautyhealth.UserCenter.Entity.UserMessage", null);
			if (list.size() == 0) {
				ToastUtil.show(getApplicationContext(), "请先登录");
			} else {

				intent.setClass(MeActivity.this, MessageBindingActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_up_in,
						R.anim.slide_down_out);
			}
   
			break;
		default:
			ToastUtil.show(getApplicationContext(), "输入有误!");

		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)// 主要是对这个函数的复写
	{
		// TODO Auto-generated method stub

		if ((keyCode == KeyEvent.KEYCODE_BACK)
				&& (event.getAction() == KeyEvent.ACTION_DOWN)) {
			if (System.currentTimeMillis() - firstBackKeyTime > 2000) // 2s内再次选择back键有效
			{
				Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
				firstBackKeyTime = System.currentTimeMillis();
			} else {
				BluetoothAdapter bluetoothAdapter = BluetoothAdapter
						.getDefaultAdapter();
				bluetoothAdapter.disable();
				ISqlHelper iSqlHelper = new SqliteHelper(null,getApplicationContext());
				List<Object> ls1 = iSqlHelper.Query("com.beautyhealth.Setting.GPSSetting.Entity.GPSStatueAndTime",null);
				Intent intentService ;
				ServiceState aService=new ServiceState();
				aService.ServiceName="LocationUploading";
				//停止这个后台的服务 
		    	aService.setCurrentServiceStateInDB("0", this);
		    	//停止这个后台的服务	
		    	intentService = new Intent(this,GDGpsService.class);
		    	intentService.setAction(aService.ServiceName);
		    	intentService.putExtra("Time","0"); 
		    	intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	stopService(intentService);
		    	Intent _Intent = new Intent(this,LocationService.class);
				stopService(_Intent);
				finish();
				System.exit(0); // 凡是非零都表示异常退出!0表示正常退出!
			}

			return true;

		}
		return super.onKeyDown(keyCode, event);
	}

}
