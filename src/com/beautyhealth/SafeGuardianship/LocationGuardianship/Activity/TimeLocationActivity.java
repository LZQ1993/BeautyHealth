package com.beautyhealth.SafeGuardianship.LocationGuardianship.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.TabBarFragment;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.PersonHealth.MyLocation.Entity.AMapUtil;
import com.beautyhealth.UserCenter.Entity.BindingMessage;
import com.beautyhealth.UserCenter.Entity.UserMessage;
import com.beautyhealth.UserCenter.Entity.guardianshipLocation;

/**
 * 地理编码与逆地理编码功能介绍
 */
public class TimeLocationActivity extends DataRequestActivity implements
		OnGeocodeSearchListener, OnClickListener, OnMarkerClickListener,
		OnItemSelectedListener {
	private ProgressDialog progDialog = null;
	private GeocodeSearch geocoderSearch;
	private String addressName;
	private AMap aMap;
	private MapView mapView;
	private LatLonPoint latLonPoint = new LatLonPoint(39.90865, 116.39751);
	private Marker geoMarker;
	private Marker regeoMarker;
	private String userID;
	private Spinner selectDeep;
	private String[] itemDeep;
	private String[] UserID;
	private int searchType = 0;
	// 建表
	private List<Object> user;
	private String[] name = new String[2];
	private String[] userid = new String[2];
	
	private  ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_location);
		initNavBar("位置监护", "<返回", "轨迹");
		setAction(null);// user this------
		IsLocal = false;// user this------
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		UpMap();
		init();
	}

	private void downLoad(int position) {
		if(UserID[position].equals("FF")){
			new AlertDialog.Builder(this)
			.setTitle("提示")
			.setMessage("绑定信息为无效信息!")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							return;
						}
					}).setCancelable(false).show();
		}else{
		registeBroadcast();
		showProgressDialog("数据加载中，请稍候...");
		// user this------
		ClassFullName = "com.beautyhealth.UserCenter.Entity.guardianshipLocation";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		// myru.setIP(NetworkInfo.getServiceUrl());
		myru.setIP(null);
		myru.setMethod("SpotService", "getCurrentSpot");
		Map requestCondition = new HashMap();
		String condition[] = {"UserID"};
		String value[] = {UserID[position]};
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);

		this.setRequestUtility(myru);
		this.requestData();
		}
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
			regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		}

		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		progDialog = new ProgressDialog(this);
		aMap.setOnMarkerClickListener(this);
	}

	private void addMarkersToMap(String Longtitude, String Latitude) {
		latLonPoint = new LatLonPoint(Double.valueOf(Latitude),Double.valueOf(Longtitude));
		aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				AMapUtil.convertToLatLng(latLonPoint), 15));
		regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
	}

	/**
	 * 设置"父亲", "母亲"的选项
	 */
	private void UpMap() {
		selectDeep = (Spinner) findViewById(R.id.selectDeep);		
		ISqlHelper isqlHelper = new SqliteHelper(null, TimeLocationActivity.this);
		List<Object> ls = isqlHelper.Query("com.beautyhealth.UserCenter.Entity.BindingMessage", null);
		//int length = ls.size();
		if (ls.size() > 0) {
			itemDeep = new String[ls.size()];
			UserID = new String[ls.size()];
			for (int i = 0; i < ls.size(); i++) {
				BindingMessage um = (BindingMessage) ls.get(i);
				if(um.UserName.equals("")){
					itemDeep[i] = "未设置姓名";	
				}else{
					itemDeep[i] = um.UserName;	
				}
				UserID[i] = um.UnderGuardUserID;
			}
		}else{
			itemDeep = new String[1];
			UserID = new String[1];
			itemDeep[0] ="未指定";
			UserID[0] = "FF";
		}
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,itemDeep);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectDeep.setAdapter(adapter);
		selectDeep.setPrompt(" 请选择： ");
		selectDeep.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
                if(UserID[position].equals("FF")){
                	new AlertDialog.Builder(TimeLocationActivity.this)
        			.setTitle("提示")
        			.setMessage("无绑定数据")
        			.setPositiveButton("确定",
        					new DialogInterface.OnClickListener() {
        						@Override
        						public void onClick(
        								DialogInterface dialog,
        								int which) {
        							return;
        						}
        					}).setCancelable(false).show();
                }else{
    				downLoad(position);
                }

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	/**
	 * 响应地理编码
	 */
	public void getLatlon(String name) {
		showProgressDialog("地址数据加载中，请稍候...");
		GeocodeQuery query = new GeocodeQuery(name, "");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(LatLonPoint _latLonPoint) {
		showProgressDialog("地址数据加载中，请稍候...");
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	/**
	 * 地理编码查询回调
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		dismissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
				geoMarker.setPosition(AMapUtil.convertToLatLng(address
						.getLatLonPoint()));
				addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
						+ address.getFormatAddress();
				ToastUtil.show(TimeLocationActivity.this, addressName);
			} else {
				ToastUtil.show(TimeLocationActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(TimeLocationActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(TimeLocationActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(TimeLocationActivity.this,
					getString(R.string.error_other) + rCode);
		}
	}

	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		dismissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						AMapUtil.convertToLatLng(latLonPoint), 15));
				addressName = result.getRegeocodeAddress().getFormatAddress()
						+ "附近";
				regeoMarker.setTitle(addressName);
				regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
				ToastUtil.show(TimeLocationActivity.this, addressName);
			} else {
				ToastUtil.show(TimeLocationActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(TimeLocationActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(TimeLocationActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(TimeLocationActivity.this,
					getString(R.string.error_other) + rCode);
		}
	}

	@Override
	public void onClick(View v) {

	}

	/**
	 * 右按钮监听函数
	 */
	public void onNavBarRightButtonClick(View view) {
		if(UserID[selectDeep.getSelectedItemPosition()].toString().equals("FF")){
			new AlertDialog.Builder(TimeLocationActivity.this)
			.setTitle("提示")
			.setMessage("请先绑定被监护人后，再进行查询!")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							return;
						}
					}).setCancelable(false).show();
		}else {
			Intent intent = new Intent();
			intent.putExtra("UsedID",UserID[selectDeep.getSelectedItemPosition()]);
			intent.setClass(TimeLocationActivity.this, LocationPathActivity.class);
			TimeLocationActivity.this.startActivity(intent);
		}
		
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		getAddress(latLonPoint);

		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	 @Override
	 public void updateView() {
	 super.updateView();
	 dismissProgressDialog();
	 if (dataResult != null) {
	 DataResult realData = (DataResult) dataResult;
	 if (realData.getResultcode().equals("1")) {
		 guardianshipLocation msg = (guardianshipLocation)realData.getResult().get(0);
	     addMarkersToMap(msg.Longtitude,msg.Latitude);
	
	   }else{
		   new AlertDialog.Builder(this)
			.setTitle("提示")
			.setMessage("暂无数据")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							return;
						}
					}).setCancelable(false).show();
	   }
	 }else{
		 new AlertDialog.Builder(this)
			.setTitle("失败")
			.setMessage("网络加载失败")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							return;
						}
					}).setCancelable(false).show();
	 }
	 }

}