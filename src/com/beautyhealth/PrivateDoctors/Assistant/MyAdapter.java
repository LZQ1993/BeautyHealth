package com.beautyhealth.PrivateDoctors.Assistant;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;

import com.beautyhealth.R;

public class MyAdapter extends BaseAdapter {

    private List<HashMap<String, Object>> imageItem;
    private LayoutInflater inflater=null;
    public MyAdapter(Context context, List<HashMap<String, Object>> imageItem) {
        super();
        this.inflater = LayoutInflater.from(context);
       this.imageItem=imageItem;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageItem.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return imageItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    private class Holder{

        ImageView img=null;
        public ImageView getImg() {
            return img;
        }
        public void setImg(ImageView img) {
            this.img = img;
        }

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.griditem_addpic, null);
            holder=new Holder();
            holder.img=(ImageView) convertView.findViewById(R.id.imageView1);
            convertView.setTag(holder);
        }else{
            holder=(Holder) convertView.getTag();
        }
           holder.img.setImageBitmap((Bitmap)imageItem.get(position).get("itemImage"));
        return convertView;
    }

}
