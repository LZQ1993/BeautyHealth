package com.beautyhealth.PersonHealth.MedicalReport;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;

public class MedicalReportActivity extends DataRequestActivity {
	private WebView medicalReport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medicalreport);
		initNavBar("体检报告", "<返回", null);
		setAction(null);// user this------
		IsLocal = true;// user this------
		fetchUIFromLayout();
		setListener();
	}

	private void setListener() {

	}

	private void fetchUIFromLayout() {

		// 实例化WebView对象
		medicalReport = (WebView) findViewById(R.id.wv_medicalreport);
		medicalReport.getSettings().setAllowFileAccess(true);
		medicalReport.getSettings().setJavaScriptEnabled(true);
		medicalReport.getSettings().setBlockNetworkImage(false);
		medicalReport.getSettings().setBlockNetworkLoads(false);
		medicalReport.getSettings().setBuiltInZoomControls(true);
		medicalReport.getSettings().setJavaScriptCanOpenWindowsAutomatically(
				true);
		medicalReport.getSettings().setLoadsImagesAutomatically(true);
		medicalReport.getSettings().setDomStorageEnabled(true);
		medicalReport.requestFocusFromTouch();
		medicalReport.getSettings().setBuiltInZoomControls(true);

		medicalReport.loadUrl("http://meinian.cn/Research.aspx");
		medicalReport.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
/*		medicalReport.addJavascriptInterface(new Object() {
			public void send(String info) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ info));
				startActivity(intent);
			}
		}, "hjzxcall");*/

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)&&medicalReport.canGoBack()) {
			medicalReport.goBack(); // goBack()表示返回WebView的上一页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
		//return false;
	}

}
