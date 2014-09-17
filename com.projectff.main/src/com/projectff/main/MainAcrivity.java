package com.projectff.main;

import java.util.Dictionary;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import rajawali.RajawaliActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainAcrivity extends RajawaliActivity {
	
	Renderer renderer;
	
	HashMap<String,View> buttons = new HashMap<String, View>();
	FrameLayout ll;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu);
		mSurfaceView = (GLSurfaceView) findViewById(R.id.glSurface);
        mDeferGLSurfaceViewCreation = true;
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		renderer = new Renderer(this);
		if (mSurfaceView != null) {
			mSurfaceView.setEGLContextClientVersion(2);
			this.renderer.setSurfaceView(mSurfaceView);
			this.setRenderer(renderer);
		}
		getButtons();
		// mLayout.addView();
	}
	
	private static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
	    ArrayList<View> views = new ArrayList<View>();
	    final int childCount = root.getChildCount();
	    for (int i = 0; i < childCount; i++) {
	        final View child = root.getChildAt(i);
	        if (child instanceof ViewGroup) {
	            views.addAll(getViewsByTag((ViewGroup) child, tag));
	        }

	        final Object tagObj = child.getTag();
	        if (tagObj != null && tagObj.equals(tag)) {
	        	Log.d("tag", tagObj.toString());
		        views.add(child);
	        }

	    }
	    return views;
	}
	
	public void getButtons(){
		
		View v = findViewById(android.R.id.content);
		ViewGroup v1 =(ViewGroup) v;
		
		ArrayList<View> a = getViewsByTag(v1, "btn");
		
		for(View v2 : a ){
			
			Log.d("child", v2.toString());
			
		}
				
	}
			
	
	@Override
    public boolean onTouchEvent(MotionEvent event) 
    {
		this.renderer.onTouch(event);
		return true;
    }
	
}
