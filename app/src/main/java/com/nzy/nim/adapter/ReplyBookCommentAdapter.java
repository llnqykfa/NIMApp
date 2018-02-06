package com.nzy.nim.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.db.BookReviewReplyList;
import com.nzy.nim.tool.common.DateUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.view.RoundImageView;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/10/30.
 */
public class ReplyBookCommentAdapter extends BaseAdapter {
    private List<BookReviewReplyList> reviewReplyLists;
    private Activity activity;

    public ReplyBookCommentAdapter(Activity activity, List<BookReviewReplyList> reviewReplyLists) {
        this.reviewReplyLists = reviewReplyLists;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return reviewReplyLists == null ? 1: reviewReplyLists.size();
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
    public View getView(final int i, View layout, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (layout == null) {
            holder = new ViewHolder();
            layout = activity.getLayoutInflater().inflate(R.layout.item_reply_comment, null);
            holder.tv_name = (TextView) layout.findViewById(R.id.tv_name);
            holder.tv_contents = (TextView) layout.findViewById(R.id.tv_contents);
            holder.tv_comment_time = (TextView) layout.findViewById(R.id.tv_comment_time);
            holder.tv_replyName = (TextView) layout.findViewById(R.id.tv_replyName);
            holder.rl_reply = (RelativeLayout) layout.findViewById(R.id.rl_reply);
            holder.img_comment_pic= (RoundImageView) layout.findViewById(R.id.img_comment_pic);
            layout.setTag(holder);
        } else {
            holder = (ViewHolder) layout.getTag();
        }
        BookReviewReplyList replyList = reviewReplyLists.get(i);
        String personName = replyList.getPersonName();
        String content = replyList.getContent();
        Long createTime = replyList.getCreateTime();
        String parentPersonName = replyList.getParentPersonName();
        if (personName!=null){
            holder.tv_name.setText(personName);
        }
        if (content!=null){
            holder.tv_contents.setText(content);
        }
        if (createTime!=null){
            String date = DateUtil.formatDate(new Date(createTime), "MM-dd HH:mm");
            holder.tv_comment_time.setText(date);
        }
        ImageUtil.displayHeadImg(replyList.getPersonPhotoPath(),holder.img_comment_pic);
        if (parentPersonName!=null&&!"".equals(parentPersonName)){
            holder.rl_reply.setVisibility(View.VISIBLE);
            holder.tv_replyName.setText(parentPersonName);
        }else{
            holder.rl_reply.setVisibility(View.GONE);
        }
        return layout;
    }

    public class ViewHolder {
        TextView tv_name;
        TextView tv_contents;
        TextView tv_comment_time;
        RelativeLayout rl_reply;
        TextView tv_replyName;
        RoundImageView img_comment_pic;
    }
}
