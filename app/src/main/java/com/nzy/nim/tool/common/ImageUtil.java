package com.nzy.nim.tool.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nzy.nim.R;
import com.nzy.nim.api.URLs;
import com.nzy.nim.view.MaskImage;
import com.nzy.nim.vo.QYApplication;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ImageUtil {
    /**
     * 判断是否是android4.4版本
     */
    public static boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    /**
     * @param bitmap
     * @param path
     * @param names
     * @return
     * @author LIUBO
     * @date 2015-3-29下午12:28:15
     * @TODO 保存图片
     */
    public static String saveBitmap(Bitmap bitmap, String path, String names) {
        String filePath = path + File.separator + names;
        if (DataUtil.isEmpty(bitmap))
            return null;
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(new File(filePath));
            bos = new BufferedOutputStream(fos);
            // 保存相片
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 关闭输出流
            try {
                if (fos != null)
                    fos.close();
                if (bos != null)
                    bos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    public static void saveImageToGallery(Context context, Bitmap bitmap, String path) {
        if (bitmap == null) {
            return;
        }
        // 首先保存图片
        File appDir = new File(path);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        String filePath = path + File.separator + fileName;
        File file = new File(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            ToastUtil.showShort(context,"保存成功！");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
    }

    @SuppressLint({"NewApi", "DefaultLocale"})
    public static CompressFormat getImgExtensions(String extension) {
        if (extension == null)
            return null;
        String str = extension.toLowerCase();
        if ("jpeg".equals(str) || "jpg".equals(str))
            return Bitmap.CompressFormat.JPEG;
        if ("png".equals(str))
            return Bitmap.CompressFormat.PNG;
        if ("webp".equals(str))
            return Bitmap.CompressFormat.WEBP;
        return null;
    }

    /**
     * 将byte数组保存到本地 Yi 2015-3-10上午9:39:14 TODO Yi 2015-3-10上午9:40:18 TODO
     *
     * @param filePath
     * @param content
     * @return*String
     */
    public static String saveByte2Native(String filePath, byte[] content) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            fos.write(content);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    /**
     * 将图片转换成byte数组
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 将图片转换成字节流
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 得到图片的大小
     *
     * @param bitmap
     */
    public static int compareBitmapSize(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray().length / 1024;
    }

    /**
     * 对图片的尺寸进行压缩
     *
     * @param targetWidth  缩放的目标宽度
     * @param targetHeigth 缩放的目标高度
     * @return 返回压缩后的图片
     * <p/>
     * 这边的图片压缩采用的是读取方式，生成一张压缩后的图片，并不会对之前的进行一个覆盖
     * @author 张全艺
     * @since 2015-2-2
     * 要压缩的图片
     */
    public static Bitmap compressBySize(String path, int targetWidth,
                                        int targetHeigth) {
        final BitmapFactory.Options opts = new BitmapFactory.Options();
        // 这里只读取图片大小，不读取图片内容
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        // 调用上面定义的方法计算inSampleSize值
        opts.inSampleSize = calculateInSampleSize(opts, targetWidth,
                targetHeigth);
        // 设置好缩放比例后，加载进内存
        opts.inJustDecodeBounds = false;
        // 因为我们对图片压缩后生成新的图片，但是之前的图片还是存在的，所以要在这里对它就行一个释放，否则会占用很大内存
        return BitmapFactory.decodeFile(path, opts);
    }

    /**
     * 对图片占用内存大小进行压缩
     */
    @SuppressLint("NewApi")
    public static Bitmap compressImage(Bitmap image, CompressFormat format,
                                       int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(format, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024.0 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            if (options == 60) {
                break;
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * @param options   BitmapFactory.Options
     * @param reqWidth  目标宽
     * @param reqHeight 目标高
     * @return
     * @author quanyi
     * @date 2015-3-22下午1:02:41
     * @TODO TODO计算压缩的比例
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        // 分别计算图片的宽高与目标宽高的比例，获取最大的比例的整数值
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * @param sourcePath
     * @param reqWidth
     * @param reqHeight
     * @return
     * @author LIUBO
     * @date 2015-4-10上午12:15:27
     * @TODO 获取压缩后的图片路径
     */
    public static String getCompressImgPath(String sourcePath, int reqWidth,
                                            int reqHeight) {
        if (new File(sourcePath).length() < 100 * 1024) {
            return sourcePath;
        }
        Bitmap bp = compressBySize(sourcePath, reqWidth, reqHeight);
        bp = compressImage(bp, CompressFormat.JPEG, 100);
        return saveBitmap(bp, QYApplication.createDirPath("tmpPic"),
//                System.currentTimeMillis() + ".jpg");
                UUID.randomUUID() + ".jpg");
    }

    /**
     * @param selectedImage
     * @author 刘波
     * @date 2015-3-1下午4:17:05
     * @todo 根据图库图片uri发送图片
     */
    public static String getLocalImgPath(Context context, Uri selectedImage) {
        String path = "";
        Cursor cursor = context.getContentResolver().query(selectedImage, null,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(context, "找不到图片",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;
            }
            path = picturePath;
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(context, "找不到图片",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;
            }
            path = file.getAbsolutePath();
        }
        return path;
    }

	/* ##########################图片展示################################## */
    /**
     * 图片参数配置
     */
    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
            .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();


    public static DisplayImageOptions optionsOfBigImg = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)
            // 设置下载的图片是否缓存在SD卡中
            .showImageOnLoading(R.drawable.default_img_1)
            .showImageOnFail(R.drawable.pic_default_head)
            .showImageForEmptyUri(R.drawable.pic_default_head)
            .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
            .build();

    public static DisplayImageOptions localImgOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true)
            .showImageOnLoading(R.drawable.default_img_1)
            .showImageOnFail(R.drawable.default_image)
            .showImageForEmptyUri(R.drawable.default_image)
            .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
            .build();
    /**
     * 图片参数配置
     */
    public static DisplayImageOptions headImgOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)
            // 设置下载的图片是否缓存在SD卡中
            .bitmapConfig(Bitmap.Config.RGB_565)
            // 设置图片的解码类型//
            .showImageForEmptyUri(R.drawable.pic_default_head)
            .showImageOnFail(R.drawable.pic_default_head)
            .showImageOnLoading(R.drawable.pic_default_head)
            .showImageForEmptyUri(R.drawable.pic_default_head).build();

    /**
     * 展示聊天界面的图片
     *
     * @param uri
     * @param iv
     * @param maskSource
     */
    public static void disPlayChatImg(final String uri, final MaskImage iv,
                                      final int maskSource) {
        if (uri == null) {
            iv.showDefaultImg(R.drawable.default_image, 200, maskSource);
        } else {
            String url;
            if (uri.indexOf("http:") == -1 && uri.indexOf("file:") == -1) {
                url = URLs.IMG_HEAD + "/" + uri;
            } else {
                url = uri;
            }
            ImageLoader.getInstance().loadImage(url, optionsOfBigImg,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            super.onLoadingStarted(imageUri, view);
                            iv.showDefaultImg(R.drawable.default_img_1, 200,
                                    maskSource);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            if (loadedImage != null) {
                                iv.show(loadedImage, 200, maskSource);
                                ImageCache.getInstance().put(uri, loadedImage);
                            } else {
                                iv.showDefaultImg(R.drawable.default_img_1,
                                        200, maskSource);
                            }
                        }
                    });
        }
    }


    /**
     * 显示图片
     *
     * @param uri
     * @param iv
     * @param options
     */

    public static void displayImg(final String uri, final ImageView iv,
                                  final DisplayImageOptions options) {
//		if (uri==null||"".equals(uri)) {
//			return;
//		}
        ImageAware imageAware = new ImageViewAware(iv, false);
        ImageLoader.getInstance().displayImage(uri, imageAware, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        if(loadedImage==null){
                            return;
                        }
                        int width = loadedImage.getWidth();

//						ViewGroup.LayoutParams params = iv.getLayoutParams();
//						params.width = ScreenUtils.SCREEN_WIDTH;
//						params.height = width*ScreenUtils.SCREEN_HEIGHT/ScreenUtils.SCREEN_WIDTH;
//						iv.setLayoutParams(params);
                        iv.setImageBitmap(loadedImage);
                        ImageCache.getInstance().put(uri, loadedImage);
                    }
                });
    }

    public static void displayNetImg(final String uri, final ImageView iv) {
        if (DataUtil.isEmpty(uri)) {
            return;
        }
        String url;
        if (uri.indexOf("http:") == -1 && uri.indexOf("file:") == -1) {
            url = URLs.IMG_HEAD + "/" + uri;
        } else {
            url = uri;
        }
        displayImg(url, iv, optionsOfBigImg);
    }
    public static void displayImg(final String uri, final ImageView iv) {
        if (DataUtil.isEmpty(uri)) {
            return;
        }
        String url;
        if (uri.indexOf("http:") == -1 && uri.indexOf("file:") == -1) {
            url = URLs.IMG_HEAD + "/" + uri;
        } else {
            url = uri;
        }
        displayImg(url, iv, localImgOptions);
    }

    public static void displayNetImg(final String uri, final ImageView iv, SimpleImageLoadingListener listener) {
        String url;
        if (uri.indexOf("http:") == -1 && uri.indexOf("file:") == -1) {
            url = URLs.IMG_HEAD + "/" + uri;
        } else {
            url = uri;
        }

        ImageAware imageAware = new ImageViewAware(iv, false);
        ImageLoader.getInstance().displayImage(url, imageAware, localImgOptions, listener);
    }

    /**
     * 显示本地图片
     *
     * @param thumbnailPath
     * @param path
     * @param iv
     */
    public static void displayLocalImg(final String thumbnailPath,
                                       final String path, final ImageView iv) {
        if (DataUtil.isEmpty(thumbnailPath) && DataUtil.isEmpty(path)) {
            iv.setImageResource(R.drawable.default_image);
        } else {
            ImageLoader.getInstance().displayImage("file://" + thumbnailPath,
                    iv, localImgOptions, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            if (loadedImage != null){
                                iv.setImageBitmap(loadedImage);
                                ImageCache.getInstance().put(imageUri, loadedImage);
                            }
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            super.onLoadingFailed(imageUri, view, failReason);
                            displayImg("file://" + path, iv, localImgOptions);
                        }
                    });
        }
    }

    //
    public static void displayHeadImg(String uri, final ImageView iv) {

        if (DataUtil.isEmpty(uri)) {
            iv.setImageResource(R.drawable.pic_default_head);
            return;
        }

        String url;
        if (uri.indexOf("http:") == -1 && uri.indexOf("file:") == -1) {
            url = URLs.IMG_HEAD + "/" + uri;
        } else {
            url = uri;
        }

        ImageLoader.getInstance().displayImage(url, iv, headImgOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri,
                                          View view, Bitmap loadedImage) {
                if (loadedImage != null)
                    iv.setImageBitmap(loadedImage);
            }
        });
    }

    // ####################从本地选择图片#####################

    /**
     * 从本地选择图片意图
     *
     * @return
     */
    public static Intent getSelectPicIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        }
        return intent;
    }

    /**
     * 获取拍照的意图
     *
     * @param photoPath
     * @return
     */
    public static Intent getTakePicIntent(String photoPath) {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(photoPath)));
        return intentCamera;
    }
}
