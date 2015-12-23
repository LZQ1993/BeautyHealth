package com.beautyhealth.PersonHealth.BloodPressureMeasure.Activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.CWViewGage;
import com.beautyhealth.Infrastructure.CWComponent.GaugeChart01View;
import com.beautyhealth.Infrastructure.CWComponent.TabBarFragment;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.TypeConverHelper;
import com.beautyhealth.UserCenter.Activity.LoginActivity;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class BloodPressureMeasureActivity extends DataRequestActivity implements
		OnClickListener {
	private Button btn_dataSubmit, btn_measure, btn_dataShow, btn_operGuide;
	private String hightPressure = "0", lowPressure = "0", heartRate = "0",
			measureTime = "xxxx-xx-xx yy:yy:yy", conclusion = "FF";
	private TextView lowpressureValue, highpressureValue, heartrateValue,
			timeshow;
	private String UserID;
	private TabBarFragment tabBarFragment;
	private GaugeChart01View mGaChart = null;

	private CWViewGage bpInstrument;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bloodpressure_measure);
		initNavBar("血压测量", "<返回", null);
		setAction(null);// user this------
		IsLocal = false;// user this------

		fetchUIFromLayout();
		setListener();
		initValue();

	}

	private void initValue() {
		hightPressure = "0";
		lowPressure = "0";
		heartRate = "0";
		lowpressureValue.setText("00");
		highpressureValue.setText("00");
		heartrateValue.setText("00");
		measureTime = "xxxx-xx-xx yy:yy:yy";
		conclusion = "FF";

		bpInstrument.setRotate(-60, 500, 500);
		bpInstrument.invalidate();
	}

	private void setListener() {
		btn_measure.setOnClickListener((OnClickListener) this);
		btn_dataShow.setOnClickListener((OnClickListener) this);
		btn_operGuide.setOnClickListener((OnClickListener) this);
	}

	private void fetchUIFromLayout() {
		btn_measure = (Button) findViewById(R.id.btn_bloodPressure_measure);
		btn_dataShow = (Button) findViewById(R.id.btn_bloodPressure_dataShow);
		btn_operGuide = (Button) findViewById(R.id.btn_bloodPressure_operGuide);
		lowpressureValue = (TextView) findViewById(R.id.tv_lowpressure);
		highpressureValue = (TextView) findViewById(R.id.tv_highpressure);
		heartrateValue = (TextView) findViewById(R.id.tv_heartrate);
		timeshow = (TextView) findViewById(R.id.bp_timeshow);
		timeshow.setText(TypeConverHelper.getDateAndWeek());

		bpInstrument = (CWViewGage) findViewById(R.id.cWViewGage1);

		bpInstrument.addBitmap(R.drawable.bloodpressureinstrument,
				R.drawable.bloodpressurearrow);
		bpInstrument.setdrawLocation(0, 0);
		bpInstrument.setScaleRate(0.9);
		bpInstrument.setSecondPicOffset(160, 15);

	}

	private void setHandler() {

		final Handler myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x1233) {
					bpInstrument.invalidate();
				}
			}
		};
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {

				bpInstrument.setRotate((double) (Math.random() * -150 + 60),
						266, 282);
				myHandler.sendEmptyMessage(0x1233);

			}
		}, 0, 1000);

	}

	public void onClick(View v) {
		if (v == btn_measure) {
			dataMeasure();
		}
		if (v == btn_dataShow) {
			IsLogin();
		}
		if (v == btn_operGuide) {
			Intent _intent = new Intent();
			_intent.setClass(BloodPressureMeasureActivity.this,
					BloodPressureOperGuideActivity.class);
			startActivity(_intent);
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
		}

	}

	private void IsLogin() {
		ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
		List<Object> list = iSqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
		if (list.size() > 0) {
			Intent _intent = new Intent();
			_intent.setClass(BloodPressureMeasureActivity.this,
					BloodPressureDataShowActivity.class);
			startActivity(_intent);
		} else {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("您处于未登录状态,不能查询数据，请登录再试")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent();
									intent.putExtra("goto",
											BloodPressureMeasureActivity.class
													.getName());
									intent.setClass(
											BloodPressureMeasureActivity.this,
											LoginActivity.class);
									startActivity(intent);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									return;
								}
							}).setCancelable(false).show();

		}
	}

	private void dataMeasure() {
		showProgressDialog("测量中，请稍候...");
		String[] bloodPressureData = launchDevice();
		hightPressure = bloodPressureData[0];
		lowPressure = bloodPressureData[1];
		heartRate = bloodPressureData[2];
		conclusion = dataEvaluate(hightPressure, lowPressure, heartRate);
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		measureTime = sDateFormat.format(new java.util.Date());
		dismissProgressDialog();
		if ((hightPressure == "0" || hightPressure == null)
				|| (lowPressure == "0" || lowPressure == null)
				|| (heartRate == "0" || heartRate == null)
				|| (conclusion == "FF" || conclusion == null)) {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("测量数据异常，建议您重新测量。")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									initValue();
									dataMeasure();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									initValue();
									return;
								}
							}).setCancelable(false).show();
		} else {
			lowpressureValue.setText(lowPressure);
			highpressureValue.setText(hightPressure);
			heartrateValue.setText(heartRate);
			ISqlHelper iSqlHelper = new SqliteHelper(null,
					getApplicationContext());
			List<Object> list = iSqlHelper.Query(
					"com.beautyhealth.UserCenter.Entity.UserMessage", null);
			if (list.size() > 0) {
				UserMessage userMessage = (UserMessage) list.get(0);
				UserID = userMessage.UserID;
				dataUpLoading();
			} else {
				new AlertDialog.Builder(this)
						.setTitle("提示")
						.setMessage("您处于离线状态,不能提交数据，请登录再试")
						.setPositiveButton("确定",null)
						.setCancelable(false).show();
			}
		}
	}

	private String[] launchDevice() {
		String[] ret = { "100", "80", "75" };
		return ret;

	}

	public String dataEvaluate(String hightPressure, String lowPressure,
			String heartRate) {
		int data = Integer.valueOf(hightPressure)
				- Integer.valueOf(lowPressure);
		String str_ret = null;
		if (data > 60) {
			str_ret = "严重";

		}
		if (data > 40 && data < 60) {
			str_ret = "一般";
		}

		if (data > 0 && data < 40) {
			str_ret = "正常";

		}

		return str_ret;

	}

	private void dataUpLoading() {
		registeBroadcast();
		showProgressDialog("血压测量完毕,数据上传中，请稍候...");
		// user this------
		ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("PressureService", "uploadPressure");
		Map requestCondition = new HashMap();

		String condition[] = { "HightPressure", "LowPressure", "HeartRate",
				"MeasureTime", "Conclusion", "UserID" };
		String value[] = { hightPressure, lowPressure, heartRate, measureTime,
				conclusion, UserID };

		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);

		this.setRequestUtility(myru);
		this.requestData();
	}

	@Override
	public void updateView() {
		super.updateView();
		dismissProgressDialog();
		if (dataResult != null) {
			// user this------
			DataResult realData = (DataResult) dataResult;
			if (realData.getResultcode().equals("1")) {
				ReturnTrasactionMessage msg = (ReturnTrasactionMessage) realData
						.getResult().get(0);
				if (msg.getResult().equals("0")) {
					new AlertDialog.Builder(this)
							.setTitle("失败")
							.setMessage(msg.getTip() + "请重新上传。")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dataUpLoading();
										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											return;
										}
									}).setCancelable(false).show();
					return;
				} else if (msg.getResult().equals("1")) {
					new AlertDialog.Builder(this)
							.setTitle("提示")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											return;
										}
									})
								.setMessage(msg.getTip())
							.setCancelable(false).show();
					return;
				}
			} else {
				new AlertDialog.Builder(this)
						.setTitle("错误")
						.setMessage("数据上传失败,点击确定重新上传。")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dataUpLoading();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}
								}).setCancelable(false).show();
				return;
			}
		} else {
			new AlertDialog.Builder(this)
					.setTitle("错误")
					.setMessage("数据上传异常,点击确定重新上传。")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dataUpLoading();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).setCancelable(false).show();
			return;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}


}
