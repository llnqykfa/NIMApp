package com.nzy.nim.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.main.DynamicCommentActivity;
import com.nzy.nim.tool.common.DateUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.vo.RingList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/1/27.
 */
public class RingAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private Context context;
    private List<RingList> ringList=new ArrayList<RingList>();
    OnPointListens listens;
    private boolean isdigg;
    private boolean isfirst;
    public RingAdapter(Context context, List<RingList> ringList){
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.ringList=ringList;
    }
    public interface OnPointListens{
        void getPointPosition(String ringId, View view, int itemIndex);
    }

    public void setPointPosition(OnPointListens listens){
        this.listens = listens;
    }
    @Override
    public int getCount() {
        return ringList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        isfirst=true;
        ViewHolder holder=null;
        if(view==null){
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.listview_all_groups_item, null);
            holder.all_group_item_img = (ImageView) view.findViewById( R.id.all_group_item_img);//封面图
            holder.all_groups_item_title = (TextView) view.findViewById(R.id.all_groups_item_title);//标题
            holder.all_groups_item_time = (TextView) view.findViewById(R.id.all_groups_item_time);//时间
            holder.all_groups_item_content = (TextView) view.findViewById(R.id.all_groups_item_content);//内容
            holder.text_class = (TextView) view.findViewById(R.id.text_class);//分类
            holder.comment_layout = (LinearLayout) view.findViewById(R.id.comment_layout);//评论
            holder.all_groups_item_review_ic = (ImageView) view.findViewById( R.id.all_groups_item_review_ic);//评论图片
            holder.all_groups_item_review_num = (TextView) view.findViewById(R.id.all_groups_item_review_num);//评论数量
            holder.point_layout = (LinearLayout) view.findViewById(R.id.point_layout);//点赞
            holder.all_groups_item_point_like = (ImageView) view.findViewById(R.id.all_groups_item_point_like);//点赞图片
            holder.all_groups_item_point_like_num = (TextView) view.findViewById(R.id.all_groups_item_point_like_num);//点赞数量
            holder.relat_class = (RelativeLayout) view.findViewById(R.id.relat_class);
            holder.img_tabtype=(ImageView)view.findViewById(R.id.img_tabtype);
            view.setTag(holder);
        }else{
            holder=(ViewHolder)view.getTag();
        }
        final RingList list = ringList.get(i);
        if(list.getImage()!=null){
            ImageUtil.displayImg(list.getImage(), holder.all_group_item_img);
        }else{
            holder.all_group_item_img.setImageResource(R.drawable.default_image);
        }
        holder.all_groups_item_title.setText(list.getTheme());
        Long aLong = Long.valueOf(list.getCreateTime());
        final Date date = new Date(aLong);
        holder.all_groups_item_time.setText(DateUtil.getStartDate(date, new Date()));
        holder.all_groups_item_content.setText(list.getIntroduce());
        if(list.getCategoryName().toString().equals("未分类")){
            holder.text_class.setVisibility(View.GONE);
            holder.relat_class.setVisibility(View.GONE);
        }else{
            holder.relat_class.setVisibility(View.VISIBLE);
            holder.text_class.setVisibility(View.VISIBLE);
            holder.text_class.setText(list.getCategoryName());
            holder.text_class.setTextColor(Color.rgb(38, 126, 233));
        }
//        holder.all_groups_item_review_num.setText("" + list.getCommentNumber());
        if (list.getDynamicNumber()!=null) {
            holder.all_groups_item_review_num.setText(list.getDynamicNumber().toString());
        }
        if (list.getDiggNumber()!=null) {
            holder.all_groups_item_point_like_num.setText(list.getDiggNumber().toString());
        }
//        if (list.isDigg()) {// 是否点赞
//            holder.all_groups_item_point_like.setImageResource(R.drawable.ic_point);
//            holder.all_groups_item_point_like_num.setTextColor(Color.rgb(38, 126, 233));
//        } else {
//            holder.all_groups_item_point_like.setImageResource(R.drawable.ic_point_like_normal);
//            holder.all_groups_item_point_like_num.setTextColor(Color.rgb(96, 114, 130));
//        }

        Integer tabType = list.getTabType();
        if (tabType!=null) {
            if(tabType==2){
                holder.img_tabtype.setVisibility(View.VISIBLE);
                holder.img_tabtype.setImageResource(R.drawable.img_ring_hot);
            }else if(tabType==3){
                holder.img_tabtype.setVisibility(View.VISIBLE);
                holder.img_tabtype.setImageResource(R.drawable.img_ring_top);
            }else{
                holder.img_tabtype.setVisibility(View.GONE);
            }
        }

        final View finalLayout = view;
        holder.point_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isfirst==true){
                    isfirst=false;
                    isdigg=list.getIsDigg();
                }
                isdigg=!isdigg;
                listens.getPointPosition(list.getRingId(),finalLayout,i);
            }
        });
        holder.comment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.getRingId()==null||list.getIsInclude()==null) {
                    return;
                }
                DynamicCommentActivity.actionIntent(context, null, list.getRingId(), list.getIsInclude());
            }
        });
        return view;
    }
    /**
     * 点赞数量局部刷新
     * @param itemIndex item下标
     */
    public void updateView(View view,int itemIndex) {
        if(view == null) {
            return;
        }
        Integer diggNumber = ringList.get(itemIndex).getDiggNumber();
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.all_groups_item_point_like_num.setText("" + diggNumber);
//            if(ringList.get(itemIndex).isDigg()){
//                holder.all_groups_item_point_like.setImageResource(R.drawable.ic_point);
//                holder.all_groups_item_point_like_num.setTextColor(Color.rgb(38, 126, 233));
//            }else{
//                holder.all_groups_item_point_like.setImageResource(R.drawable.ic_point_like_normal);
//                holder.all_groups_item_point_like_num.setTextColor(Color.rgb(96, 114, 130));
//            }
    }
    public static class ViewHolder {
        ImageView all_group_item_img;//封面图
        TextView all_groups_item_title ;//标题
        TextView all_groups_item_time ;//时间
        TextView all_groups_item_content;//内容
        TextView text_class ;//分类
        LinearLayout comment_layout ;//评论
        ImageView all_groups_item_review_ic ;//评论图片
        TextView all_groups_item_review_num ;//评论数量
        LinearLayout point_layout ;//点赞
        ImageView all_groups_item_point_like ;//点赞图片
        TextView all_groups_item_point_like_num ;//点赞数量
        RelativeLayout relat_class;
        ImageView img_tabtype;//组圈标识
    }
}
