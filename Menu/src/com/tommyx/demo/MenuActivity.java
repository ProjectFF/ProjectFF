package com.tommyx.demo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.tommyx.demos.R;

import rajawali.RajawaliActivity;

public class MenuActivity extends RajawaliActivity {

public boolean fragmentFinished = false;
	
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.rajawali_fragment);
	        launchFragment(new FragmentMenu());
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
