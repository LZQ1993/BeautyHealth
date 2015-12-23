package com.beautyhealth.PersonHealth;

import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWComponent.TabBarFragment;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.PersonHealth.AbilityFunction.Activity.DeskClockMainActivity;
import com.beautyhealth.PersonHealth.BloodPressureMeasure.Activity.BloodPressureMeasureActivity;
import com.beautyhealth.PersonHealth.BloodSugarMearsure.Activity.BloodSugarMeasureActivity;
import com.beautyhealth.PersonHealth.MedicalReport.MedicalReportActivity;
import com.beautyhealth.PersonHealth.MyLocation.Activity.GDGpsService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.LocationService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.RouteActivity;
import com.beautyhealth.PersonHealth.MyLocation.Activity.ServiceState;
import com.beautyhealth.PersonHealth.Pedometer.PedometerActivity;
import com.beautyhealth.PrivateDoctors.PrivateDoctorsActivity;

public class PersonHealthActivity extends NavAndTabBarActivity implements OnClickListener{
     private ImageButton bloodpressure,bloodsugar,pedometer,abilityfunction,medicalreport,mylocation;
	 private int positionTag;
	 private long firstBackKeyTime = 0;
     @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personhealth);
		initNavBar("个人健康", null, null);
		fetchUIFromLayout();
		setListener();
	}

	private void setListener() {
		bloodpressure.setOnClickListener((OnClickListener) this);
		bloodsugar.setOnClickListener((OnClickListener) this);
		pedometer.setOnClickListener((OnClickListener) this);
		abilityfunction.setOnClickListener((OnClickListener) this);
		medicalreport.setOnClickListener((OnClickListener) this);
		mylocation.setOnClickListener((OnClickListener) this);
		
	}

	private void fetchUIFromLayout() {
		
		bloodpressure = (ImageButton) findViewById(R.id.btn_bloodpressure);
		bloodsugar = (ImageButton) findViewById(R.id.btn_bloodsugar);
		pedometer = (ImageButton) findViewById(R.id.btn_pedometer);
		abilityfunction = (ImageButton) findViewById(R.id.btn_abilityfunction);
		medicalreport = (ImageButton) findViewById(R.id.btn_medicalreport);
		mylocation = (ImageButton) findViewById(R.id.btn_mylocation);
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch(v.getId()){
		case R.id.btn_bloodpressure:
			intent.setClass(getApplicationContext(), BloodPressureMeasureActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in,
					R.anim.slide_down_out);
			break;
		case R.id.btn_bloodsugar:	
			intent.setClass(PersonHealthActivity.this, BloodSugarMeasureActivity.class);
			
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in,
					R.anim.slide_down_out);
			break;
		case R.id.btn_pedometer:			
			intent.setClass(PersonHealthActivity.this, PedometerActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in,
					R.anim.slide_down_out);
			break;
		case R.id.btn_abilityfunction:
			intent.setClass(PersonHealthActivity.this,DeskClockMainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in,
					R.anim.slide_down_out);
			break;
		case R.id.btn_medicalreport:
			intent.setClass(PersonHealthActivity.this, MedicalReportActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in,
					R.anim.slide_down_out);
			break;
		case R.id.btn_mylocation:
			intent.setClass(PersonHealthActivity.this, RouteActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in,
					R.anim.slide_down_out);
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
				BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
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
