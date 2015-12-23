package com.beautyhealth.PersonHealth.BloodSugarMearsure.Activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.CWViewGage;
import com.beautyhealth.Infrastructure.CWComponent.TabBarFragment;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.PersonHealth.BloodPressureMeasure.Activity.BloodPressureDataShowActivity;
import com.beautyhealth.PersonHealth.BloodPressureMeasure.Activity.BloodPressureMeasureActivity;
import com.beautyhealth.UserCenter.Activity.LoginActivity;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class BloodSugarMeasureActivity extends DataRequestActivity implements
		OnClickListener {
	private Button btn_dataShow, btn_operGuide, btn_dataSubmit;
	private ImageButton ib_befoedinner, ib_afterdinner, ib_earlymoring,
			ib_bedtime;
	private String bloodSugarValue = "0", measureTime = "xxxx-xx-xx yy:yy:yy",
			conclusion = "FF";
	private String UserID;
	private TabBarFragment tabBarFragment;
	private TextView dataValueShow;
	private Animation animation;
	private int point = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bloodsugar_measure);

		if (findViewById(R.id.fragment_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
			tabBarFragment = new TabBarFragment();
			tabBarFragment.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction()
					.add(R.id.fragment_container, tabBarFragment).commit();
		}
		initNavBar("血糖测量", "<返回", null);
		setAction(null);// user this------
		IsLocal = false;// user this------
		fetchUIFromLayout();
		setListener();
		initValue();
	}

	private void setListener() {
		ib_befoedinner.setOnClickListener((OnClickListener) this);
		ib_afterdinner.setOnClickListener((OnClickListener) this);
		ib_earlymoring.setOnClickListener((OnClickListener) this);
		ib_bedtime.setOnClickListener((OnClickListener) this);
		// btn_dataSubmit.setOnClickListener((OnClickListener) this);
		btn_dataShow.setOnClickListener((OnClickListener) this);
		btn_operGuide.setOnClickListener((OnClickListener) this);
	}

	private void fetchUIFromLayout() {
		ib_befoedinner = (ImageButton) findViewById(R.id.ib_befordinner);
		ib_afterdinner = (ImageButton) findViewById(R.id.ib_afterdinner);
		ib_earlymoring = (ImageButton) findViewById(R.id.ib_earlymorning);
		ib_bedtime = (ImageButton) findViewById(R.id.ib_bedtime);
		animation = AnimationUtils.loadAnimation(this, R.anim.btn_bs_anim);
		// btn_dataSubmit = (Button)
		// findViewById(R.id.btn_bloodSugar_dataSubmit);
		btn_dataShow = (Button) findViewById(R.id.btn_bloodSugar_dataShow);
		btn_operGuide = (Button) findViewById(R.id.btn_bloodSugar_operGuide);
		dataValueShow = (TextView) findViewById(R.id.bs_datashow);
	}

	private void initValue() {
		bloodSugarValue = "0";
		measureTime = "xxxx-xx-xx yy:yy:yy";
		conclusion = "FF";
		dataValueShow.setText("血糖值: 00 mmol/l");
	}

	@Override
	public void onClick(View v) {

		if (v == ib_befoedinner) {
			ib_befoedinner.startAnimation(animation);
			point = 1;
			dataMeasure();
		}
		if (v == ib_afterdinner) {
			ib_afterdinner.startAnimation(animation);
			point = 2;
			dataMeasure();
		}
		if (v == ib_earlymoring) {
			ib_earlymoring.startAnimation(animation);
			point = 3;
			dataMeasure();
		}

		if (v == ib_bedtime) {
			ib_bedtime.startAnimation(animation);
			point = 4;
			dataMeasure();
		}
		/*
		 * if (v == btn_dataSubmit) { btn_dataSubmit.startAnimation(animation);
		 * ISqlHelper iSqlHelper = new SqliteHelper(null,
		 * getApplicationContext()); List<Object> list = iSqlHelper.Query(
		 * "com.beautyhealth.UserCenter.Entity.UserMessage", null); if
		 * (list.size() > 0) { UserMessage userMessage = (UserMessage)
		 * list.get(0); UserID = userMessage.UserID; dataUpLoading(); } else {
		 * new AlertDialog.Builder(this) .setTitle("提示")
		 * .setMessage("您处于未登录状态,不能提交数据，请登录再试") .setPositiveButton("确定", new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * Intent intent = new Intent(); intent.putExtra("goto",
		 * BloodSugarMeasureActivity.class .getName()); intent.setClass(
		 * BloodSugarMeasureActivity.this, LoginActivity.class);
		 * startActivity(intent);
		 * 
		 * } }) .setNegativeButton("取消", new DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * return; } }).setCancelable(false).show(); }
		 * 
		 * }
		 */
		if (v == btn_dataShow) {
			IsLogin();
		}
		if (v == btn_operGuide) {
			Intent _intent = new Intent();
			_intent.setClass(BloodSugarMeasureActivity.this,
					BloodSugarOperGuideActivity.class);
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
			_intent.setClass(BloodSugarMeasureActivity.this,
					BloodSugarDatashowActivity.class);
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
											BloodSugarMeasureActivity.class
													.getName());
									intent.setClass(
											BloodSugarMeasureActivity.this,
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
		bloodSugarValue = launchDevice();
		conclusion = dataEvaluate(bloodSugarValue);
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		measureTime = sDateFormat.format(new java.util.Date());
		dismissProgressDialog();
		if ((bloodSugarValue == "0" || bloodSugarValue == null)
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
			dataValueShow.setText("血糖值: " + bloodSugarValue + " mmol/l");
			ISqlHelper iSqlHelper = new SqliteHelper(null,
					getApplicationContext());
			List<Object> list = iSqlHelper.Query(
					"com.beautyhealth.UserCenter.Entity.UserMessage", null);
			if (list.size() > 0) {
				UserMessage userMessage = (UserMessage) list.get(0);
				UserID = userMessage.UserID;
				dataUpLoading();
			} else {
				new AlertDialog.Builder(this).setTitle("提示")
						.setMessage("您处于离线状态,不能提交数据，请登录再试")
						.setPositiveButton("确定", null).setCancelable(false)
						.show();
			}
		}
	}

	private String launchDevice() {
		Random random1 = new Random(10);

		String ret = "62";
		return ret;

	}

	public String dataEvaluate(String Value) {
		String str_ret = null;
		if (Integer.valueOf(Value) > 40)
			str_ret = "严重";
		if (Integer.valueOf(Value) > 0 && Integer.valueOf(Value) < 40)
			str_ret = "正常";
		return str_ret;

	}

	private void dataUpLoading() {
		if ((bloodSugarValue == "0" || bloodSugarValue == null)
				|| (conclusion == "FF" || conclusion == null)) {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("测量数据异常，建议您重新测量。")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
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
			registeBroadcast();
			showProgressDialog("数据上传中，请稍候...");
			// user this------
			ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage";
			// user this------
			RequestUtility myru = new RequestUtility(this);
			myru.setIP(null);
			myru.setMethod("BloodService", "uploadBlood");
			Map requestCondition = new HashMap();
			String condition[] = { "Value", "MeasureTime", "Conclusion",
					"UserID" };
			String value[] = { bloodSugarValue, measureTime, conclusion, UserID };
			String strJson = JsonDecode.toJson(condition, value);
			requestCondition.put("json", strJson);
			myru.setParams(requestCondition);

			this.setRequestUtility(myru);
			this.requestData();
		}
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
									}).setMessage(msg.getTip())
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
									initValue();
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
