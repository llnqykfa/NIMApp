package com.nzy.nim.zxing;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.manager.ServerApplication;
import com.nzy.nim.zxing.camera.CameraManager;
import com.nzy.nim.zxing.decoding.CaptureActivityHandler;
import com.nzy.nim.zxing.decoding.InactivityTimer;
import com.nzy.nim.zxing.view.ViewfinderView;
import com.nzy.nim.R;

import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends BaseActivity implements Callback {

	private TextView topBarContent;
	private Button  topBarMore;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
    private boolean IS_CALLBACK = false;
    private boolean IS_ADDOLDBOOK = false;
//	private boolean QD_CODE=false;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        IS_CALLBACK = getIntent().getBooleanExtra("IS_CALLBACK",false);
		IS_ADDOLDBOOK = getIntent().getBooleanExtra("addoldbook",false);
//		QD_CODE = getIntent().getBooleanExtra("QD_CODE",false);
		setContentView(R.layout.activity_capture);
		initTopBar();
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		ImageView mButtonBack = (ImageView) findViewById(R.id.top_bar_back);
		mButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MipcaActivityCapture.this.finish();

            }
        });
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	private void initTopBar() {
		topBarContent = (TextView) findViewById(R.id.top_bar_content);
		topBarMore = (Button) findViewById(R.id.top_bar_next);
		topBarMore.setVisibility(View.INVISIBLE);
		topBarContent.setText(R.string.action_scan);
		findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 处理扫描结果
	 *
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (resultString.equals("")) {
			Toast.makeText(MipcaActivityCapture.this, "扫描失败",
					Toast.LENGTH_SHORT).show();
		} else {
			if(IS_ADDOLDBOOK){
				/*Intent intent = new Intent();
				intent.putExtra("add_ISBN",resultString);
				intent.setClass(MipcaActivityCapture.this, AddOldBookActivity.class);
				startActivity(intent);*/
			}else if(!IS_CALLBACK) {
//				if(!QD_CODE){
////				getDesString(resultString);
//				}
				Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("result", resultString);
				resultIntent.putExtras(bundle);
				this.setResult(RESULT_OK, resultIntent);
			} else {
				ServerApplication.getInstance().getScanResultHandlerSerilize().onResult(resultString);
			}
		}
		MipcaActivityCapture.this.finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	public static void actionIntent(Context context) {
		context.startActivity(new Intent(context, MipcaActivityCapture.class));
	}


    public abstract class ScanResultHandlerSerilize implements Serializable{
        public abstract void onResult(String result);
    }

}