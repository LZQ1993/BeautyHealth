package com.beautyhealth.PrivateDoctors.Activity;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.HttpUtil;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.Infrastructure.CWDataRequest.ReadNetPicture;
import com.beautyhealth.PrivateDoctors.Entity.PictureMessage;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class PDMCpicShowActivity extends DataRequestActivity implements
		OnTouchListener, OnClickListener {
	/**
	 * ImagaSwitcher 的引用
	 */
	private ImageView mImageView;
	private List<String> mImageUrl;
	private ImageButton leftArrow, rightArrow;
	private LinearLayout linearLayout;
	private int currentPosition = 0;
	private float downX;
	private Bitmap bitmap;

	Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pdmc_picshow);
		initNavBar("查看图片", "<返回", null);
		initPic();
		fetchUIFromLayout();		
		setListener();
	}

	private void initPic() {
		ClassFullName = "com.beautyhealth.PrivateDoctors.Entity.PictureMessage";
		String url = NetworkSetInfo.getServiceUrl() + "/PrivateDoctor/queryPic";
		RequestParams params = new RequestParams();
		String condition[] = { "QAID", "TypeID", "page", "rows" };
		String value[] = { getIntent().getStringExtra("QAID"),
				getIntent().getStringExtra("TypeID"), "-1", "18" };
		String strJson = JsonDecode.toJson(condition, value);
		params.put("json", strJson);
		HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable error) {
				new AlertDialog.Builder(PDMCpicShowActivity.this)
						.setTitle("错误")
						.setMessage("网络访问失败，请检查网络！" + error.toString())
						.setPositiveButton("确定", null).show();
				return;
			}

			@Override
			public void onSuccess(String content) {
				dataResult = dataDecode.decode(content, ClassFullName);
				if (dataResult != null) {
					DataResult realData = (DataResult) dataResult;
					if (realData.getResultcode().equals("1")) {
						mImageUrl = new ArrayList<String>();
						for (int i = 0; i < realData.getResult().size(); i++) {
							PictureMessage msg = (PictureMessage) realData.getResult()
									.get(i);
							String webaddrss = NetworkSetInfo.getServiceUrl()+"/"+msg.PathAndFileName.substring(2, msg.PathAndFileName.length());
							mImageUrl.add(webaddrss);
							
						}
						downloadpic(0);
					} else {
						new AlertDialog.Builder(PDMCpicShowActivity.this)
								.setTitle("提示")
								.setMessage("暂无数据")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,
													int which) {
												finish();
												return;
											}
										}).setCancelable(false).show();
					}
				} else {
					new AlertDialog.Builder(PDMCpicShowActivity.this)
							.setTitle("错误")
							.setMessage("网络加载失败")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											finish();
											return;
										}
									}).setCancelable(false).show();
				}
			}

		});
	}
	
	private void setListener() {
		rightArrow.setOnClickListener((OnClickListener) this);
		leftArrow.setOnClickListener((OnClickListener) this);

	}

	@SuppressWarnings("deprecation")
	private void fetchUIFromLayout() {
		leftArrow = (ImageButton) findViewById(R.id.pdmc_left_arrow);
		rightArrow = (ImageButton) findViewById(R.id.pdmc_right_arrow);
		// 实例化ImageView
		mImageView = (ImageView) findViewById(R.id.pdmc_imageView);

		// 设置OnTouchListener，我们通过Touch事件来切换图片
		mImageView.setOnTouchListener(this);
		mImageView.setImageResource(R.drawable.userphoto);
		leftArrow.setVisibility(View.INVISIBLE);
	}

	private void downloadpic(final int position) {
		showProgressDialog("图片加载中，请稍候...");
		new Thread() {
			public void run() {
				bitmap = ReadNetPicture.getHttpBitmap(mImageUrl.get(position));
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (bitmap == null) {
							mImageView.setImageResource(R.drawable.userphoto);
						} else {
							mImageView.setImageBitmap(bitmap);
						}
						dismissProgressDialog();
					}
				});
			};

		}.start();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			// 手指按下的X坐标
			downX = event.getX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			float lastX = event.getX();
			// 抬起的时候的X坐标大于按下的时候就显示上一张图片
			if (lastX > downX) {
				prev(v);
			}

			if (lastX < downX) {
				next(v);
			}
		}

			break;
		}

		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == leftArrow) {
			leftArrow.setAnimation(AnimationUtils.loadAnimation(
					getApplication(), R.anim.arrow));
			prev(v);
		}
		if (v == rightArrow) {
			rightArrow.setAnimation(AnimationUtils.loadAnimation(
					getApplication(), R.anim.arrow));
			next(v);
		}

	}

	public void prev(View source) {

		rightArrow.setVisibility(View.VISIBLE);
		// 显示上一个组件
		if (currentPosition > 0) {
			// 设置动画，这里的动画比较简单，不明白的去网上看看相关内容
			currentPosition--;
			if (currentPosition == 0) {
				leftArrow.setVisibility(View.INVISIBLE);
			}
			downloadpic(currentPosition % mImageUrl.size());
		} else {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).setMessage("已经是第一张").setCancelable(false).show();
			leftArrow.setVisibility(View.INVISIBLE);
		}

	}

	public void next(View source) {
		leftArrow.setVisibility(View.VISIBLE);
		// 显示下一个组件。
		if (currentPosition < (mImageUrl.size()-1)){

			currentPosition++;	
			if (currentPosition == (mImageUrl.size()-1)) {
				rightArrow.setVisibility(View.INVISIBLE);
			}
			downloadpic(currentPosition % mImageUrl.size());
		} else {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).setMessage("到了最后一张").setCancelable(false).show();
			rightArrow.setVisibility(View.INVISIBLE);
		}

	}

}
