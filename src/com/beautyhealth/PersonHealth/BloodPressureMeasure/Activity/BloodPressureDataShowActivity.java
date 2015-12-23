package com.beautyhealth.PersonHealth.BloodPressureMeasure.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.beautyhealth.PersonHealth.BloodPressureMeasure.Entity.BloodPressureSearch;
import com.beautyhealth.SafeGuardianship.BloodPressureGuardianship.Activity.BloodPressureGuardianshipActivity;
import com.beautyhealth.SafeGuardianship.BloodPressureGuardianship.Activity.Pressure;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class BloodPressureDataShowActivity extends DataRequestActivity {

	private SplineChartView mspchar;
	private List<BloodPressureSearch> testDataPressure= new ArrayList<BloodPressureSearch>();
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
		// user this------
		ClassFullName = "com.beautyhealth.PersonHealth.BloodPressureMeasure.Entity.BloodPressureSearch";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("PressureService", "queryPressure");
		Map requestCondition = new HashMap();
		String condition[] = { "StartTime", "EndTime", "UserID", "page", "rows" };
		String value[] = { starttime, endtime, UUID, "-1", "18" };
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);
		this.setRequestUtility(myru);
		this.requestData();
	}
	
	@Override
	public void updateView() {
		super.updateView();
		if (dataResult != null) {
			// user this------
			DataResult realData = (DataResult) dataResult;
			testDataPressure.clear();
			Lines.clear();
			colors.clear();
			if (realData.getResultcode().equals("1")) {
				for (int i = 0; i < realData.getResult().size(); i++) {
					BloodPressureSearch msg = (BloodPressureSearch) realData.getResult().get(i);
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
			heartValueLine.LineTitle = "心率";
			heartValueLine.XUnit = "时间";
			heartValueLine.YUnit = "次数";

			CWChartLine2D hightValueLine = new CWChartLine2D();
			hightValueLine.LineTitle = "高压";
			hightValueLine.XUnit = "时间";
			hightValueLine.YUnit = "mms/l";

			CWChartLine2D lowValueLine = new CWChartLine2D();
			lowValueLine.LineTitle = "低压";
			lowValueLine.XUnit = "时间";
			lowValueLine.YUnit = "mms/l";

			for (int i = 0; i < testDataPressure.size(); i++) {
				BloodPressureSearch ap = testDataPressure.get(i);
				CWChartPoint2D heartValuePoint = new CWChartPoint2D();
				heartValuePoint.AY = Double.valueOf(ap.HeartRate);
				heartValuePoint.AX = i * 10;
				heartValuePoint.AXLabel = ap.MeasureTime;

				CWChartPoint2D hightValuePoint = new CWChartPoint2D();
				hightValuePoint.AY = Double.valueOf(ap.HightPressure);
				hightValuePoint.AX = i * 10;
				hightValuePoint.AXLabel = ap.MeasureTime;

				CWChartPoint2D lowValuePoint = new CWChartPoint2D();
				lowValuePoint.AY = Double.valueOf(ap.LowPressure);
				lowValuePoint.AX = i * 10;
				lowValuePoint.AXLabel = ap.MeasureTime;

				if (i == 0) {
					if (Double.valueOf(ap.HightPressure)> Double.valueOf(ap.HeartRate) ){
						YAxistMax = Double.valueOf(ap.HightPressure);
					} else {
						YAxistMax = Double.valueOf(ap.HeartRate);
					}
					if (Double.valueOf(ap.LowPressure) < Double.valueOf(ap.HeartRate)) {
						YAxistMin = Double.valueOf(ap.LowPressure);
					} else {
						YAxistMin = Double.valueOf(ap.HeartRate);
					}

				} else {
					setMaxValue(Double.valueOf(ap.HeartRate));
					setMaxValue(Double.valueOf(ap.HightPressure));
					setMaxValue(Double.valueOf(ap.LowPressure));
					setMinValue(Double.valueOf(ap.HeartRate));
					setMinValue(Double.valueOf(ap.HightPressure));
					setMinValue(Double.valueOf(ap.LowPressure));
				}
				heartValueLine.ChartPoints.add(heartValuePoint);
				hightValueLine.ChartPoints.add(hightValuePoint);
				lowValueLine.ChartPoints.add(lowValuePoint);
			}
			Lines.add(heartValueLine);
			Lines.add(hightValueLine);
			Lines.add(lowValueLine);
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
		View convertView = inflater.inflate(R.layout.activity_bloodpressure_datashow, null);
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
					new DatePickerDialog(BloodPressureDataShowActivity.this,
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
					new DatePickerDialog(BloodPressureDataShowActivity.this,
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
	        new AlertDialog.Builder(BloodPressureDataShowActivity.this)
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
