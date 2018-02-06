package com.nzy.nim.db;

import com.nzy.nim.db.bean.ImageItem;

import java.io.Serializable;
import java.util.List;

public class ImageBucket implements Serializable {
	private static final long serialVersionUID = 1L;
	public int count = 0;
	public String bucketName;
	public List<ImageItem> imageList;
	public boolean isSelected = false;
}
