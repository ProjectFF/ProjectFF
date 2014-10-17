package com.projectff.main;

import java.util.Dictionary;

import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import rajawali.RajawaliActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenu;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem.OnSemiCircularRadialMenuPressed;

public class MainActivity extends RajawaliActivity {
	
	Renderer renderer;
	
	HashMap<String,View> buttons = new HashMap<String, View>();
	private SemiCircularRadialMenu mMenu;
	private List<SemiCircularRadialMenuItem> currentItems = new ArrayList<SemiCircularRadialMenuItem>();
	private List<String> currentItemNames = new ArrayList<String>();
	
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
		
		
		mMenu = (SemiCircularRadialMenu) findViewById(R.id.radial_menu);
		
		currentItemNames.add("attack");
		currentItemNames.add("magic");
		currentItemNames.add("item");
		
		createMenuItem(currentItemNames);
		
		
	}
	
	public void removeMenuItems(){
		mMenu.removeAllMenuItems();
	}
	
	public void createMenuItem(final List<String> names){
		
		for (String name : names){
				int drawable = getResources().getIdentifier("btn_"+name, "drawable", "com.projectff.main");
				currentItems.add(new SemiCircularRadialMenuItem(name, getResources().getDrawable(drawable), name));
		}
		
		for (final SemiCircularRadialMenuItem item : currentItems){
			mMenu.addMenuItem(item.getMenuID(), item);
			item.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() 
			{
				@Override
				public void onMenuItemPressed() {
					renderer.setAction(item.getMenuID());
				}
			});
		}
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
	
	
	@Override
    public boolean onTouchEvent(MotionEvent event) 
    {
		this.renderer.onTouch(event);
		return true;
    }
	
}
