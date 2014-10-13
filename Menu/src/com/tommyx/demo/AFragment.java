package com.tommyx.demo;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rajawali.RajawaliFragment;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.RajLog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.tommyx.demos.R;

public abstract class AFragment extends RajawaliFragment{

	public static final String BUNDLE_EXAMPLE_URL = "BUNDLE_EXAMPLE_URL";

	protected RajawaliRenderer mRenderer;
	protected ProgressBar mProgressBarLoader;
	protected String mExampleUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isTransparentSurfaceView())
			setGLBackgroundTransparent(true);

		mRenderer = createRenderer();
		if (mRenderer == null)
			mRenderer = new NullRenderer(getActivity());

		mRenderer.setSurfaceView(mSurfaceView);
		setRenderer(mRenderer);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLayout = (FrameLayout) inflater.inflate(R.layout.rajawali_fragment,
				container, false);

		mLayout.addView(mSurfaceView);

		return mLayout;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (mLayout != null)
			mLayout.removeView(mSurfaceView);
	}

	@Override
	public void onDestroy() {
		try {
			super.onDestroy();
		} catch (Exception e) {
		}
		mRenderer.onSurfaceDestroyed();
	}

	/**
	 * Create a renderer to be used by the fragment. Optionally null can be returned by fragments
	 * that do not intend to display a rendered scene. Returning null will cause a warning to be
	 * logged to the console in the event null is in error.
	 * 
	 * @return
	 */
	protected abstract ARenderer createRenderer();

	protected boolean isTransparentSurfaceView() {
		return false;
	}

	protected abstract class ARenderer extends RajawaliRenderer {

		public ARenderer(Context context) {
			super(context);
			setFrameRate(60);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			super.onSurfaceCreated(gl, config);
		}
	}

	private static final class NullRenderer extends RajawaliRenderer {

		public NullRenderer(Context context) {
			super(context);
			RajLog.w(this + ": Fragment created without renderer!");
		}

		public void onTouch(){
			
		}
		
		@Override
		public void onSurfaceDestroyed() {
			stopRendering();
		}
	}
}
