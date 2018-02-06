package com.nzy.nim.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.ImagePagerActivity;
import com.nzy.nim.activity.main.CommentInfoActivity;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.bean.DynamicList;
import com.nzy.nim.tool.common.DateUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.view.BaseViewHolder;
import com.nzy.nim.view.NoScrollGridView;
import com.nzy.nim.view.QYUriMatcher;
import com.nzy.nim.view.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/1/28.
 */
public class DynamicAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private Context context;
    private List<DynamicList> list=new ArrayList<DynamicList>();
//    private ArrayList<String> memberIds;
    public DynamicAdapter(Context context, List<DynamicList> list){
//    public DynamicAdapter(Context context,List<DynamicList> list,ArrayList<String> memberIds){
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.list=list;
//        this.memberIds=memberIds;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view = mInflater.inflate(R.layout.listview_comment_item, null);
        }
        LinearLayout not_share = BaseViewHolder.get(view, R.id.not_share);
        RelativeLayout share_container = BaseViewHolder.get(view, R.id.share_container);
        RoundImageView head = BaseViewHolder.get(view, R.id.listview_comment_item_head);
        TextView floor = BaseViewHolder.get(view, R.id.listview_comment_item_floor);
        TextView name = BaseViewHolder.get(view, R.id.listview_comment_item_name);
        final TextView content = BaseViewHolder.get(view, R.id.listview_comment_item_content);
        ImageView flag = BaseViewHolder.get(view, R.id.listview_comment_item_flag);
        TextView timeStamp = BaseViewHolder.get(view, R.id.listview_comment_item_time);
        NoScrollGridView picList = BaseViewHolder.get(view, R.id.listview_comment_item_pics);
        ImageView share_image = BaseViewHolder.get(view, R.id.share_Img);
        TextView shared_title = BaseViewHolder.get(view, R.id.shared_title);
        TextView share_content = BaseViewHolder.get(view, R.id.share_content);

        final DynamicList dynamicList = list.get(i);
        ImageUtil.displayHeadImg(dynamicList.getPersonPhoto(), head);
        floor.setVisibility(View.VISIBLE);
        floor.setText("第" + dynamicList.getPageNow()+"页");
        name.setText(dynamicList.getPersonName());
        Long aLong = Long.valueOf(dynamicList.getCreateTime());
        final Date date = new Date(aLong);
        timeStamp.setText(DateUtil.getStartDate(date, new Date()));
        if(dynamicList.getIsShare()){
            not_share.setVisibility(View.GONE);
            share_container.setVisibility(View.VISIBLE);
            try {
                JSONObject jb = new JSONObject(dynamicList.getShareContent());
                String shareContent = jb.getString("shareContent");
                final String actionUri = jb.getString("actionUri");
                int style = jb.getInt("style");
                String shareType = jb.getString("shareType");
                String title = jb.getString("title");
                shared_title.setText(title);
                if (style == MyConstants.SHARE_ONLY_TEXT) {//无图分享时，隐藏图片
                    share_image.setVisibility(View.GONE);
                }else{
                    share_image.setVisibility(View.VISIBLE);
                    if(shareType.equals(MyConstants.BOOK_REVIEW_TYPE)){
                        share_image.setImageResource(R.drawable.icon_book_review);
                    }else if(shareType.equals(MyConstants.BOOK_SHARE_TYPE)){
                            share_image.setImageResource(R.drawable.pic_default_book);
                    }else{
                        String imgPath = jb.getString("imgPath");
                        ImageUtil.displayNetImg(imgPath, share_image);
                    }
                }
                String contents = Html.fromHtml(shareContent).toString().replaceAll("\\s*", "");
                share_content.setText(contents);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        QYUriMatcher.actionUri(context, actionUri);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            not_share.setVisibility(View.VISIBLE);
            share_container.setVisibility(View.GONE);
            content.setText(dynamicList.getContent());
            if (dynamicList.getImageList()!=null) {
                if(dynamicList.getImageList().size()==1){
                    picList.setNumColumns(1);
                }else if(dynamicList.getImageList().size()==2||dynamicList.getImageList().size()==3||dynamicList.getImageList().size()==4){
                    picList.setNumColumns(2);
                }else{
                    picList.setNumColumns(3);
                }
                picList.setVisibility(View.VISIBLE);
                picList.setAdapter(new PicsGridListAdapter(context, dynamicList.getImageList()));
                picList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        ArrayList<String> list = new ArrayList<String>();
                        list.addAll(dynamicList.getImageList());
                        ImagePagerActivity.actionIntent(context, list,position);
                    }
                });
            }else{
                picList.setVisibility(View.GONE);
            }
            flag.setVisibility(View.GONE);
//        if (memberIds != null && memberIds.size() > 0 && dynamicList.getPersonId().equals(memberIds.get(0))) {// 标志
//            flag.setImageResource(R.drawable.ic_master_flag);
//        } else if (isMember(dynamicList.getPersonId())) {
//            flag.setImageResource(R.drawable.ic_member_flag);
//        }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommentInfoActivity.actionIntent(context,dynamicList.getId());
                }
            });
        }
        return view;
    }
//    /**
//     * @author LIUBO
//     * @date 2015-4-6下午6:43:52
//     * @TODO 判断评论者是否是群成员
//     * @param commentatorId
//     * @return
//     */
//    private boolean isMember(String commentatorId) {
//        if (memberIds == null)
//            return false;
//        for (int i = 0; i < memberIds.size(); i++) {
//            if (commentatorId.equals(memberIds.get(i))) {
//                return true;
//            }
//        }
//        return false;
//    }
}
