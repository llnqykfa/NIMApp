package com.nzy.nim.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2016/9/21.
 */
public class FaceImage extends DataSupport {
    private String faceImage;
    private onGridViewItemClickListener onClickListener;
    public String getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }
    public onGridViewItemClickListener getOnClickListener() {
        return onClickListener;
    }
    public void setOnClickListener(onGridViewItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface onGridViewItemClickListener
    {
        public abstract void ongvItemClickListener(int position);
    }
}
