package com.beautyhealth.PersonHealth.MyLocation.Activity;

import java.text.SimpleDateFormat;
import java.util.List;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.HttpUtil;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.UserCenter.Entity.UserMessage;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service implements AMapLocationListener{
	// 声明AMapLocationClient类对象
		public AMapLocationClient mLocationClient1 = null;
		// 声明mLocationOption对象
		public AMapLocationClientOption mLocationOption1 = null;
		private String measureTime = "0";
		private String longtitude = "0";
		private String latitude = "0";
		private String Altitude = "";
		WakeLock mWakeLock=null;
		private String UserID;
		private Intent endIntent;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// 初始化定位
		mLocationClient1 = new AMapLocationClient(getApplicationContext());
		mLocationClient1.setLocationListener(this);
		Location();	
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
			
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		releaseWakeLock();
		mLocationClient1.onDestroy();//销毁定位客户端
		super.onDestroy();
	}
	private void acquireWakeLock() {
		if (null == mWakeLock) {
			PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "myService");
			if (null != mWakeLock) {
				mWakeLock.acquire();
			}
		}
	}

	/**
	 * onDestroy时，释放设备电源锁
	 */
	private void releaseWakeLock() {
		if (null != mWakeLock) {
			mWakeLock.release();
			mWakeLock = null;
		}

	}
	private void Location() {
		acquireWakeLock();
		// 初始化定位参数
		mLocationOption1 = new AMapLocationClientOption();
		// 设置定位模式为高精度模式Hight_Accuracy，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		ConnectivityManager nw = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = nw.getActiveNetworkInfo();
		if (netinfo != null) {
			if (gps && netinfo.isAvailable()) {
				mLocationOption1
						.setLocationMode(AMapLocationMode.Hight_Accuracy);
			} else if (gps == false && netinfo.isAvailable() == true) {
				mLocationOption1
						.setLocationMode(AMapLocationMode.Battery_Saving);
			}
		} else {
			ToastUtil.show(getApplicationContext(), "请您打开网络后，在进行定位");
			return;
		}
		// 设置是否返回地址信息（默认返回地址信息）
		mLocationOption1.setNeedAddress(true);
		// 设置是否只定位一次,默认为false
		mLocationOption1.setOnceLocation(true);
		// 设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption1.setWifiActiveScan(true);
		// 定位进程不被杀死
		mLocationOption1.setKillProcess(false);
		// 设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption1.setMockEnable(false);
		// 设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption1.setInterval(-1);
		// 给定位客户端对象设置定位参数
		mLocationClient1.setLocationOption(mLocationOption1);
		// 启动定位
		mLocationClient1.startLocation();
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {		
		if (amapLocation != null) {
			if (amapLocation.getErrorCode() == 0) {
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				measureTime = sDateFormat.format(new java.util.Date());
				latitude = "" + amapLocation.getLatitude();// 获取经度
				longtitude = "" + amapLocation.getLongitude();// 获取纬度
				Altitude = "" + amapLocation.getAltitude();
				Network();
				mLocationClient1.stopLocation();// 停止定位
			} else {
				// 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
				Log.e("AmapError",
						"location Error, ErrCode:"
								+ amapLocation.getErrorCode() + ", errInfo:"
								+ amapLocation.getErrorInfo());
			}
		}

	}
   private void Network(){
	   String url = NetworkSetInfo.getServiceUrl()
				+ "/SpotService/uploadSpot";
		RequestParams params = new RequestParams();
		ISqlHelper iSqlHelper = new SqliteHelper(null,this);
		List<Object> list = iSqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
		UserMessage userMessage = (UserMessage) list.get(0);
		UserID = userMessage.UserID;
		String condition[] = { "UserID", "MeasureTime", "Altitude",
				"Longtitude", "Latitude" };
		String value[] = { UserID, measureTime, Altitude, longtitude,
				latitude };
		String strJson = JsonDecode.toJson(condition, value);
		params.put("json", strJson);
		HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable error) {
				Toast.makeText(getApplicationContext(),"网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
				Intent _Intent = new Intent(getApplicationContext(),LocationService.class);
				stopService(_Intent);
				return;
			}

			@Override
			public void onSuccess(String content) {
				
				Toast.makeText(getApplicationContext(),"ok", Toast.LENGTH_SHORT).show();
				releaseWakeLock();
				Intent _Intent = new Intent(getApplicationContext(),LocationService.class);
				stopService(_Intent);
			}

		});
	}
}
