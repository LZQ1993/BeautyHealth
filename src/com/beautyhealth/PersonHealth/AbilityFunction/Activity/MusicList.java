package com.beautyhealth.PersonHealth.AbilityFunction.Activity;

import java.util.ArrayList;
import java.util.List;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWComponent.TabBarFragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

public class MusicList  extends  NavAndTabBarActivity implements OnItemClickListener   
{   

	private List<MusicItemBean> musicList = new ArrayList<MusicItemBean>();
	private ListView musiclist;
	private  MusicAdapter adapter;   
    private List<String> mMusicpath = new ArrayList<String>();      
    private int currentListItme = 0; 
   
    /** Called when the activity is first created. */   
    @Override   
    public void onCreate(Bundle savedInstanceState)   
    {   
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.music_select); 
        initNavBar("铃声选择", "<返回",null);
        
        musiclist = (ListView) findViewById(R.id.listView1);
        musiclist.setOnItemClickListener(this);      
        /* 更新显示播放列表 */               
        musicList();
    }    
    
    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {    
    	currentListItme = position; 
        String fileName=mMusicpath.get(position);
        Intent in_result = new Intent();  
        in_result.putExtra("fileName", fileName); 
	    //-1为RESULT_OK, 1为RESULT_CANCEL..  
		// in 则是回调的Activity内OnActivityResult那个方法内处理
        setResult(-1, in_result);  
        finish(); 		
	}     
   
    /* 播放列表 */   
    public void musicList()   
    {   
    	Cursor cursor = MusicList.this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
	    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	    // 遍历媒体数据库
	    if (cursor.moveToFirst())
	    {
	    	while (!cursor.isAfterLast())
		    {	   
			    // 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
			    String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			    // 歌曲文件显示名字
			    String disName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));		
			    
			    MusicItemBean mib = new MusicItemBean(disName);    
			    musicList.add(mib);
			    
		        mMusicpath.add(url);      		    
			    cursor.moveToNext();
			}	    	 
            adapter= new MusicAdapter(this, musicList);            
            musiclist.setAdapter(adapter);           
            
	    	cursor.close();
		 }       
    }       	   
}
   
