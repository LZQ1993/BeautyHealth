package com.beautyhealth.PrivateDoctors;

import java.text.SimpleDateFormat;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.HttpUtil;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.PersonHealth.MyLocation.Activity.GDGpsService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.LocationService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.ServiceState;
import com.beautyhealth.PrivateDoctors.Activity.AppointmentActivity;
import com.beautyhealth.PrivateDoctors.Activity.MedicalConsultActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class PrivateDoctorsActivity extends DataRequestActivity implements OnClickListener{
    private Button herbalistdoctor,westerndoctor,appointment;
    private String starttime,endtime;
    private String UUID;
	private long firstBackKeyTime = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privatedoctors);
		initNavBar("专家咨询",null, null);
		fetchUIFromLayout();
		setListener();
	}

	private void setListener() {
		herbalistdoctor.setOnClickListener((OnClickListener) this);
		westerndoctor.setOnClickListener((OnClickListener) this);
		appointment.setOnClickListener((OnClickListener) this);
	}

	private void fetchUIFromLayout() {
		herbalistdoctor = (Button) findViewById(R.id.btn_pd_herbalistdoctor);
		westerndoctor = (Button) findViewById(R.id.btn_pd_westerndoctor);
		appointment = (Button) findViewById(R.id.btn_pd_appointment);

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch(v.getId()){
		case R.id.btn_pd_herbalistdoctor: 
			dataUpLoading("1");
			 break;
		case R.id.btn_pd_westerndoctor:
			dataUpLoading("0");
			 break;
		case R.id.btn_pd_appointment:
			 intent.setClass(PrivateDoctorsActivity.this,AppointmentActivity.class);
			 startActivity(intent);
			 overridePendingTransition(R.anim.slide_up_in,
						R.anim.slide_down_out);
			 break;
		default: ToastUtil.show(getApplicationContext(), "输入有误!"); break;
		
		}
		
	}
    private void dataUpLoading(final String key) {
		showProgressDialog("数据加载中，请稍候...");
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
		String Time = sDateFormat.format(new java.util.Date());
		starttime = Time +"-01 "+"00:00:00";
		endtime = Time+"-31 "+"23:59:59";
	    String url = NetworkSetInfo.getServiceUrl() + "/PrivateDoctor/queryQuestion";        
	        RequestParams params = new RequestParams();
	        String condition[] = { "StartTime", "EndTime", "UserID", "page", "rows","DoctorType"};
			String value[] = {starttime,endtime, "999", "-1", "18" ,key};
			String strJson = JsonDecode.toJson(condition, value);
	        params.put("json",strJson);
	        HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(Throwable error) {
					dismissProgressDialog();
	                 new AlertDialog.Builder(PrivateDoctorsActivity.this)
	                 .setTitle("错误")
	                 .setMessage("网络访问失败，请检查网络！"+error.toString())
	                 .setPositiveButton("确定",null)
	                 .show(); 
	                 return;
				}
				@Override
				public void onSuccess(String content) {
					dismissProgressDialog();
		                	Intent _intent = new Intent();
							if(key.equals("0")){
								_intent.setClass(PrivateDoctorsActivity.this, MedicalConsultActivity.class);
								_intent.putExtra("btnkey", "0");
								_intent.putExtra("data", content);
								
							}else{
								_intent.setClass(PrivateDoctorsActivity.this, MedicalConsultActivity.class);
								_intent.putExtra("btnkey", "1");
								_intent.putExtra("data",content);
								 
							}
							startActivity(_intent);	
							overridePendingTransition(R.anim.slide_up_in,
									R.anim.slide_down_out);
				}
	        	
	        });		
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
