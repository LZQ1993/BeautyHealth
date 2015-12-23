package com.beautyhealth.MembersCenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.RefreshableView;
import com.beautyhealth.Infrastructure.CWComponent.RefreshableView.PullToRefreshListener;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.MembersCenter.Assistant.AdvertisementAdpter;
import com.beautyhealth.MembersCenter.Entity.AdvertisementInfo;
import com.beautyhealth.PersonHealth.MyLocation.Activity.GDGpsService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.LocationService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.ServiceState;

public class MembersCenterAcitivity extends DataRequestActivity implements
		OnItemClickListener {
	private ListView lv_ad_show;
	private List<AdvertisementInfo> advertisementInfo;
	private AdvertisementAdpter advertisementAdpter;
	private long firstBackKeyTime = 0;
	private String time;
	private RefreshableView refreshableView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memberscenter);
		setAction(null);// user this------
		IsLocal = false;// user this------
		initNavBar("会员中心", null, null);
		initList();
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		time = sDateFormat.format(new java.util.Date());
		fetchUIFromLayout();
		initCommentData();
	}

	private void initCommentData() {
		registeBroadcast();
		showProgressDialog("加载中...");
		// user this------
		ClassFullName = "com.beautyhealth.MembersCenter.Entity.AdvertisementInfo";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("MembersService", "searchAd");
		Map requestCondition = new HashMap();
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
		String Time = sDateFormat.format(new java.util.Date());
		String stime = Time +"-01 "+"00:00:00";
		String etime = Time+"-31 "+"23:59:59";
		String condition[] = {"StartTime", "EndTime","page","rows"};
		String value[] = {stime,etime,"-1","18"};
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);
		this.setRequestUtility(myru);
		this.requestData();
	}

	@Override
	public void updateView() {
		super.updateView();
		dismissProgressDialog();
		if (dataResult != null) {
			// user this------
			DataResult realData = (DataResult) dataResult;
			if (realData.getResultcode().equals("1")&&realData.result.size()>0) {
				advertisementInfo.clear();
				for (int i = 0; i < realData.getResult().size(); i++) {
					AdvertisementInfo advertisementitem = (AdvertisementInfo) realData
							.getResult().get(i);
					advertisementInfo.add(advertisementitem);
				}
				hander.sendEmptyMessage(0);
			} else {
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
			return;
		}
	}

	private void initList() {
		advertisementInfo = new ArrayList<AdvertisementInfo>();
	}

	private void fetchUIFromLayout() {
		lv_ad_show = (ListView) findViewById(R.id.lv_ad_show);
	
		LinearLayout empty_view = (LinearLayout)findViewById(R.id.empty_view);
		empty_view.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {			
				initCommentData();
			}
		});
		lv_ad_show.setEmptyView(empty_view);
	}

	private void setListener() {
		advertisementAdpter = null;
		advertisementAdpter = new AdvertisementAdpter(this, advertisementInfo,
				myListener);
		lv_ad_show.setAdapter(advertisementAdpter);
		lv_ad_show.setOnItemClickListener((OnItemClickListener) this);
		
	}

	private OnClickListener myListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();

			new AlertDialog.Builder(MembersCenterAcitivity.this)
					.setTitle("简介：")
					.setMessage(advertisementInfo.get(position).AdBriefly)
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
		AdvertisementInfo advertisementItem = advertisementInfo.get(position);
		Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("advertisementItem",advertisementItem);
        intent.putExtras(bundle);
        intent.setClass(getApplicationContext(), FavorableInfoActivity.class);
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
	
	private Handler hander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	setListener() ;
        	/*advertisementAdpter.updateList(advertisementInfo);
        	advertisementAdpter.notifyDataSetChanged();*/
        }
    };
}
