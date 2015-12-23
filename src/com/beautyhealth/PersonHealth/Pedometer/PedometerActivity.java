package com.beautyhealth.PersonHealth.Pedometer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.UserCenter.Activity.LoginActivity;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class PedometerActivity extends DataRequestActivity {

	private Circlebar circleBar;
	Calendar c = Calendar.getInstance();
	int myear;
	int mmonth;
	int mday;
	int mHour;
	int mMinute;
	int msecond;

	String month;
	String day;
	String Hour;
	String Minute;
	String second;

	private Thread thread;
	private int total_step;

	private Button btn_start;
	private Button btn_stop;
	private TextView tv_timer;// 运行时间
	private Button ib;

	private String Value = null;
	private String MeasureTime = null;
	private String TimeSpan = null;
	private String UserID;

	// 需要在handler里修改UI
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			total_step = StepDetector.CURRENT_SETP;
			System.out.println(total_step);
			circleBar.setProgress(total_step);
			tv_timer.setText(timeRun(StepDetector.timer));

		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedomter);
		initNavBar("计步器", "<返回", "上传");
		circleBar = (Circlebar) this.findViewById(R.id.progress_pedometer);
		btn_start = (Button) findViewById(R.id.start);
		btn_stop = (Button) findViewById(R.id.stop);
		tv_timer = (TextView) findViewById(R.id.usetime);
		ib = (Button) findViewById(R.id.ib);
		total_step = StepDetector.CURRENT_SETP;
		circleBar.setProgress(total_step);
		circleBar.startCustomAnimation();// 开启动画
		if (StepService.flag) {
			btn_start.setEnabled(false);
			btn_stop.setEnabled(true);
		}
		if (!StepService.flag & StepDetector.CURRENT_SETP > 0) {
			btn_start.setEnabled(true);
			// btn_stop.setText(R.string.cancel);
			// btn_stop.setBackground(getResources().getDrawable(R.drawable.btn1_bg));

			btn_stop.setBackground(getResources().getDrawable(
					R.drawable.reset_enable));
			btn_stop.setEnabled(true);
			// btn_stop.setEnabled(true);
			tv_timer.setText(timeRun(StepDetector.timer));
		}
		mThread();
		setAction(null);
		IsLocal = false;
		Log.e("sss", "重新启动" + StepDetector.starttime + "---"
				+ StepDetector.timer);
		ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						PedometerDataShowActivity.class);
				startActivity(intent);

			}
		});

	}

	@SuppressWarnings("deprecation")
	public void onClick(View view) {

		Intent service = new Intent(PedometerActivity.this, StepService.class);
		switch (view.getId()) {
		case R.id.start:
			startService(service);
			StepDetector.starttime = System.currentTimeMillis();
			Log.e("sss", "开始" + StepDetector.starttime + "----"
					+ StepDetector.timer);
			StepDetector.tempTime = StepDetector.timer;
			btn_start.setEnabled(false);
			btn_stop.setEnabled(true);
			break;
		case R.id.stop:
			stopService(service);
			if (StepService.flag && StepDetector.CURRENT_SETP > 0) {
				btn_stop.setBackground(getResources().getDrawable(
						R.drawable.reset_enable));
			} else {
				StepDetector.CURRENT_SETP = 0;
				StepDetector.starttime = 0;
				StepDetector.tempTime = StepDetector.timer = 0;

				circleBar.setProgress(0);
				tv_timer.setText(timeRun(StepDetector.timer));
				btn_stop.setBackground(getResources().getDrawable(
						R.drawable.btn1_bg));
				btn_stop.setEnabled(false);
			}

			btn_start.setEnabled(true);
			Log.e("sss", "暂停" + StepDetector.starttime + "----"
					+ StepDetector.timer);
			break;
		}
	}

	/**
	 * 右按钮监听函数
	 */
	public void onNavBarRightButtonClick(View view) {
		ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
		List<Object> list = iSqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
		if (list.size() > 0) {
			UserMessage userMessage = (UserMessage) list.get(0);
			UserID = userMessage.UserID;
			upload(view);
		} else {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("您处于未登录状态,不能提交数据，请登录再试")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent();
									intent.putExtra("goto",
											PedometerActivity.class.getName());
									intent.setClass(PedometerActivity.this,
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

	public void upload(View view) {
		if (StepService.flag) {
			new AlertDialog.Builder(PedometerActivity.this).setTitle("提示")
					.setMessage("请先暂停").setPositiveButton("确定", null).show();
		} else if (StepDetector.CURRENT_SETP == 0) {
			new AlertDialog.Builder(PedometerActivity.this).setTitle("提示")
					.setMessage("步数为0，无效信息").setPositiveButton("确定", null)
					.show();
		} else {
			showProgressDialog("数据上传中，请稍候...");
			RequestUtility UploadPedometer = new RequestUtility(this);
			ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage";
			Value = StepDetector.CURRENT_SETP + "";
			MeasureTime = getFormatTime();
			TimeSpan = getTimeSpan(StepDetector.timer);
			UploadPedometer.setIP(null);
			UploadPedometer.setMethod("WalkActionService", "uploadWalkAction");
			String condition[] = { "Value", "MeasureTime", "TimeSpan", "UserID" };
			String value[] = { Value, MeasureTime, TimeSpan, UserID };
			String strJson = JsonDecode.toJson(condition, value);
			Map requestCondition = new HashMap();
			requestCondition.put("json", strJson);
			UploadPedometer.setParams(requestCondition);
			this.setRequestUtility(UploadPedometer);
			this.requestData();

		}

	}

	@Override
	public void updateView() {
		super.updateView();
		dismissProgressDialog();
		if (dataResult != null) {
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
				} else {
					new AlertDialog.Builder(this)
							.setTitle("提示")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											StepDetector.CURRENT_SETP = 0;
											circleBar.setProgress(0);
											btn_stop.setText("暂停");
											btn_stop.setEnabled(false);
											StepDetector.tempTime = StepDetector.timer = 0;
											tv_timer.setText(timeRun(StepDetector.timer));

											return;
										}
									}).setMessage(msg.getTip())
							.setCancelable(false).show();
					return;
				}
			} else {
				new AlertDialog.Builder(this)
						.setTitle("错误")
						.setMessage("暂无数据")
						.setPositiveButton("确定",
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
			new AlertDialog.Builder(PedometerActivity.this).setTitle("错误")
					.setMessage("数据上传失败,点击确定重新上传")
					.setPositiveButton("确定", null).show();
			return;
		}

	}

	public String getFormatTime() {
		c.setTimeInMillis(System.currentTimeMillis());
		myear = c.get(Calendar.YEAR);
		mmonth = c.get(Calendar.MONTH) + 1;
		if (mmonth < 10) {
			month = "0" + mmonth + "";
		} else {
			month = mmonth + "";
		}
		mday = c.get(Calendar.DATE);
		if (mday < 10) {
			day = "0" + mday + "";
		} else {
			day = mday + "";
		}
		mHour = c.get(Calendar.HOUR_OF_DAY);
		if (mHour < 10) {
			Hour = "0" + mHour + "";
		} else {
			Hour = mHour + "";
		}
		mMinute = c.get(Calendar.MINUTE);
		if (mMinute < 10) {
			Minute = "0" + mMinute + "";
		} else {
			Minute = mMinute + "";
		}
		msecond = c.get(Calendar.SECOND);
		if (msecond < 10) {
			second = "0" + msecond + "";
		} else {
			second = msecond + "";
		}
		String time = myear + "-" + month + "-" + day + " " + Hour + ":"
				+ Minute + ":" + second;
		return time;
	}

	private String timeRun(long time) {
		time = time / 1000;
		long second = time % 60;
		long minute = (time % 3600) / 60;
		long hour = time / 3600;
		// 毫秒秒显示两位
		// String strMillisecond = "" + (millisecond / 10);
		// 秒显示两位
		Log.e("jjj", second + "%%%" + minute + "&&&" + hour + "");
		String strSecond = ("00" + second)
				.substring(("00" + second).length() - 2);
		// 分显示两位
		String strMinute = ("00" + minute)
				.substring(("00" + minute).length() - 2);
		// 时显示两位
		String strHour = ("00" + hour).substring(("00" + hour).length() - 2);

		return strHour + ":" + strMinute + ":" + strSecond;
	}

	private String getTimeSpan(long time) {
		time = time / 1000;

		long minute = (time % 3600) / 60;
		long hour = time / 3600;
		int TimeSpan = (int) (minute + hour * 60);
		String timespan = Integer.toString(TimeSpan);
		;
		return timespan;
	}

	private void mThread() {
		if (thread == null) {
			thread = new Thread(new Runnable() {
				public void run() {
					while (true) {

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (StepService.flag) {

							Message msg = new Message();

							if (StepDetector.starttime != System
									.currentTimeMillis()) {

								StepDetector.timer = StepDetector.tempTime
										+ System.currentTimeMillis()
										- StepDetector.starttime;
								handler.sendMessage(msg);

							}

						}
					}
				}

			});
			thread.start();
		}
	}

}
