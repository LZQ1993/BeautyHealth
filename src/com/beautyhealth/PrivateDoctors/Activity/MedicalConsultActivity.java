package com.beautyhealth.PrivateDoctors.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.TabBarFragment;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.HttpUtil;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.PrivateDoctors.Assistant.CommentAdapter;
import com.beautyhealth.PrivateDoctors.Assistant.CommentReplyAdapter;
import com.beautyhealth.PrivateDoctors.Assistant.IssueSort;
import com.beautyhealth.PrivateDoctors.Assistant.QuestionDecode;
import com.beautyhealth.PrivateDoctors.Assistant.ReplySort;
import com.beautyhealth.PrivateDoctors.Entity.MedicalIssue;
import com.beautyhealth.PrivateDoctors.Entity.Reply;
import com.beautyhealth.UserCenter.Activity.LoginActivity;
import com.beautyhealth.UserCenter.Entity.UserMessage;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MedicalConsultActivity extends DataRequestActivity implements
		OnClickListener {
	private ListView lv_Issue;

	private List<MedicalIssue> doctorInfo;
	private List<List<Reply>> replyInfo;
	private List<Reply> lsreply;
	private CommentAdapter commentAdapter;
	private CommentReplyAdapter commentReplyAdapter;
	private String strbtnkey, jsonstr;
	private String UID;
	private EditText startTime, endTime;
	private String stime, etime;
	private View headerView;
	private Button btn_addIssue, btn_myIssue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pd_medicalconsult);
		setAction(null);// user this------
		IsLocal = false;// user this------
		
		initNavBar("医疗咨询", "<返回", "≡");
		initList();
		strbtnkey = getIntent().getStringExtra("btnkey");
		jsonstr = getIntent().getStringExtra("data");
		initCommentData(jsonstr);
		fetchUIFromLayout();
		setListener();

	}

	private void initCommentData(String str) {
		doctorInfo.clear();
		replyInfo.clear();
		QuestionDecode questionDecode = new QuestionDecode();
		DataResult dataResult = (DataResult) questionDecode.fromJson(str);
		if (dataResult.equals("0")) {
			new AlertDialog.Builder(this)
					.setTitle("错误")
					.setMessage("暂无数据")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).setCancelable(false).show();
		} else {
			for (int i = 0; i < dataResult.getResult().size(); i++) {
				MedicalIssue MmedicalIssue = (MedicalIssue) dataResult
						.getResult().get(i);
				lsreply = new ArrayList<Reply>();
				for (int j = 0; j < MmedicalIssue.getReply().size(); j++) {
					Reply reply = (Reply) MmedicalIssue.getReply().get(j);
					lsreply.add(reply);
				}
				replyInfo.add(lsreply);
				doctorInfo.add(MmedicalIssue);
			}

		}

	}

	private void fetchUIFromLayout() {
		lv_Issue = (ListView) findViewById(R.id.lv_pd_Issue);
		headerView = View.inflate(this, R.layout.mc_listview_header, null);
		lv_Issue.addHeaderView(headerView);//ListView条目中的悬浮部分 添加到头部
		btn_addIssue = (Button) findViewById(R.id.btn_pd_addIssue);
		btn_myIssue = (Button) findViewById(R.id.btn_pd_myIssue);
	}

	private void initList() {
		// TODO Auto-generated method stub
		doctorInfo = new ArrayList<MedicalIssue>();
		replyInfo = new ArrayList<List<Reply>>();
	}

	private void setListener() {
		btn_addIssue.setOnClickListener((OnClickListener) this);
		btn_myIssue.setOnClickListener((OnClickListener) this);
		commentReplyAdapter = null;
		commentAdapter = new CommentAdapter(this, doctorInfo, replyInfo,
				myListener, commentReplyAdapter);
		lv_Issue.setAdapter(commentAdapter);
	}

	private OnClickListener myListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int position = 0, parentposition = 0;
			String pos = (String) v.getTag();
			String a[] = pos.split(":");
			if (a.length == 1) {
				position = Integer.valueOf(a[0]);
			} else {
				position = Integer.valueOf(a[0]);
				parentposition = Integer.valueOf(a[1]);
			}
			Intent _intent = new Intent();
			if (v.getId() == R.id.btn_reply_pic) {
				_intent.putExtra("QAID",
						replyInfo.get(parentposition)
								.get(position).auto_id);
				_intent.putExtra("TypeID", "1");
				_intent.setClass(getApplicationContext(),
						PDMCpicShowActivity.class);
				startActivity(_intent);
			}
			if (v.getId() == R.id.btn_issue_pic) {
				_intent.putExtra(
						"QAID",
						doctorInfo.get(position).auto_id);			
				_intent.putExtra("TypeID", "0");
				_intent.setClass(getApplicationContext(),
						PDMCpicShowActivity.class);
				startActivity(_intent);
			}

		}
	};

	/**
	 * 右按钮监听函数
	 */
	public void onNavBarRightButtonClick(View view) {

		showDialog();

	}

	public void isLogin() {
		ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
		List<Object> list = iSqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
		if (list.size() > 0) {
			UserMessage userMessage = (UserMessage) list.get(0);
			UID = userMessage.UserID;
		} else {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("您处于未登录状态,请登录再试")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent();
									intent.putExtra("goto",
											MedicalConsultActivity.class
													.getName());
									intent.setClass(
											MedicalConsultActivity.this,
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM");
			String Time = sDateFormat.format(new java.util.Date());
			stime = Time+"-01" + " " + "00:00:00";
			etime = Time+"-31" + " " + "23:59:59";
			dataUpLoading(stime, etime, "999");
		}
	}

	private void showDialog() {
		View itemview = getLayoutInflater().inflate(R.layout.showseacherdialog,
				null);
		startTime = (EditText) itemview
				.findViewById(R.id.pressureguardianship_start);
		endTime = (EditText) itemview
				.findViewById(R.id.pressureguardianship_end);
		startTime.setInputType(InputType.TYPE_NULL);
		endTime.setInputType(InputType.TYPE_NULL);
		startTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				new DatePickerDialog(MedicalConsultActivity.this,
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
				new DatePickerDialog(MedicalConsultActivity.this,
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
		// 对话框
		new AlertDialog.Builder(MedicalConsultActivity.this).setView(itemview)
				.setTitle("提示：输入条件")
				.setPositiveButton("确定", new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						stime = startTime.getText().toString();
						etime = endTime.getText().toString();
						dataUpLoading(stime, etime, "999");
					}
				}).setNegativeButton("取消", null).setCancelable(false) // 触摸不消失
				.show();
		return;
	}

	private void dataUpLoading( String starttime,
			String endtime, String UserID) {
		showProgressDialog("数据加载中，请稍候...");	
		String url = NetworkSetInfo.getServiceUrl()
				+ "/PrivateDoctor/queryQuestion";
		RequestParams params = new RequestParams();
		String condition[] = { "StartTime", "EndTime", "UserID", "page",
				"rows", "DoctorType" };
		String value[] = { starttime, endtime, UserID, "-1", "18", strbtnkey };
		String strJson = JsonDecode.toJson(condition, value);
		params.put("json", strJson);
		HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable error) {
				dismissProgressDialog();
				new AlertDialog.Builder(MedicalConsultActivity.this)
						.setTitle("错误")
						.setMessage("网络访问失败，请检查网络！" + error.toString())
						.setPositiveButton("确定", null).show();
				return;
			}

			@Override
			public void onSuccess(String content) {
				dismissProgressDialog();
				Message message = new Message();
				message.obj = content;
				handler.sendMessage(message);

			}

		});
	}
	

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			initCommentData((String) msg.obj);
			commentAdapter = new CommentAdapter(getApplicationContext(),
					doctorInfo, replyInfo, myListener, commentReplyAdapter);
			lv_Issue.setAdapter(commentAdapter);
		}
	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_pd_addIssue) {
			Intent _intent = new Intent();
			_intent.setClass(getApplicationContext(), AddIssueActivity.class);
			_intent.putExtra("btnkey", strbtnkey);
			startActivityForResult(_intent, 1);
		}
		if (v.getId() == R.id.btn_pd_myIssue) {
			ISqlHelper iSqlHelper = new SqliteHelper(null,
					getApplicationContext());
			List<Object> list = iSqlHelper.Query(
					"com.beautyhealth.UserCenter.Entity.UserMessage", null);
			if (list.size() > 0) {
				UserMessage userMessage = (UserMessage) list.get(0);
				UID = userMessage.UserID;
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM");
				String Time = sDateFormat.format(new java.util.Date());
				stime = Time+"-01" + " " + "00:00:00";
				etime = Time+"-31" + " " + "23:59:59";
				dataUpLoading(stime, etime, UID);
			} else {
				new AlertDialog.Builder(this)
						.setTitle("提示")
						.setMessage("您处于未登录状态,请登录再试")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent();
										intent.putExtra("goto",
												MedicalConsultActivity.class
														.getName());
										intent.setClass(
												MedicalConsultActivity.this,
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
	}
}
