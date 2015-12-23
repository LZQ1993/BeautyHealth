package com.beautyhealth.Infrastructure.CWComponent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestFragment;
import com.beautyhealth.Infrastructure.CWDataRequest.LoadImage;
import com.beautyhealth.Infrastructure.CWDomain.ReturnImageMessage;
import com.beautyhealth.Infrastructure.CWFileSystem.IFileSystem;
import com.beautyhealth.Infrastructure.CWFileSystem.LocalFileSystem;

public class ImagesShowFragment extends DataRequestFragment implements OnClickListener{
	
	private ImageView imageshow;
	private ImageButton next;
	private ImageButton prev;
	private int current = 0;
	private int total = 0;

	private String localPath;
	private List<ReturnImageMessage> downloadPics = new ArrayList<ReturnImageMessage>();
	private LoadImage load;
	private IFileSystem myfilesystem;
	public boolean HideBtn=false;
	public int loadingPicID=0;	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		View _view = inflater.inflate(R.layout.imageshow, container, false);
		initVariable();
		fetchUIFromLayout(_view);
		setListener();	
		setNextAndPrevBtn();
		return _view;
	}

	private void initVariable() {
		setAction(null);// user this------
		IsLocal = false;// user this------
		loadingPicID=R.drawable.ic_launcher;
		myfilesystem = new LocalFileSystem(getActivity());
		localPath = myfilesystem.getLocalPath();
		load = new LoadImage(localPath);
	}

	private void fetchUIFromLayout(View _view) {
		imageshow = (ImageView) _view.findViewById(R.id.imageshow);
		next = (ImageButton) _view.findViewById(R.id.next);
		prev = (ImageButton) _view.findViewById(R.id.prev);
	}

	private void setListener() {
		next.setOnClickListener((OnClickListener) this);
		prev.setOnClickListener((OnClickListener) this);
	}

	public void onClick(View v) {
		// 跳转到首页
		if (v == next) {
			current++;
			if (current > total - 1) {
				current = 0;
			}
		} else if (v == prev) {
			current--;
			if (current < 0) {
				current = total - 1;
			}
		}
		updateImageShow();
	}

	private void loadImageOnView(ReturnImageMessage current) {
		// 下载图片
		try {
			if(!IsLocal){
				load.download(current.BaseUrl, current.FileAndPath,
						Long.valueOf(current.Size));
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateView() {
		super.updateView();
		// user this------
		if (dataResult != null) {
			//user this------
			DataResult realData=(DataResult)dataResult;
			if(realData.getResult().size()>0){
				current = 0;
				total = realData.getResult().size();
				
				for (int i = 0; i < total; i++) {
					ReturnImageMessage areturn = (ReturnImageMessage) realData
							.getResult().get(i);
					areturn.BaseUrl = "http://www.xqtian.com/course/";///////
					downloadPics.add(areturn);
				}
				updateImageShow();
			} else {
				Toast.makeText(getActivity(),"loading errors:nodata",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getActivity(), "loading errors", Toast.LENGTH_SHORT)
					.show();
		}
		setNextAndPrevBtn();
	}

	private void updateImageShow() {
		// 显示 一个加载或刷新的小图标
		new Thread(runnable).start();
	}

	private Bitmap getBitmap(String filename,String filePath){
		Bitmap bitmap = null;

		try {
			File file = new File(filePath);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(filePath);
			}
		} catch (Exception e) {
			
		}
		return bitmap;
	}
	
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bitmap bitmap = null;
			Bundle data = msg.getData();
			String val = data.getString("value");
			String filename = downloadPics.get(current).FileAndPath;
			String filePath = localPath + File.separator
					+ filename.substring(filename.lastIndexOf("/") + 1);
			if (val == "OK") {
				if(IsLocal){
					bitmap=myfilesystem.getImage(filename);
				} else {
					bitmap=getBitmap(filename,filePath);
				}
				
			} else {
				bitmap = BitmapFactory.decodeResource(getResources(),
						loadingPicID);
			}

			imageshow.setImageBitmap(bitmap);
		}
	};
	


	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			Message msg = new Message();
			Bundle data = new Bundle();

			try {// 下载图片
				loadImageOnView(downloadPics.get(current));
				data.putString("value", "OK");
			} catch (Exception e) {
				data.putString("value", "error");
			}
			msg.setData(data);
			handler.sendMessage(msg);
		}
	};
	
	public void setNextAndPrevBtn(){
		if(total==0||HideBtn){
			next.setVisibility(View.GONE);
			prev.setVisibility(View.GONE);
		}else{
			next.setVisibility(View.VISIBLE);
			prev.setVisibility(View.VISIBLE);
		}
	}
	
	public void setloadingPicID(int _loadingPicID){
		loadingPicID=_loadingPicID;
	}
}
