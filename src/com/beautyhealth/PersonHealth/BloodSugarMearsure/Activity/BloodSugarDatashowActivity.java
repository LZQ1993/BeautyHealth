package com.beautyhealth.PersonHealth.BloodSugarMearsure.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.CWChartLine2D;
import com.beautyhealth.Infrastructure.CWComponent.CWChartPoint2D;
import com.beautyhealth.Infrastructure.CWComponent.SplineChartView;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.PersonHealth.BloodPressureMeasure.Activity.BloodPressureDataShowActivity;
import com.beautyhealth.PersonHealth.BloodPressureMeasure.Entity.BloodPressureSearch;
import com.beautyhealth.PersonHealth.BloodSugarMearsure.Entity.BloodSugarSearch;
import com.beautyhealth.UserCenter.Activity.LoginActivity;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class BloodSugarDatashowActivity extends DataRequestActivity{
	private SplineChartView mspchar;
	private List<BloodSugarSearch> testDataPressure= new ArrayList<BloodSugarSearch>();
	private List<CWChartLine2D> Lines = new ArrayList<CWChartLine2D>();
	private List<Integer> colors = new ArrayList<Integer>();
	private double YAxistMax = 0;
	private double YAxistMin = 0;
    private EditText startTime,endTime;
	
	private String UUID;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAction(null);// user this------
		IsLocal = false;// user this------
        setLandscape();
        set2DLines();
	    setChart();
	    initActivity();
	    this.setTitle("myChart");
	    
	    InitData();
	}

	
	private void setLandscape() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
	}
	
	private void InitData() {
		ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
		List<Object> list=iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.UserMessage", null);
		if(list.size()>0){
			UserMessage userMessage=(UserMessage) list.get(0);
			UUID = userMessage.UUID;
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
			String Time = sDateFormat.format(new java.util.Date());
			String stime = Time +"-01 "+"00:00:00";
			String etime = Time+"-31 "+"23:59:59";
			dataUpLoading(stime,etime);
		}

	}
	
	private void dataUpLoading(String starttime,String endtime) {
		//showProgressDialog("数据加载中，请稍候...");
		// user this------
		ClassFullName = "com.beautyhealth.PersonHealth.BloodSugarMearsure.Entity.BloodSugarSearch";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		// myru.setIP(NetworkInfo.getServiceUrl());
		myru.setIP(null);
		myru.setMethod("BloodService", "queryBlood");
		Map requestCondition = new HashMap();
		String condition[] = { "StartTime", "EndTime", "UserID", "page", "rows" };
		String value[] = { starttime, endtime,UUID, "-1", "18" };
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);

		this.setRequestUtility(myru);
		this.requestData();
	}
		
	
	@Override
	public void updateView(){
		super.updateView();
		//dismissProgressDialog();
		if (dataResult != null) {
			// user this------
			DataResult realData = (DataResult) dataResult;
			testDataPressure.clear();
			Lines.clear();
			colors.clear();
			if (realData.getResultcode().equals("1")) {
				for (int i = 0; i < realData.getResult().size(); i++) {
					BloodSugarSearch msg = (BloodSugarSearch) realData.getResult().get(i);
					testDataPressure.add(msg);
				}
				set2DLines();
				setChart();
				initActivity();
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
	
	
	// 修改这里
		private void set2DLines() {
			if (testDataPressure.size() > 0) {
				CWChartLine2D heartValueLine = new CWChartLine2D();
				heartValueLine.LineTitle = "血糖值";
				heartValueLine.XUnit = "时间";
				heartValueLine.YUnit = "mmol/L";

				for (int i = 0; i < testDataPressure.size(); i++) {
					BloodSugarSearch ap = testDataPressure.get(i);
					CWChartPoint2D heartValuePoint = new CWChartPoint2D();
					heartValuePoint.AY = Double.valueOf(ap.Value);
					heartValuePoint.AX = i * 10;
					heartValuePoint.AXLabel = ap.MeasureTime;
					if (i == 0) {
							YAxistMax = Double.valueOf(ap.Value);
							YAxistMin = Double.valueOf(ap.Value);
					} else {
						setMaxValue(Double.valueOf(ap.Value));
						setMinValue(Double.valueOf(ap.Value));

					}
					heartValueLine.ChartPoints.add(heartValuePoint);
				}
				Lines.add(heartValueLine);
			}
		}
		// 修改这里
		private void setChart() {
			colors.add(Color.rgb(54, 141, 238));
			colors.add(Color.rgb(255, 165, 132));
			colors.add(Color.rgb(84, 206, 231));
			mspchar = new SplineChartView(this, Lines, colors); // 平滑曲线图
			mspchar.getChart().setTitle("");
			// 数据轴最大值
			mspchar.getChart().getDataAxis().setAxisMax(Math.floor(YAxistMax * 1.2));
			mspchar.getChart().getDataAxis().setAxisMin(Math.floor(YAxistMin * 0.8));
			// 数据轴刻度间隔
			mspchar.getChart().getDataAxis().setAxisSteps(20);
			mspchar.getChart().getPlotGrid().getHorizontalLinePaint()
					.setColor(Color.rgb(179, 147, 197));
		}

		private void setMinValue(double value) {
			if (YAxistMin > value) {
				YAxistMin = value;
			}
		}

		private void setMaxValue(double value) {
			if (YAxistMax < value) {
				YAxistMax = value;
			}
		}
		protected void onResume() {
			/**
			 * 设置为横屏
			 */
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			super.onResume();
		}

		private void initActivity() {
			FrameLayout content = new FrameLayout(this);

			// 缩放控件放置在FrameLayout的上层，用于放大缩小图表
			FrameLayout.LayoutParams frameParm = new FrameLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			frameParm.gravity = Gravity.BOTTOM | Gravity.RIGHT;

			// 图表显示范围在占屏幕大小的98%的区域内
			DisplayMetrics dm = getResources().getDisplayMetrics();
			int scrWidth = (int) (dm.widthPixels);
			int scrHeight = (int) ((dm.heightPixels)/8*7+5);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					scrWidth, scrHeight);

			// 居中显示
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			// 图表view放入布局中，也可直接将图表view放入Activity对应的xml文件中
			final RelativeLayout chartLayout = new RelativeLayout(this);

			chartLayout.addView(mspchar, layoutParams);

			// 增加控件
			((ViewGroup) content).addView(chartLayout);

			// 增加一个按钮用于打开时间选择框
			setTimeSelected(content);
			setContentView(content);
		}

		private void setTimeSelected(FrameLayout content) {
			LayoutInflater inflater = LayoutInflater.from(this);
			View convertView = inflater.inflate(R.layout.activity_bloodsugar_datashow, null);
			DisplayMetrics dms = getResources().getDisplayMetrics();
			RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams((int)dms.widthPixels,(int)((dms.heightPixels)/8*1)); // 设置按钮的宽度和高度
			btParams.leftMargin = 0; // 横坐标定位
			btParams.topMargin = 0; // 纵坐标定位
			((ViewGroup) content).addView(convertView, btParams);// 将按钮放入layout组件

		}
		
		public void LeftButtonClick(View view){
			finish();
		}
		
		public void RightButtonClick(View view){
			showDialog();
		}
		
		 private void showDialog(){ 	 
		    	View itemview = getLayoutInflater().inflate(R.layout.showseacherdialog, null);        
				startTime = (EditText) itemview.findViewById(R.id.pressureguardianship_start);
				endTime = (EditText)  itemview.findViewById(R.id.pressureguardianship_end);
				startTime.setInputType(InputType.TYPE_NULL);
				endTime.setInputType(InputType.TYPE_NULL);
				startTime.setOnClickListener(new OnClickListener() {
		    
					@Override
					public void onClick(View v) {
						Calendar c = Calendar.getInstance();
						new DatePickerDialog(BloodSugarDatashowActivity.this,
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
						new DatePickerDialog(BloodSugarDatashowActivity.this,
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
		        new AlertDialog.Builder(BloodSugarDatashowActivity.this)
		        .setView(itemview)
		        .setTitle("提示：输入条件")
		        .setPositiveButton("确定", new Dialog.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {	            	
		            	dataUpLoading(startTime.getText().toString(),endTime.getText().toString());
		            	return;
		            }
		        })
		        .setNegativeButton("取消", null)
		        .setCancelable(false) //触摸不消失
		        .show();
		        return;
		    }
	}
