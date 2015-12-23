package com.beautyhealth.PrivateDoctors.Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.PrivateDoctors.Util.Bimp;
import com.beautyhealth.PrivateDoctors.Util.ImageItem;
import com.beautyhealth.PrivateDoctors.Util.PublicWay;
import com.beautyhealth.PrivateDoctors.Util.Res;
import com.beautyhealth.UserCenter.Activity.LoginActivity;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class AddIssueActivity extends DataRequestActivity {
	private GridView noScrollgridview;
	private GridAdapter adapter;
	private View parentView;
	private TextView tvsend;
	private EditText etcontent;
	// PopupWindow这个类用来实现一个弹出框，可以使用任意布局的View作为其内容，这个弹出框是悬浮在当前activity之上的。
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static Bitmap bimap;

	private String path;
	private Uri uri;
	private File file;
	private String name;
	private String strbtnkey;
	private RadioGroup rg_contenttype;
	private String IsShared;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Res.init(this);
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);
		PublicWay.activityList.add(this);
		parentView = getLayoutInflater().inflate(R.layout.activity_pd_addissue,
				null);

		rg_contenttype = (RadioGroup) parentView
				.findViewById(R.id.rg_pd_contenttype);
		etcontent = (EditText) parentView.findViewById(R.id.content);
		// 改变默认选项
		rg_contenttype.check(R.id.rg_pd_contentissue_public);
		// 获取默认被被选中值
		if (rg_contenttype.getCheckedRadioButtonId() == R.id.rg_pd_contentissue_public) {
			IsShared = "1";
		} else {
			IsShared = "0";
		}
		strbtnkey = getIntent().getStringExtra("btnkey");
		setContentView(parentView);
		Init();
		setAction(null);// user this------
		IsLocal = false;// user this------

		// 注册事件
		rg_contenttype
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (rg_contenttype.getCheckedRadioButtonId() == R.id.rg_pd_contentissue_public) {
							IsShared = "1";
						} else {
							IsShared = "0";
						}
					}
				});
	}

	public void Init() {

		pop = new PopupWindow(AddIssueActivity.this);

		View view = getLayoutInflater().inflate(R.layout.item_popupwindows,
				null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		// 背景设置 无阴影
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);// 获取焦点，设置当前窗体为操作窗体
		pop.setOutsideTouchable(true); // 外围点击dismiss
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);

		// 点击窗体
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		// 点击拍照
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		// 点击相册
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AddIssueActivity.this,
						AlbumActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_translate_in,
						R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		// 点击退出
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

		noScrollgridview = (GridView) findViewById(R.id.Scrollgridview);
		// 选中背景
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(parentView.getWindowToken(), 0); // 强制隐藏键盘
					ll_popup.startAnimation(AnimationUtils
							.loadAnimation(AddIssueActivity.this,
									R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {
					Intent intent = new Intent(AddIssueActivity.this,
							GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});

	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == 9) {
				return 9;
			}
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position)
						.getBitmap());
			}
			return convertView;

		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.tempSelectBitmap.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	private static final int TAKE_PICTURE = 0x000001;

	public void photo() {
		Intent openCameraIntent = new Intent();
		openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

		name = "IMG_"
				+ DateFormat.format("yyyyMMdd_hhmmss",
						Calendar.getInstance(Locale.CHINA)) + ".jpg";
		path = "/sdcard/DCIM/Camera/" + name;
		file = new File(path);
		uri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 设置拍照的照片存储在哪个位置。

		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			Toast.makeText(getApplicationContext(),
					"SD Card Error! Please check it!", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(uri), null, options);
					options.inSampleSize = 4;
					options.inJustDecodeBounds = false;
					Bitmap photo = BitmapFactory.decodeStream(
							getContentResolver().openInputStream(uri), null,
							options);
					ImageItem takePhoto = new ImageItem();
					takePhoto.setBitmap(photo);
					takePhoto.setImagePath(path);
					takePhoto.setName(name);
					Bimp.tempSelectBitmap.add(takePhoto);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}

	/*
	 * public boolean onKeyDown(int keyCode, KeyEvent event) { if (keyCode ==
	 * KeyEvent.KEYCODE_BACK) { for (int i = 0; i <
	 * PublicWay.activityList.size(); i++) { if (null !=
	 * PublicWay.activityList.get(i)) { PublicWay.activityList.get(i).finish();
	 * } } System.exit(0); } return true; }
	 */

	/**
	 * 右按钮监听函数
	 */
	public void RightButtonClick(View view) {
		if (etcontent.getText().toString().equals("")) {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("请填写您的病情简例!")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).setCancelable(false).show();
			return;
		} else {
			isLogin();
		}
	}

	public void isLogin() {
		ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
		List<Object> list = iSqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
		if (list.size() > 0) {
			UserMessage userMessage = (UserMessage) list.get(0);
			String UserID = userMessage.UserID;
			uploadimage(UserID);
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
											AddIssueActivity.class.getName());
									intent.setClass(AddIssueActivity.this,
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
								};
							}).setCancelable(false).show();
		}

	}

	public void uploadimage(String UserID) {
		registeBroadcast();
		showProgressDialog("数据上传中，请稍候...");
		ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage";
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("PrivateDoctor", "uploadQuestion");
		Map requestCondition = new HashMap();
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String QuestionTime = sDateFormat.format(new java.util.Date());
		String condition[] = { "UserID", "QuestionContent", "QuestionTime",
				"IsShared", "DoctorType" };
		String value[] = { UserID, etcontent.getText().toString(),
				QuestionTime, IsShared, strbtnkey };
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			file = new File(Bimp.tempSelectBitmap.get(i).getImagePath());
			requestCondition.put("image" + i, file);
		}
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
			if (realData.getResult().size() > 0) {
				ReturnTrasactionMessage auser = (ReturnTrasactionMessage) realData
						.getResult().get(0);
				Toast.makeText(getApplicationContext(),
						"result:" + auser.result + " tip:" + auser.tip,
						Toast.LENGTH_SHORT).show();
				etcontent.setText("");
				Bimp.tempSelectBitmap.clear();
				Bimp.max = 0;
				adapter.update();
			}
		} else {
			Toast.makeText(getApplicationContext(), "加载失败",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)// 主要是对这个函数的复写
	{ // TODO Auto-generated method stub

		if ((keyCode == KeyEvent.KEYCODE_BACK)
				&& (event.getAction() == KeyEvent.ACTION_DOWN)) {
			etcontent.setText("");
			rg_contenttype.check(R.id.rg_pd_contentissue_public);
			Bimp.tempSelectBitmap.clear();
			Bimp.max = 0;
			adapter.update();
			Intent intent = new Intent();
			AddIssueActivity.this.setResult(1, intent);
			AddIssueActivity.this.finish();
			return true;

		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 左按钮监听函数
	 */
	public void LeftButtonClick(View view) {
		etcontent.setText("");
		rg_contenttype.check(R.id.rg_pd_contentissue_public);
		Bimp.tempSelectBitmap.clear();
		Bimp.max = 0;
		adapter.update();
		Intent intent = new Intent();
		AddIssueActivity.this.setResult(1, intent);
		AddIssueActivity.this.finish();
	}

}
