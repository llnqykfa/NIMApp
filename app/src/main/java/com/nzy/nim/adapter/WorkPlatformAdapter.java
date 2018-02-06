package com.nzy.nim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.view.BaseViewHolder;

/**
 * Created by Administrator on 2016/11/28.
 */
public class WorkPlatformAdapter extends BaseAdapter {

    private Context mContext;
    private int[] funImgs;
    private String[] funTitles;

    public WorkPlatformAdapter(Context ctx, int[] imgs, String[] titles) {
        super();
        this.mContext = ctx;
        this.funImgs = imgs;
        this.funTitles = titles;
    }

    @Override
    public int getCount() {
        return this.funImgs == null ? 0 : this.funImgs.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WorkPlatformViewHolder  workPlatformViewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_workplatform, parent, false);
            workPlatformViewHolder  = new WorkPlatformViewHolder();
            workPlatformViewHolder.txtView = (TextView) convertView.findViewById(R.id.grid_item_txt);
            workPlatformViewHolder.imgView = (ImageView) convertView.findViewById(R.id.grid_item_img);
            workPlatformViewHolder.imgView.setBackgroundResource(funImgs[position]);
            workPlatformViewHolder.txtView.setText(funTitles[position]);
            convertView.setTag(workPlatformViewHolder);
        }else{
            workPlatformViewHolder = (WorkPlatformViewHolder) convertView.getTag();
        }

        /*TextView txtView = BaseViewHolder.get(convertView, R.id.grid_item_txt);
        ImageView imgView = BaseViewHolder.get(convertView, R.id.grid_item_img);
        imgView.setBackgroundResource(funImgs[position]);
        txtView.setText(funTitles[position]);*/

        workPlatformViewHolder.txtView.setText(funTitles[position]);
        workPlatformViewHolder.imgView.setBackgroundResource(funImgs[position]);
        return convertView;
    }

    public class WorkPlatformViewHolder {
        TextView txtView;
        ImageView imgView;
    }
}
