package com.beautyhealth;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.beautyhealth.Infrastructure.CWComponent.AppInfo;
import com.beautyhealth.Infrastructure.CWComponent.ImageCal;
import com.beautyhealth.Infrastructure.CWDataDecode.DataDecode;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.IDataDecode;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.HttpUtil;
import com.beautyhealth.Infrastructure.CWDataRequest.LoadImage;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.Infrastructure.CWDomain.Ad;
import com.beautyhealth.Infrastructure.CWFileSystem.IFileSystem;
import com.beautyhealth.Infrastructure.CWFileSystem.LocalFileSystem;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.PersonHealth.MyLocation.Activity.GpsLocationReceiver;
import com.beautyhealth.PersonHealth.MyLocation.Activity.GDGpsService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.ServiceState;
import com.beautyhealth.Setting.BluetoothSetting;
import com.beautyhealth.Setting.GPSSetting.Entity.GPSStatueAndTime;
import com.beautyhealth.UserCenter.Activity.AutoLoginService;
import com.beautyhealth.UserCenter.Activity.LoginActivity;
import com.beautyhealth.UserCenter.Entity.UserMessage;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class StartupActivity extends DataRequestActivity {
	private Intent service;
	private ImageView ad;
	private View parentView;
	private AppInfo appInfo;

	private LoadImage load;
	private IFileSystem myfilesystem;
	public String localPath;
	private String webaddress;
	private String filename;
	private long size;
	private int result;
	private int reqWidth;
	private int reqHeight;

	
	public IDataDecode dataDecode = null;
	public Object dataResult = null;// ////////////
	/**
	 * onCreate
	 */
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager wm = this.getWindowManager();
		if (dataDecode == null) {
			dataDecode = new DataDecode(this);
			dataResult = new Object();
		}
		reqWidth = wm.getDefaultDisplay().getWidth();
		reqHeight = wm.getDefaultDisplay().getHeight();
		appInfo = new AppInfo(getApplicationContext());
		parentView = getLayoutInflater().inflate(R.layout.activity_startup,
				null);
		ad = (ImageView) parentView.findViewById(R.id.imageView1);
		myfilesystem = new LocalFileSystem(getApplicationContext());
		localPath = myfilesystem.getLocalPath();
		setAction(null);// user this------
		IsLocal = false;// user this------
		if (!appInfo.isNewVersion()) {
			getFiles(this, localPath);
			getadimage(); // 获取广告的地址
		}
		setContentView(parentView);
		// 延时线程
		new Thread() {
			public void run() {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				BluetoothAdapter bluetoothAdapter = BluetoothAdapter
						.getDefaultAdapter();
				ISqlHelper iSqlHelper = new SqliteHelper(null,
						getApplicationContext());
				// 判断是否为首次登陆
				Intent intent = new Intent();
				if (appInfo.isNewVersion()) {
					// 建表
					iSqlHelper
							.CreateTable("com.beautyhealth.Setting.GPSSetting.Entity.GPSStatueAndTime");
					iSqlHelper
							.CreateTable("com.beautyhealth.UserCenter.Entity.FamilyNumberMessage");
					iSqlHelper
							.CreateTable("com.beautyhealth.Setting.CallNumSetting.Entity.UserLocal");
					iSqlHelper
							.CreateTable("com.beautyhealth.Setting.BluetoothSetting");
					iSqlHelper
							.CreateTable("com.beautyhealth.UserCenter.Entity.UserMessage");
					iSqlHelper
							.CreateTable("com.beautyhealth.UserCenter.Entity.BindingMessage");
					bluetoothAdapter.disable();
					intent.setClass(StartupActivity.this, IntroActivity.class);
					intent.putExtra("goto", LoginActivity.class.getName());
					AppInfo.markCurrentVersion(); // 标记当前版本
					startActivity(intent);
					overridePendingTransition(R.anim.slide_up_in,
							R.anim.slide_down_out);
					finish();
				} else {

					List<Object> ls = iSqlHelper.Query(
							"com.beautyhealth.Setting.BluetoothSetting", null);
					if (ls.size() > 0) {
						BluetoothSetting bs = (BluetoothSetting) ls.get(0);
						if (bs.State.equals("1")) {
							bluetoothAdapter.enable();
						} else {
							bluetoothAdapter.disable();
						}
					} else {
						bluetoothAdapter.disable();
					}				
					List<Object> um = iSqlHelper.Query(
							"com.beautyhealth.UserCenter.Entity.UserMessage",
							null);
					if (um.size() > 0) {
						UserMessage u = (UserMessage) um.get(0);
						SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
						boolean isChecked = config.getBoolean("isChecked", false);
						if (u.PasswordType.equals("2")&&isChecked) {

							service = new Intent(getApplicationContext(),
									AutoLoginService.class);
							startService(service);
							intent.setClass(StartupActivity.this,
									MainActivity.class);
							startActivity(intent);
							overridePendingTransition(R.anim.slide_up_in,
									R.anim.slide_down_out);
							finish();
						} else {
							intent.setClass(StartupActivity.this,
									LoginActivity.class);
							intent.putExtra("goto",
									MainActivity.class.getName());
							startActivity(intent);
							overridePendingTransition(R.anim.slide_up_in,
									R.anim.slide_down_out);
							finish();
						}
					} else {
						intent.setClass(StartupActivity.this,
								LoginActivity.class);
						intent.putExtra("goto", MainActivity.class.getName());
						startActivity(intent);
						overridePendingTransition(R.anim.slide_up_in,
								R.anim.slide_down_out);
						finish();
					}
				}
			}
		}.start();
	}

	public void getadimage() {
		String url = NetworkSetInfo.getServiceUrl() + "/PicService/queryPic";
		RequestParams params = new RequestParams();
		String condition[] = { "page", "rows", "MobileCode", "TypeID" };
		String value[] = { "-1", "3", "1", "1" };
		String strJson = JsonDecode.toJson(condition, value);
		params.put("json", strJson);
		HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable error) {
				
			}

			@Override
			public void onSuccess(String content) {
				ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.Ad";
				if (content != null) {
					dataResult = dataDecode.decode(content, ClassFullName);
					DataResult realData = (DataResult) dataResult;
					if (realData.getResultcode().equals("1")) {
						load = new LoadImage(localPath);
						for (int i = 0; i < realData.getResult().size(); i++) {
							Ad msg = (Ad) realData.getResult().get(i);
							webaddress = msg.getWebAddress();
							filename = msg.getPathAndFileName();
							size = Long.parseLong(msg.getSize());
						}
						updateImageShow();
					}
				}
			}

		});
	}

	@Override
	public void updateView() {
		super.updateView();
		dismissProgressDialog();
		if (dataResult != null) {
			DataResult realData = (DataResult) dataResult;
			if (realData.getResultcode().equals("1")) {
				load = new LoadImage(localPath);
				for (int i = 0; i < realData.getResult().size(); i++) {
					Ad msg = (Ad) realData.getResult().get(i);
					webaddress = msg.getWebAddress();
					filename = msg.getPathAndFileName();
					size = Long.parseLong(msg.getSize());
				}
				updateImageShow();
			}
		}
	}

	private void updateImageShow() {
		// mHandler.post(runnable);
		new Thread(runnable).start();
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			loadImageOnView();
		}
	};

	private void loadImageOnView() {
		// TODO Auto-generated method stub
		try {
			result = load.download(webaddress, filename, size);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("sss", e.getMessage());
		}
		String webfilename = filename.substring(filename.lastIndexOf("/") + 1);
		Log.e("webfilename", webfilename);
		if (result == 1) {
			File files = new File(localPath); // 创建文件对象
			File[] file = files.listFiles();
			for (int iFileLength = 0; iFileLength < file.length; iFileLength++) {
				if (!file[iFileLength].isDirectory()) {
					String name = file[iFileLength].getName();
					Log.e("filename", name);
					if ((name.trim().toLowerCase().endsWith(".jpg") || name
							.trim().toLowerCase().endsWith(".png"))
							& !name.equals(webfilename)) {
						Log.e("执行删除", "sssssss");
						file[iFileLength].delete();
					}
				}

			}
		}
	}

	private void getFiles(StartupActivity startupActivity, String url) {
		File files = new File(url); // 创建文件对象
		File[] file = files.listFiles();
		int fileLength = file.length;
		
			try {
				for (int iFileLength = 0; iFileLength < fileLength; iFileLength++) {
					Log.e("文件里有几张图片", file.length + "");
					// 判断是否为文件夹
					if (!file[iFileLength].isDirectory()) {
						String filename = file[iFileLength].getName();
						if (filename.trim().toLowerCase().endsWith(".jpg")
								|| filename.trim().toLowerCase()
										.endsWith(".png")) {
							ad.setImageBitmap(ImageCal
									.decodeSampledBitmapFromResource(localPath
											+ File.separator + filename,
											reqWidth, reqHeight));
						} else {
							ad.setImageResource(R.drawable.app_startup);
						}
					}else{
						ad.setImageResource(R.drawable.app_startup);
					}
				}
			} catch (Exception e) {
				e.printStackTrace(); // 输出异常信息

			}
		}

	/**
	 * 返回键按下
	 */
	@Override
	public void onBackPressed() {

	}

}
