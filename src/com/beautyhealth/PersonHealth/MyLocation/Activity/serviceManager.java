package com.beautyhealth.PersonHealth.MyLocation.Activity;




import com.beautyhealth.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class serviceManager extends Activity implements OnClickListener{

	Button btn_service_start;
	Button btn_service_end;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_manager);
		fetchUIFromLayout();
		setListener();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private void setListener() {
		btn_service_start.setOnClickListener((OnClickListener) this);
		btn_service_end.setOnClickListener((OnClickListener) this);
	}

	private void fetchUIFromLayout() {
		btn_service_start = (Button)findViewById(R.id.btn_service_start);
		btn_service_end = (Button)findViewById(R.id.btn_service_end);
	}
	
	
	public void onClick(View v) {
    	   	//跳转到首页   
		Intent _intent  = new Intent();
		//_intent.setClass(getApplicationContext(), MyGpsService.class);  
		_intent.setClass(getApplicationContext(), GDGpsService.class);  
        if(v == btn_service_start){
        	startService(_intent); 
        }
        else if(v == btn_service_end){
        	stopService(_intent);
        }
	}
}


/*1 从Service继承一个类。
2 创建startService()方法。
3 创建endService()方法 重载onCreate方法和onDestroy方法，并在这两个方法里面来调用startService以及endService。
4 在startService中，通过getSystemService方法获取Context.LOCATION_SERVICE。
5 基于LocationListener实现一个新类。
     默认将重载四个方法onLocationChanged、
     onProviderDisabled、
     onProviderEnabled、
     onStatusChanged。
     对于onLocationChanged方法是我们更新最新的GPS数据的方法。
     一般我们的操作都只需要在这里进行处理。
6 调用LocationManager的requestLocationUpdates方法，
    来定期触发获取GPS数据即可。
    在onLocationChanged函数里面可以实现我们对得到的经纬度的最终操作。
7 最后在我们的Activity里面通过按钮来启动Service，停止Service。
*/