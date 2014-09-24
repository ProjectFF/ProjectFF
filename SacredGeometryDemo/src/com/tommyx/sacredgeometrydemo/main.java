package com.tommyx.sacredgeometrydemo;

import android.os.Bundle;
import rajawali.RajawaliActivity;
import rajawali.renderer.RajawaliRenderer;

public class main extends RajawaliActivity {

	IntroRenderer renderer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		renderer = new IntroRenderer(this);
		renderer.setSurfaceView(mSurfaceView);
		setRenderer(renderer);
	}
}
