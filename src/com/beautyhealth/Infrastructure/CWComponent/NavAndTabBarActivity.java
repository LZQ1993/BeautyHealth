package com.beautyhealth.Infrastructure.CWComponent;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.beautyhealth.R;

public class NavAndTabBarActivity extends Activity {

	    public final static int RESULT_OK = Activity.RESULT_OK;
	    public final static int RESULT_ERROR = Activity.RESULT_FIRST_USER;
	    public final static int RESULT_CANCELED = Activity.RESULT_CANCELED;
	    public final static int REQUEST_DEFAULT = 0;

	  //  private ProgressDialog progressDialog;
	    
	    private TextView tvTitle;
	    private Button btnLeft;  
	    private Button btnRight;

	    /**
	     * onCreate
	     */
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
    
	    }
	    
	   
	    /**
	     * 初始化导航栏
	     */
	    protected void initNavBar(String title,String left,String right) {
	        tvTitle = (TextView) findViewById(R.id.nav_bar_tv_title);
	        tvTitle.setText(title);
	        btnLeft = (Button) findViewById(R.id.nav_bar_btn_left);
	        btnLeft.setText(left);
	        btnRight = (Button) findViewById(R.id.nav_bar_btn_right);
	        btnRight.setText(right);
	        if(title == null) tvTitle.setVisibility(View.INVISIBLE);
	        if(left == null) btnLeft.setVisibility(View.INVISIBLE);
	        if(right == null) btnRight.setVisibility(View.INVISIBLE);
	    }

	    /**
	     * 设置标题
	     */
	    protected void setNavBarTitle(String title) {
	        tvTitle.setText(title);
	    }

	    /**
	     * 获取标题
	     */
	    protected String getNavBarTitle() {
	        return tvTitle.getText().toString();  
	    }

	    /**
	     * 设置左按钮文本
	     */
	    protected void setNavBarLeftButtonText(String text) {
	        btnLeft.setText(text);
	    }

	    /**
	     * 设置右按钮文本
	     */
	    protected void setNavBarRightButtonText(String text) {
	        btnRight.setText(text);
	    }

	    /**
	     * 获取左按钮文本
	     */
	    protected String getNavBarLeftButtonText() {
	        return btnLeft.getText().toString();
	    }

	    /**
	     * 获取右按钮文本
	     */
	    protected String getNavBarRightButtonText() {
	        return btnRight.getText().toString();
	    }

	    /**
	     * 左按钮监听函数
	     */
	    public void onNavBarLeftButtonClick(View view) {
	        finish();
	    }

	    /**
	     * 右按钮监听函数
	     */
	    public void onNavBarRightButtonClick(View view) {

	    }

	    /**
	     * 标题监听
	     */
	    public void onNavBarTitleClick(View view) {
	    }

	    /**
	     * 左加图片
	     */
	    public void onNavBarLeftButtonBackground(int picId){
	    	this.btnLeft.setBackground(getResources().getDrawable(picId));
	    }
	    
	    /**
	     * 右加图片
	     */
	    public void onNavBarRightButtonBackground(int picId){
	    	this.btnRight.setBackground(getResources().getDrawable(picId));
	    }
	    
	    /**
	     * 设置左按钮可视
	     */
	    protected void setNavBarLeftButtonVisible(boolean visible) {
	        if(visible) btnLeft.setVisibility(View.VISIBLE);
	        else btnLeft.setVisibility(View.INVISIBLE);
	    }

	    /**
	     * 设置右按钮可视
	     */
	    protected void setNavBarRightButtonVisible(boolean visible) {
	        if(visible) btnRight.setVisibility(View.VISIBLE);
	        else btnRight.setVisibility(View.INVISIBLE);
	    }
	   /* *//**
	     * 显示进度条
	     *//*
	    protected void showProgressDialog(String content) {
	        progressDialog = new ProgressDialog(this);  
	    	progressDialog.setMessage(content);
	        progressDialog.setCancelable(false);
	        progressDialog.show();
	        progressDialog.setOnKeyListener(onKeyListener);
	        
	    }
	    private OnKeyListener onKeyListener = new OnKeyListener() {
	        @Override
	        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
	            	//removeBroadcast();
	            	
	            	dismissProgressDialog();
	            	//handler.sendEmptyMessage(0);
	            }
	            return false;
	        }
	    };

	    
	    *//**
	     * 隐藏进度条
	     *//*
	    protected void dismissProgressDialog() {
	    	 if (isFinishing()) {
	    		// removeBroadcast();
	             return;
	         }
	         if (null != progressDialog && progressDialog.isShowing()) {
	        	// removeBroadcast(); 	
	        	 progressDialog.dismiss();
	        	// handler.sendEmptyMessage(0);
	            
	         }
	    }

	    @Override
	    public void onBackPressed() {
	        if (progressDialog != null && progressDialog.isShowing()) {
	        	// removeBroadcast();
	        	dismissProgressDialog();  
	        	// handler.sendEmptyMessage(0);
	        } else {
	            super.onBackPressed();
	        }
	    }*/
	}
