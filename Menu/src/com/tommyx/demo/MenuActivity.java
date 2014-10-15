package com.tommyx.demo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.tommyx.demos.R;

import rajawali.RajawaliActivity;

public class MenuActivity extends RajawaliActivity {

public boolean fragmentFinished = false;
	
	public void onCreate(Bundle savedInstanceState) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
        	super.onCreate(savedInstanceState);
	        setContentView(R.layout.rajawali_fragment);
	        launchFragment(new FragmentMenu());
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 			 			 WindowManager.LayoutParams.FLAG_FULLSCREEN);
      
	 }
	 
	 public void launchFragment(AFragment frag) {
		 	final FragmentManager fragmentManager = getFragmentManager();
			final FragmentTransaction transaction = fragmentManager.beginTransaction();

			try {
				frag.setArguments(getIntent().getExtras());

				transaction.replace(R.id.content_frame, frag);
				transaction.commit();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
	}
	 
	@Override
	protected void onDestroy() {
		try {
			super.onDestroy();
		} catch (Exception e) {
		}
	}
	
}
