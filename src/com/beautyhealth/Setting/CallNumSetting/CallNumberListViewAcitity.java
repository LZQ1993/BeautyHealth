package com.beautyhealth.Setting.CallNumSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Setting.CallNumSetting.Entity.CallCenterNumber;
import com.beautyhealth.Setting.CallNumSetting.Entity.UserLocal;
import com.beautyhealth.UserCenter.MeActivity;

public class CallNumberListViewAcitity extends DataRequestActivity implements AMapLocationListener {
	private Button btn_save;
	private ListView listView;
	private ListViewAdapter adapter;
	private String mylocaltion;
	private String city;

	private String[] citynames;
	private String[] phones;
	private List<Boolean> res1 = new ArrayList<Boolean>();
	private List<String[]> params = new ArrayList<String[]>();
	private int a = -1;
	private String dbTel, dbcity;
	//声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;
	//声明mLocationOption对象
	public AMapLocationClientOption mLocationOption = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customlistview);
		initNavBar("呼叫中心设置", "<返回", "保存");
		fetchUIFromLayout();
		//初始化定位
		mLocationClient = new AMapLocationClient(getApplicationContext());
		mLocationClient.setLocationListener(this);
		setAction(null);// user this------
		IsLocal = false;// user this------
		loadData();
		
		

	}
	private void Location() {
		//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为高精度模式Hight_Accuracy，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）  
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）  
        ConnectivityManager nw = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = nw.getActiveNetworkInfo();
        if(netinfo!=null){
	        if(gps&&netinfo.isAvailable()){
	        	 mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);	
	        }else if(gps==false&&netinfo.isAvailable()==true){
	        	 mLocationOption.setLocationMode(AMapLocationMode.Battery_Saving);
	        }
        }else{
        	Toast.makeText(getApplicationContext(),"请您打开网络后，在进行定位",Toast.LENGTH_SHORT).show();
        }       
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		//设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(true);
		//设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(-1);
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		//启动定位
		mLocationClient.startLocation();	
	}
	private void loadData() {// 网上下载
		registeBroadcast();
		// user this------
		showProgressDialog("数据加载中...");
		ClassFullName = "com.beautyhealth.Setting.CallNumSetting.Entity.CallCenterNumber";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("CallCenterService", "queryCallCenter");// CallCenterService接口名，方法名，到接口文件中去找。

		Map requestCondition = new HashMap();
		String condition[] = { "page", "rows" };
		String value[] = { "-1", "18" };
		requestCondition.put("json", JsonDecode.toJson(condition, value));
		myru.setParams(requestCondition);
		this.setRequestUtility(myru);
		this.requestData();
	}

	private void fetchUIFromLayout() {
		listView = (ListView) findViewById(R.id.listview1);
	}

	/**
	 * 右按钮监听函数
	 */
	public void onNavBarRightButtonClick(View view) {
		// 本地存储
		ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
		// iSqlHelper.CreateTable("com.beautyhealth.Setting.CallNumSetting.Entity.UserLocal");//建表
		UserLocal userLocal = new UserLocal();// 表中的列即类中的属性
		// 相当于向数据库中传值
		userLocal.key = "1";
		userLocal.CityName = citynames[adapter.SelectedPosition];
		userLocal.Tel = phones[adapter.SelectedPosition];
		iSqlHelper.SQLExec("delete from UserLocal where key='1'");// 删除表中原有的数据，保证只有一条
		iSqlHelper.Insert(userLocal);// 插入新的数据，即要保存的
		List<Object> users = iSqlHelper.Query(
				"com.beautyhealth.Setting.CallNumSetting.Entity.UserLocal",
				null);
		for (int i = 0; i < users.size(); i++) {
			UserLocal ecc = (UserLocal) users.get(i);
			Toast.makeText(getApplicationContext(),
					" 选择的城市:" + ecc.CityName + " 电话号：" + ecc.Tel + "",
					Toast.LENGTH_LONG).show();
		}
		Intent intent = new Intent();
		intent.setClass(CallNumberListViewAcitity.this, MeActivity.class);
		// 跳到SettingActivity后，它之前启动的Activity都被销毁。
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);

	}

	@Override
	public void updateView() {// 更新视图
		super.updateView();
		dismissProgressDialog();
		if (dataResult != null) {
			// user this------
			DataResult realData = (DataResult) dataResult;
			if (realData.getResult().size() > 0) {
				citynames = new String[realData.getResult().size()];
				phones = new String[realData.getResult().size()];
				for (int i = 0; i < realData.getResult().size(); i++) {
					CallCenterNumber auser = (CallCenterNumber) realData
							.getResult().get(i);
					citynames[i] = auser.CityName;
					phones[i] = auser.Tel;
					res1.add(i, false);
				}
				params.add(citynames);
				params.add(phones);
				adapter = new ListViewAdapter(CallNumberListViewAcitity.this,
						params, res1);
				listView.setAdapter(adapter);
				listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				Location();
			}
		} else {
			Toast.makeText(getApplicationContext(), "网络加载失败",
					Toast.LENGTH_SHORT).show();
		}
	}
	// 通过GPS获得当前位置，并将列表中的相应位置后标记为“当前位置”。
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null) {
	        if (amapLocation.getErrorCode() == 0) {
	        	a=-1;
	        	city =amapLocation.getCity();//城市信息	  
	        	ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
	        	List<Object> ls =  iSqlHelper.Query("com.beautyhealth.Setting.CallNumSetting.Entity.UserLocal",null);	
				if(ls.size()>0){
					UserLocal us = (UserLocal) ls.get(0);	
					dbcity = us.CityName;
					dbTel = us.Tel;
					for (int i = 0; i < citynames.length; i++) {
						if (city.equals(citynames[i])) {
							citynames[i] = citynames[i] + "（当前位置）";	
						}
						if(dbcity.equals(citynames[i])){
							a=i;
						}	
					}
	
			 }else{
				for (int i = 0; i < citynames.length; i++) {
					if (city.equals(citynames[i])) {
						citynames[i] = citynames[i] + "（当前位置）";	
						a=i;
					}			
			   }
			 }
			if(a==(-1)){
					res1.set(0, true);	
					//adapter.SelectedPosition=0;
				}else{
					res1.set(a, true);	
					//adapter.SelectedPosition=a;
				}   	
			adapter = new ListViewAdapter(CallNumberListViewAcitity.this, params,res1);
			listView.setAdapter(adapter);
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			mLocationClient.stopLocation();//停止定位 		
	    } else {
	        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
	        Log.e("AmapError","location Error, ErrCode:"
	            + amapLocation.getErrorCode() + ", errInfo:"
	            + amapLocation.getErrorInfo());
	       }
	   }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationClient.onDestroy();//销毁定位客户端
	}
}
