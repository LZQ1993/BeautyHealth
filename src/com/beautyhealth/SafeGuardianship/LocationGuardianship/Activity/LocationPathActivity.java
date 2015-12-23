package com.beautyhealth.SafeGuardianship.LocationGuardianship.Activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.UserCenter.Entity.guardianshipLocation;

public class LocationPathActivity extends  DataRequestActivity 
{
	private AMap aMap;
	private MapView mapView;
	private Button replayButton;
	private SeekBar processbar;
	private Marker marker = null;// 当前轨迹点图案
	public Handler timer = new Handler();// 定时器
	public Runnable runnable = null;
    private String starttime,endtime;
    private EditText startTime,endTime;
	// 存放所有坐标的数组
	private ArrayList<LatLng> latlngList = new ArrayList<LatLng>();
	private ArrayList<LatLng> latlngList_path = new ArrayList<LatLng>();
	// private ArrayList<LatLng> latlngList_path1 = new ArrayList<LatLng>();

	Context context;
	@Override
	protected void onCreate( Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_replay);
		context = this;
		initNavBar("运动轨迹", "<返回", "查询");
		setAction(null);// user this------
		IsLocal = false;// user this------
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写	
		fetchUIFromLayout(); 
		setListener();	
		init();
	
	}
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		replayButton = (Button) findViewById(R.id.btn_replay);
		processbar = (SeekBar) findViewById(R.id.process_bar);
		processbar.setSelected(false);
		processbar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		// 进度条拖动时 执行相应事件
		processbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			// 复写OnSeeBarChangeListener的三个方法
			// 第一个时OnStartTrackingTouch,在进度开始改变时执行
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			// 第二个方法onProgressChanged是当进度发生改变时执行
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub

				
				latlngList_path.clear();
				if (progress != 0) {
					for (int i = 0; i < seekBar.getProgress(); i++) {
						latlngList_path.add(latlngList.get(i));
					}
					drawLine(latlngList_path, progress);
				}
			
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// 第三个是onStopTrackingTouch,在停止拖动时执行
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
				latlngList_path.clear();
				int current = seekBar.getProgress();
				if (current != 0) {
					for (int i = 0; i < seekBar.getProgress(); i++) {
						latlngList_path.add(latlngList.get(i));
					}
					drawLine(latlngList_path, current);
				}
			}
		});
		
		// 初始化runnable开始
		runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 要做的事情
				handler.sendMessage(Message.obtain(handler, 1));
				
			}
			
		};
		// 初始化runnable结束
		// TODO Auto-generated method stub
		if (aMap == null) {
			aMap = mapView.getMap();
		/*	if (aMap != null) {
				setUpMap();	
		}*/
		}
	}

	
	private void setListener() {
		
	}
	private void fetchUIFromLayout() {
		
	}
	private void dataUpLoading() {
		registeBroadcast();
		showProgressDialog("数据加载中，请稍候...");
		// user this------
		ClassFullName = "com.beautyhealth.UserCenter.Entity.guardianshipLocation";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		// myru.setIP(NetworkInfo.getServiceUrl());
		myru.setIP(null);
		myru.setMethod("SpotService", "querySpot");
		Map requestCondition = new HashMap();
		String condition[] = { "StartTime", "EndTime", "UserID", "page", "rows" };
		String value[] = { starttime,endtime,
				            getIntent().getStringExtra("UsedID"),"-1","-1"};
		
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);

		this.setRequestUtility(myru);
		this.requestData();
	}
	

	/**
	 * 右按钮监听函数
	 */
	public void onNavBarRightButtonClick(View view) {
		showDialog();
    	return;
		
		
	}
	 private void showDialog(){ 	 
	    	View itemview = getLayoutInflater().inflate(R.layout.showseacherdialog, null);        
			startTime = (EditText) itemview.findViewById(R.id.pressureguardianship_start);
			endTime = (EditText)  itemview.findViewById(R.id.pressureguardianship_end);
			Calendar c = Calendar.getInstance();
			startTime.setText(c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH)+" "+"00:00:00");
			endTime.setText(c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH)+" "+"23:59:59");
			
			startTime.setInputType(InputType.TYPE_NULL);
			endTime.setInputType(InputType.TYPE_NULL);
			startTime.setOnClickListener(new OnClickListener() {
	    
				@Override
				public void onClick(View v) {
					Calendar c = Calendar.getInstance();
					new DatePickerDialog(LocationPathActivity.this,
							new DatePickerDialog.OnDateSetListener() {

								@Override
								public void onDateSet(DatePicker view, int year,
										int monthOfYear, int dayOfMonth) {
									int month = (monthOfYear + 1);
									String strdate = year + "-";
									if (month < 10) {
										strdate = strdate + "0" + month + "-";
									} else {
										strdate = strdate + month + "-";
									}
									if (dayOfMonth < 10) {
										strdate = strdate + "0" + dayOfMonth + " ";
									} else {
										strdate = strdate + dayOfMonth + " ";
									}

									startTime.setText(strdate + "00:00:00");
								}
							}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
									.get(Calendar.DAY_OF_MONTH)).show();
				}
			});

			endTime.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Calendar c = Calendar.getInstance();
					new DatePickerDialog(LocationPathActivity.this,
							new DatePickerDialog.OnDateSetListener() {

								@Override
								public void onDateSet(DatePicker view, int year,
										int monthOfYear, int dayOfMonth) {
									int month = (monthOfYear + 1);
									String strdate = year + "-";
									if (month < 10) {
										strdate = strdate + "0" + month + "-";
									} else {
										strdate = strdate + month + "-";
									}
									if (dayOfMonth < 10) {
										strdate = strdate + "0" + dayOfMonth + " ";
									} else {
										strdate = strdate + dayOfMonth + " ";
									}

									endTime.setText(strdate + "23:59:59");
								}
							}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
									.get(Calendar.DAY_OF_MONTH)).show();
				}
			});
	        //对话框
	        new AlertDialog.Builder(LocationPathActivity.this)
	        .setView(itemview)
	        .setTitle("提示：输入条件")
	        .setPositiveButton("确定", new Dialog.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	starttime = startTime.getText().toString();
	            	endtime =endTime.getText().toString();
	            	dataUpLoading();
	            	return;
	            }
	        })
	        .setNegativeButton("取消", null)
	        .setCancelable(false) //触摸不消失
	        .show();
	        return;
	    }

	private void drawLine(ArrayList<LatLng> list,int current) {
		// TODO Auto-generated method stub
		aMap.clear();
		LatLng replayGeoPoint = latlngList.get(current - 1);
		if (marker != null) {
			marker.destroy();
		}
		// 添加汽车位置
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions
		.position(replayGeoPoint)
		.title("起点")
		.snippet(" ")
		.icon(BitmapDescriptorFactory
				.fromBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.car)))
						.anchor(0.5f, 0.5f);
		marker = aMap.addMarker(markerOptions);
		// 增加起点开始
		aMap.addMarker(new MarkerOptions()
		.position(latlngList.get(0))
		.title("起点")
		.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
				.decodeResource(
						getResources(),
						R.drawable.nav_route_result_start_point))));
		// 增加起点结束
		if (latlngList_path.size() > 1) {
			PolylineOptions polylineOptions = (new PolylineOptions())
					.addAll(latlngList_path)
					.color(Color.rgb(9, 129, 240)).width(6.0f);
			aMap.addPolyline(polylineOptions);
		}
		if (latlngList_path.size() == latlngList.size()) {
			aMap.addMarker(new MarkerOptions()
					.position(latlngList.get(latlngList.size() - 1))
					.title("终点")
					.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
							.decodeResource(
									getResources(),
									R.drawable.nav_route_result_end_point))));
		}
	}
	
	
	// 根据定时器线程传递过来指令执行任务
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				int curpro = processbar.getProgress();
				if (curpro != processbar.getMax()) {
					processbar.setProgress(curpro + 1);
					timer.postDelayed(runnable, 500);// 延迟0.5秒后继续执行
				} else {
					Button button = (Button) findViewById(R.id.btn_replay);
					button.setText(" 回放 ");// 已执行到最后一个坐标 停止任务
				}
			}
		}
	};

	private void setUpMap() {
		// 设置进度条最大长度为数组长度
		processbar.setMax(latlngList.size());
		aMap.setMapType(AMap.MAP_TYPE_NORMAL);
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngList.get(0), 18));
	}

	public void btn_replay_click(View v) {
		// 根据按钮上的字判断当前是否在回放
		if (replayButton.getText().toString().trim().equals("回放")) {
			if (latlngList.size() > 0) {
				// 假如当前已经回放到最后一点 置0
				if (processbar.getProgress() == processbar.getMax()) {
					processbar.setProgress(0);
				}
				// 将按钮上的字设为"停止" 开始调用定时器回放
				replayButton.setText(" 停止 ");
				timer.postDelayed(runnable, 10);
			}
		} else {
			// 移除定时器的任务
			timer.removeCallbacks(runnable);
			replayButton.setText(" 回放 ");
		}
		
//				dataUpLoading();
			

		
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

	 @Override
	 public void updateView() {
	 super.updateView();
	 dismissProgressDialog();
	 aMap.clear();
	 if (dataResult != null) {
	 DataResult realData = (DataResult) dataResult;
	 if (realData.getResultcode().equals("1")) {
		 latlngList.clear();
		 for(int i=0;i<realData.getResult().size();i++){
			 guardianshipLocation msg = (guardianshipLocation)realData.getResult().get(i);	
			 LatLng marker = new LatLng(Double.valueOf(msg.Latitude),Double.valueOf(msg.Longtitude));
			 latlngList.add(marker);
		 }
		 setUpMap();
	   }else{
		   new AlertDialog.Builder(LocationPathActivity.this)
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
		 new AlertDialog.Builder(LocationPathActivity.this)
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
