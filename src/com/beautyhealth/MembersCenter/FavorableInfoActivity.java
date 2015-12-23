package com.beautyhealth.MembersCenter;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.MembersCenter.Entity.AdvertisementInfo;

public class FavorableInfoActivity extends DataRequestActivity{
   private TextView tv_fi_briefly,tv_adTitle,tv_publicTime;
   private ImageView iv_brieflypic;
   private AdvertisementInfo advertisementInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorableinfo);
		setAction(null);// user this------
		IsLocal = false;// user this------
		initNavBar("优惠详情", "<返回", null);
		initData();
		fetchUIFromLayout();
		
	}
	private void initData() {
		advertisementInfo = (AdvertisementInfo) getIntent().getSerializableExtra(
				"advertisementItem");
	}
	private void fetchUIFromLayout() {
		tv_fi_briefly = (TextView) findViewById(R.id.tv_fi_briefly);
		if (advertisementInfo.AdBriefly.equals("")||advertisementInfo.AdBriefly==null) {
			tv_fi_briefly.setText("暂无介绍");
		} else {
			tv_fi_briefly.setText(advertisementInfo.AdBriefly);
		}
		tv_adTitle = (TextView) findViewById(R.id.tv_adTitle);
		tv_adTitle.setText(advertisementInfo.AdTitle);
		tv_publicTime = (TextView) findViewById(R.id.tv_publicTime);
		tv_publicTime.setText(advertisementInfo.PublishTime);
		
		iv_brieflypic = (ImageView) findViewById(R.id.iv_brieflypic);
		
	}

}
