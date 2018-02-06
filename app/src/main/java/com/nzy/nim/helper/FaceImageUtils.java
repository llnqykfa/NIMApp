package com.nzy.nim.helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.nzy.nim.db.FaceImage;
import com.nzy.nim.tool.common.DataUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/9/21.
 */
public class FaceImageUtils {
    public static FaceImageUtils faceImageUtils;
    private static List<FaceImage> list=new ArrayList<FaceImage>();
    private String[] p_name= {"p_emoji_31","p_emoji_32","p_emoji_33","p_emoji_34","p_emoji_35","p_emoji_36",
            "p_emoji_37","p_emoji_38","p_emoji_39","p_emoji_40","p_emoji_41","p_emoji_42","p_emoji_43",
            "p_emoji_44","p_emoji_45","p_emoji_46","p_emoji_47","p_emoji_48","p_emoji_49","p_emoji_50"
            ,"p_emoji_51","p_emoji_52","p_emoji_53","p_emoji_54","p_emoji_55","p_emoji_56","p_emoji_57"
            ,"p_emoji_58","p_emoji_59","p_emoji_60","p_emoji_61","p_emoji_62","p_emoji_63","p_emoji_64","p_emoji_65"
            ,"p_emoji_66","p_emoji_67","p_emoji_68","p_emoji_69","p_emoji_70","p_emoji_71","p_emoji_72"};
    public static FaceImageUtils getInstace() {
        if (faceImageUtils == null) {
            faceImageUtils = new FaceImageUtils();
        }
        return faceImageUtils;
    }
    public void saveFace(Context context){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "MyFile");
            DeleteFile(file);
        }else{
            File homeDir = new File(context.getFilesDir().getAbsolutePath()+ File.separator + "MyFile");
            DeleteFile(homeDir);
        }
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        for (int i = 0; i < p_name.length; i++) {
            int resID = context.getResources().getIdentifier(p_name[i], "drawable", applicationInfo.packageName);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resID);
            String fileName=UUID.randomUUID()+".png";
            String fileDstPath = saveBitMapToFile(context, fileName, bitmap);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            byte[] appicon = baos.toByteArray();// 转为byte数组
//            String s_pic = Base64.encodeToString(appicon, Base64.DEFAULT);
            if(!DataUtil.isEmpty(fileDstPath)){
                FaceImage faceImage = new FaceImage();
                faceImage.setFaceImage(fileDstPath);
                list.add(faceImage);
            }
        }
        if(!DataUtil.isEmpty(list)){
            DataSupport.deleteAll(FaceImage.class);
            DataSupport.saveAll(list);
        }

    }
    public static String saveBitMapToFile(Context context, String fileName, Bitmap bitmap) {
        if(null == context || null == bitmap) {
            return null;
        }
        if(TextUtils.isEmpty(fileName)) {
            return null;
        }
        FileOutputStream fOut = null;
        try {
            File file = null;
            String fileDstPath = "";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 保存到sd卡
                fileDstPath =Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "MyFile" + File.separator+ fileName;

                File homeDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "MyFile" + File.separator);
                if (!homeDir.exists()) {
                    homeDir.mkdirs();
                }
            } else {
                // 保存到file目录
                fileDstPath = context.getFilesDir().getAbsolutePath()
                        +  fileName;

                File homeDir = new File(context.getFilesDir().getAbsolutePath()
                        + File.separator + "MyFile" + File.separator);
                if (!homeDir.exists()) {
                    homeDir.mkdir();
                }
            }

            file = new File(fileDstPath);

            if (!file.exists()) {
                // 简单起见，先删除老文件，不管它是否存在。
                file.delete();

                fOut = new FileOutputStream(file);
                if (fileName.endsWith(".jpg")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fOut);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                }
                fOut.flush();
                bitmap.recycle();
            }
            Log.i("FileSave", "saveDrawableToFile " + fileName
                    + " success, save path is " + fileDstPath);
            return fileDstPath;
        } catch (Exception e) {
            Log.e("FileSave", "saveDrawableToFile: " + fileName + " , error", e);
            return null;
        } finally {
            if(null != fOut) {
                try {
                    fOut.close();
                } catch (Exception e) {
                    Log.e("FileSave", "saveDrawableToFile, close error", e);
                }
            }
        }
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file
     *            要删除的根目录
     */
    public void DeleteFile(File file) {
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f);
                }
                file.delete();
            }
        }
    }
}
