package com.beautyhealth.Infrastructure.CWFileSystem;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

public abstract class AbsFileSystem implements IFileSystem {

	private Context cxt;
	public void setContext(Context _cxt) {
		// TODO Auto-generated method stub
		cxt=_cxt;
	}
	
	public Context getContext(){
		return cxt;
	}

	@Override
	public String readTxt(String inFileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean write(String content, String outFileName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Map<String, Object>> getFileList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void addFile(File[] currentFiles,List<Map<String,Object>> filesAndFolders){
		for(int i=0;i<currentFiles.length;i++){
			Map<String,Object> fileAndFolder= new HashMap<String,Object>();
			if(currentFiles[i].isFile()){
				fileAndFolder.put("type", "file");
				fileAndFolder.put("name", currentFiles[i].getName());
				filesAndFolders.add(fileAndFolder);
			}else{
				fileAndFolder.put("type", "folder");
				fileAndFolder.put("name", currentFiles[i].getName());
				filesAndFolders.add(fileAndFolder);
				addFile(currentFiles[i].listFiles(),filesAndFolders);
			}

		}
	}
	
    public String getLocalPath() {
        return cxt.getApplicationContext().getFilesDir().getAbsolutePath();
    }
    
	public Bitmap getImage(String fileName){
		return null;
	}

}
