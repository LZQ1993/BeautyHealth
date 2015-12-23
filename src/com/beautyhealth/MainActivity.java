package com.beautyhealth;

import java.util.List;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWComponent.TabBarFragment;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.PersonHealth.PersonHealthActivity;
import com.beautyhealth.PersonHealth.MyLocation.Activity.GDGpsService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.LocationService;
import com.beautyhealth.PersonHealth.MyLocation.Activity.ServiceState;
import com.beautyhealth.PrivateDoctors.PrivateDoctorsActivity;
import com.beautyhealth.SafeGuardianship.SafeGuardianshipActivity;
import com.beautyhealth.Setting.CallNumSetting.Entity.UserLocal;
import com.beautyhealth.UserCenter.Activity.AutoLoginService;

public class MainActivity extends NavAndTabBarActivity implements
		OnClickListener {
	private Button personHealth, privateDoctors, safeGuardianship,
			callCenter;
	private Animation scaleAnim;
	private long firstBackKeyTime = 0;

	private TabBarFragment tabBarFragment;
	private Intent service;

	private static final String MYACTION = "Success";
	private static final String MYACTION1 = "Failure";
	private LocalBroadcastManager localBroadcastManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initNavBar("美年颐康", null, null);
		fetchUIFromLayout();
		setListener();
		localBroadcastManager = LocalBroadcastManager
				.getInstance(getApplicationContext());
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.beautyhealth.UserCenter.Activity.AutoLogin");
		localBroadcastManager.registerReceiver(localReceiver, filter);

	}

	private void setListener() {
		personHealth.setOnClickListener((OnClickListener) this);

		privateDoctors.setOnClickListener((OnClickListener) this);
		safeGuardianship.setOnClickListener((OnClickListener) this);

		callCenter.setOnClickListener((OnClickListener) this);
		callCenter.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				List<Object> userLocal;
				ISqlHelper iSqlHelper = new SqliteHelper(null,
						getApplicationContext());
				userLocal = iSqlHelper
						.Query("com.beautyhealth.Setting.CallNumSetting.Entity.UserLocal",
								null);
				if (userLocal.size() > 0) {
					UserLocal retuserLocal = (UserLocal) userLocal.get(0);
					if (retuserLocal != null) {
						Intent _intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + retuserLocal.getTel()));
						startActivity(_intent);
					} else {
						new AlertDialog.Builder(MainActivity.this)
								.setTitle("错误")
								.setMessage("数据未加载...")
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

				} else {
					new AlertDialog.Builder(MainActivity.this).setTitle("错误")
							.setMessage("未设置呼叫中心！")
							.setPositiveButton("确定", null).show();
				}
				return true;
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void fetchUIFromLayout() {
		personHealth = (Button) findViewById(R.id.personHealth);

		privateDoctors = (Button) findViewById(R.id.privateDoctors);
		safeGuardianship = (Button) findViewById(R.id.safeGuardianship);

		callCenter = (Button) findViewById(R.id.callCenter);
		scaleAnim = AnimationUtils.loadAnimation(this, R.anim.anim_scale);

	}

	@Override
	public void onClick(View view) {

		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.personHealth:
			personHealth.startAnimation(scaleAnim);
			intent.setClass(MainActivity.this, PersonHealthActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			finish();
			break;

		case R.id.privateDoctors:
			privateDoctors.startAnimation(scaleAnim);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(MainActivity.this, PrivateDoctorsActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			finish();
			break;
		case R.id.safeGuardianship:
			safeGuardianship.startAnimation(scaleAnim);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(MainActivity.this, SafeGuardianshipActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			finish();
			break;
		case R.id.callCenter:
			callCenter.startAnimation(scaleAnim);
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("为了避免误触，请您长按呼叫中心键!")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).setCancelable(false).show();
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
				Intent _Intent = new Intent(this,LocationService.class);
				stopService(_Intent);
				//停止这个后台的服务 
		    	aService.setCurrentServiceStateInDB("0", this);
		    	//停止这个后台的服务	
		    	intentService = new Intent(this,GDGpsService.class);
		    	intentService.setAction(aService.ServiceName);
		    	intentService.putExtra("Time","0"); 
		    	intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	stopService(intentService);
				finish();
				System.exit(0); // 凡是非零都表示异常退出!0表示正常退出!
			}

			return true;

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		localBroadcastManager.unregisterReceiver(localReceiver);

	}

	private BroadcastReceiver localReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("tag", "onReceive");

			if (MYACTION.equals(intent.getStringExtra("state"))) {
				ToastUtil.show(MainActivity.this, "自动登录成功");

			}
			if (MYACTION1.equals(intent.getStringExtra("state"))) {
				ToastUtil.show(MainActivity.this, "自动登录失败");

			}
			service = new Intent(getApplicationContext(),
					AutoLoginService.class);
			stopService(service);

		}

	};

}
