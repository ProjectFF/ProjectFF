package com.projectff.main;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import rajawali.RajawaliActivity;

public class MainAcrivity extends RajawaliActivity {
	
	Renderer renderer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		renderer = new Renderer(this);
		this.renderer.setSurfaceView(mSurfaceView);
		this.setRenderer(renderer);
	
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		
		renderer.onKeyDown(keyCode, event);
		
		return true;
		
	};

	@Override
    public boolean onTouchEvent(MotionEvent event) 
    {
		this.renderer.onTouch(event);
		return true;
    }
	
}
