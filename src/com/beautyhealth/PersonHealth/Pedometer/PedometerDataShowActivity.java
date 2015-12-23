package com.beautyhealth.PersonHealth.Pedometer;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.PersonHealth.BloodPressureMeasure.Activity.BloodPressureDataShowActivity;
import com.beautyhealth.SafeGuardianship.ActionGuardianship.Data;
import com.beautyhealth.SafeGuardianship.ActionGuardianship.SafeActionActivity;
import com.beautyhealth.SafeGuardianship.ActionGuardianship.SafeActionLine;
import com.beautyhealth.SafeGuardianship.ActionGuardianship.SafeActionPoint;
import com.beautyhealth.SafeGuardianship.ActionGuardianship.SafeActionView;
import com.beautyhealth.UserCenter.Entity.BindingMessage;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class PedometerDataShowActivity extends DataRequestActivity  {

	private SafeActionView mspchar;
	private List<Data> datas = new ArrayList<Data>();
	private List<SafeActionLine> Lines = new ArrayList<SafeActionLine>();
	private List<Integer> colors = new ArrayList<Integer>();
	private double YAxistMax = 0;
	private double YAxistMin = 0;

	private String UserID;
	private EditText startTime, endTime;
	private Spinner spUserName;

	private String[] strsUserName = null;
	private String[] strsUUID = null;
	private ArrayAdapter<String> adapter;

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

	private void set2DLines() {
		if (datas.size() > 0) {
			SafeActionLine heartValueLine = new SafeActionLine();
			heartValueLine.LineTitle = "步数";
			heartValueLine.XUnit = "时间";
			heartValueLine.YUnit = "步";
			heartValueLine.TimeSpan = "时长";

			for (int i = 0; i < datas.size(); i++) {
				Data ap = datas.get(i);
				SafeActionPoint heartValuePoint = new SafeActionPoint();
				heartValuePoint.AY = Double.valueOf(ap.Value);
				heartValuePoint.AX = i * 10;
				heartValuePoint.AXLabel = ap.MeasureTime;
				heartValuePoint.TimeSpan = ap.TimeSpan;

				if (i == 0) {
					YAxistMax = Double.valueOf(ap.Value);
					YAxistMin = Double.valueOf(ap.Value);
				} else {
					setMaxValue(Double.valueOf(ap.Value));
					setMinValue(Double.valueOf(ap.Value));

				}
				heartValueLine.SafeActionPoint.add(heartValuePoint);
			}
			Lines.add(heartValueLine);
		}
	}

	// 修改这里
	private void setChart() {
		colors.add(Color.rgb(54, 141, 238));
		colors.add(Color.rgb(255, 165, 132));
		colors.add(Color.rgb(84, 206, 231));
		mspchar = new SafeActionView(this, Lines, colors); // 平滑曲线图
		mspchar.getChart().setTitle("");
		// 数据轴最大值
		mspchar.getChart().getDataAxis()
				.setAxisMax(Math.floor(YAxistMax * 1.4));
		mspchar.getChart().getDataAxis()
				.setAxisMin(Math.floor(YAxistMin * 0.6));
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
		int scrHeight = (int) ((dm.heightPixels) / 8 * 7+5);
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
		View convertView = inflater.inflate(
				R.layout.android_safeaction, null);
		DisplayMetrics dms = getResources().getDisplayMetrics();
		RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams(
				(int) dms.widthPixels, (int) ((dms.heightPixels) / 8 * 1)); // 设置按钮的宽度和高度
		btParams.leftMargin = 0; // 横坐标定位
		btParams.topMargin = 0; // 纵坐标定位
		((ViewGroup) content).addView(convertView, btParams);// 将按钮放入layout组件

	}

	public void LeftButtonClick(View view) {
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)// 主要是对这个函数的复写
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK)
				&& (event.getAction() == KeyEvent.ACTION_DOWN)) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	public void RightButtonClick(View view) {
		showDialog();
	}

	private void InitData() {
		ISqlHelper isqlHelper = new SqliteHelper(null, getApplicationContext());
		List<Object> ls = isqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
		if (ls.size() > 0) {
			UserMessage userMessage=(UserMessage) ls.get(0);
			UserID = userMessage.UUID;
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
			String Time = sDateFormat.format(new java.util.Date());
			String stime = Time +"-01 "+"00:00:00";
			String etime = Time+"-31 "+"23:59:59";		
			SeachStep(stime, etime, UserID);
		} 
	}

	private int dp2px(int value) {
		return DisplayUtil.dip2px(getApplicationContext(), value);
	}

	public void SeachStep(String StartTime, String EndTime, String UUID) {
		
		RequestUtility SearchStep = new RequestUtility(this);
		ClassFullName = "com.beautyhealth.SafeGuardianship.ActionGuardianship.Data";
		SearchStep.setIP(null);
		SearchStep.setMethod("WalkActionService", "queryWalkAction");
		String condition[] = { "StartTime", "EndTime", "UserID", "page", "rows" };
		String value[] = { StartTime, EndTime, UUID, "-1", "18" };
		String strJson = JsonDecode.toJson(condition, value);
		Map requestCondition = new HashMap();
		requestCondition.put("json", strJson);
		SearchStep.setParams(requestCondition);
		this.setRequestUtility(SearchStep);
		this.requestData();
	}

	@Override
	public void updateView() {
		super.updateView();
		if (dataResult != null) {
			// user this------
			DataResult realData = (DataResult) dataResult;
			datas.clear();
			Lines.clear();
			colors.clear();
			if (realData.getResultcode().equals("1")) {
				for (int i = 0; i < realData.getResult().size(); i++) {
					Data msg = (Data) realData.getResult().get(i);
					datas.add(msg);
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

	public void search(View v) {
		showDialog();
		return;
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
				new DatePickerDialog(PedometerDataShowActivity.this,
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
				new DatePickerDialog(PedometerDataShowActivity.this,
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
        new AlertDialog.Builder(PedometerDataShowActivity.this)
        .setView(itemview)
        .setTitle("提示：输入条件")
        .setPositiveButton("确定", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {	            	
            	SeachStep(startTime.getText().toString(),endTime.getText().toString(),UserID);
            	return;
            }
        })
        .setNegativeButton("取消", null)
        .setCancelable(false) //触摸不消失
        .show();
        return;
    }

}
