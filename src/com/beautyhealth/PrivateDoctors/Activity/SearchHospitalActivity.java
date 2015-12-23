package com.beautyhealth.PrivateDoctors.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.R.string;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.RefreshableView;
import com.beautyhealth.Infrastructure.CWComponent.TabBarFragment;
import com.beautyhealth.Infrastructure.CWComponent.RefreshableView.PullToRefreshListener;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.PersonHealth.MyLocation.Activity.GDGpsService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.LocationService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.ServiceState;
import com.beautyhealth.PrivateDoctors.Assistant.HospitalListAdpter;
import com.beautyhealth.PrivateDoctors.Entity.Hospital;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class SearchHospitalActivity extends DataRequestActivity implements
		OnItemClickListener {
	private ListView lv_hospital;
	private List<Hospital> hospital;
	private HospitalListAdpter hospitalListAdpter;
	private long firstBackKeyTime = 0;
	private RefreshableView refreshableView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchhospital);
		setAction(null);// user this------
		IsLocal = false;// user this------
		initNavBar("绿色就医", null, "预约记录");
		initList();
		fetchUIFromLayout();
		initCommentData();
	}

	private void initCommentData() {
		registeBroadcast();
		showProgressDialog("加载中...");
		// user this------
		ClassFullName = "com.beautyhealth.PrivateDoctors.Entity.Hospital";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("HandyDoctorService", "searchHospital");
		Map requestCondition = new HashMap();
		String condition[] = { "page", "rows" };
		String value[] = { "-1", "18" };
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);
		this.setRequestUtility(myru);
		this.requestData();
	}

	@Override
	public void onNavBarRightButtonClick(View view) {
		ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
		List<Object> list = iSqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
		if (list.size() > 0) {

			Intent intent = new Intent();
			intent.setClass(SearchHospitalActivity.this,
					MyAppointInfoActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);

		} else {
			new AlertDialog.Builder(this).setTitle("提示")
					.setMessage("您处于离线状态,请登录再试").setPositiveButton("确定", null)
					.setCancelable(false).show();
		}

	}

	@Override
	public void updateView() {
		
		super.updateView();	
		dismissProgressDialog();
		if (dataResult != null) {
			// user this------
			DataResult realData = (DataResult) dataResult;
			if (realData.getResultcode().equals("1")) {
				hospital.clear();
				for (int i = 0; i < realData.getResult().size(); i++) {
					Hospital hospitalitem = (Hospital) realData.getResult()
							.get(i);
					hospital.add(hospitalitem);
				}
				hander.sendEmptyMessage(0);
			} else {
				new AlertDialog.Builder(this)
						.setTitle("错误")
						.setMessage("暂无数据！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}
								}).setCancelable(false).show();
				return;
			}
		} else {
			new AlertDialog.Builder(this)
					.setTitle("错误")
					.setMessage("网络加载失败！")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).setCancelable(false).show();
		}
	}

	private void initList() {
		hospital = new ArrayList<Hospital>();
	}

	private void fetchUIFromLayout() {
		lv_hospital = (ListView) findViewById(R.id.lv_hospital);
		LinearLayout empty_view = (LinearLayout)findViewById(R.id.empty_view);
		empty_view.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				initCommentData();
			}
		});
		lv_hospital.setEmptyView(empty_view);
	}

	private void setListener() {
		hospitalListAdpter = null;
		hospitalListAdpter = new HospitalListAdpter(this, hospital, myListener);
		lv_hospital.setAdapter(hospitalListAdpter);
		lv_hospital.setOnItemClickListener((OnItemClickListener) this);
		
	}

	private OnClickListener myListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();

			new AlertDialog.Builder(SearchHospitalActivity.this)
					.setTitle("简介：")
					.setMessage(hospital.get(position).HospitalBriefly)
					.setPositiveButton("取消",

					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					}).setCancelable(false).show();
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Hospital hospitalItem = hospital.get(position);
		Intent intent = new Intent();
		intent.putExtra("HospitalID", hospitalItem.HospitalID);
		intent.putExtra("HospitalName", hospitalItem.HospitalName);
		intent.setClass(getApplicationContext(), DoctorBrieflyActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
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

	private Handler hander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setListener();
		}
	};
}
