package com.hhj.android.fill;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/* Han Hyeonju */

public class CameraSurfaceView extends SurfaceView implements Callback {

	public static final String TAG = "CameraSurfaceView";

	private Activity mActivity;
	private SurfaceHolder mHolder;
    private Camera mCamera = null;

	public CameraSurfaceView(Context context, Activity activity) {
		super(context);

		mActivity = activity;

		mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		mCamera.setDisplayOrientation(getDegrees());
		mCamera.startPreview();
	}

	// 단말기 카메라 화면에 맞지 않는 카메라 화면의 방향을 맞추기 위한 메서드.
	private final int getDegrees() {
		int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;

		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}

		int result = (90 - degrees + 360) % 360;
		return result;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		openCamera();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		stopPreview();
	}

	public Surface getSurface() {
		return mHolder.getSurface();
	}

	public boolean capture(Camera.PictureCallback jpegHandler) {
        if (mCamera != null) {
        	mCamera.takePicture(null, null, jpegHandler);
            return true;
        } else {
            return false;
        }
    }

	public void stopPreview() {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	public void startPreview() {
		openCamera();
		mCamera.startPreview();
	}

	public void openCamera() {
		mCamera = Camera.open();
        try {
        	mCamera.setPreviewDisplay(mHolder);
        } catch (Exception ex) {
            Log.e(TAG, "Failed to set camera preview display", ex);
        }
	}
}
