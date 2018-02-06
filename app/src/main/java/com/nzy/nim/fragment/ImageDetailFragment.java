package com.nzy.nim.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nzy.nim.R;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.ImageCache;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.photoview.PhotoViewAttacher;

/**
 * @author LIUBO
 * @date 2015-4-5下午6:50:59
 * @TODO 图片展示的具体界面
 */
public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url")
				: null;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_image_detail,
				container, false);
		mImageView = (ImageView) v
				.findViewById(R.id.fragment_image_detail_image);
		mAttacher = new PhotoViewAttacher(mImageView);

		mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
			}
		});

		progressBar = (ProgressBar) v
				.findViewById(R.id.fragment_image_detail_loading);
		mAttacher.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showRemindDialog();
				return true;
			}
		});
		return v;
	}
	private void showRemindDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("保存图片")
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if(!DataUtil.isEmpty(mImageUrl)){
							ToastUtil.showShort(getActivity(), mImageUrl);
							Bitmap bitmap = ImageCache.getInstance().get(mImageUrl);
							ImageUtil.saveImageToGallery(getActivity(), bitmap, MyConstants.BASE_DIR);
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).show();
		AlertDialog alert = builder.create();
	}
		@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ImageUtil.displayNetImg(mImageUrl, mImageView,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						progressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "下载错误";
							break;
						case DECODING_ERROR:
							message = "图片无法显示";
							break;
						case NETWORK_DENIED:
							message = "网络有问题，无法下载";
							break;
						case OUT_OF_MEMORY:
							message = "图片太大无法显示";
							break;
						case UNKNOWN:
							message = "未知的错误";
							break;
						}
						Toast.makeText(getActivity(), message,
								Toast.LENGTH_SHORT).show();
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						progressBar.setVisibility(View.GONE);
						mAttacher.update();
					}
				});

	}

}
