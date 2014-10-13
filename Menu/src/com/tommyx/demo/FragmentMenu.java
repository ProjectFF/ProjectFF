package com.tommyx.demo;

import javax.microedition.khronos.opengles.GL10;

import com.tommyx.demos.R;
import android.content.Context;

import rajawali.Camera;
import rajawali.Object3D;
import rajawali.animation.AlphaAnimation3D;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.ColorAnimation3D;
import rajawali.animation.EllipticalOrbitAnimation3D;
import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.ATexture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.math.vector.Vector3;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.passes.RenderPass;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import rajawali.renderer.RenderTarget;
import rajawali.scene.RajawaliScene;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.media.MediaPlayer;
import android.os.Bundle;

public class FragmentMenu extends AFragment implements OnTouchListener {

	@Override
	protected ARenderer createRenderer() {
		return new FragmentRenderer(getActivity());
	}
	
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			((FragmentRenderer) mRenderer).getObjectAt(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("sdfa", Float.toString(event.getX()));
			((FragmentRenderer) mRenderer).rotateCamera(event.getX(),
					event.getY());
			break;
		default :
			break;
		}
		return true;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mSurfaceView.setOnTouchListener(this);
		return mLayout;
	}
	public final class FragmentRenderer extends ARenderer implements OnObjectPickedListener {

		DirectionalLight mLight;
		private PostProcessingManager target;
		private PostProcessingManager target2;
		Plane line0[];
		
		float timer;
		int numObjects = 0;
		
		int animcount = 100;
		int linecounter = 0;
		boolean counterset = false;
		int counter = 0;
		int devisor = 5;
		int duration = 600;
		MediaPlayer sound1; 
		RajawaliScene glowScene, liveScene;
		Plane buttons[];
		Texture gTexture; 
		Object3D nullObject;
		float xd,yd, xpos, ypos = 0;
		private ObjectColorPicker mPicker;
		
		
		public FragmentRenderer(Context main) {
			super(main);
		}
		
		public void rotateCamera(float x, float y){
			//yd = - y/8 - ypos;
			//xd = - x/8 - xpos;
			
			nullObject.setRotY(yd+(-x/8));
			
		}
		
		public void onObjectPicked(Object3D object) {
		}
	
		public void getObjectAt(float x, float y) {
			xpos = x;
			ypos = y;
			
			if (nullObject.getNumChildren()>0){
				yd = (float) nullObject.getRotX();
				xd = (float) nullObject.getRotY();
			}
			mPicker.getObjectAt(x, y);
		}
		
		@Override
		protected void initScene() {
	
			super.initScene();
			mPicker = new ObjectColorPicker(this);
			mPicker.setOnObjectPickedListener(this);
			
			mLight = new DirectionalLight(0,0,0); // set the direction
			mLight.setPosition(0,5,0);
			mLight.setColor(1.0f, 1.0f, 1.0f);
			mLight.setPower(5f);

			getCurrentCamera().setPosition(0, 1,5);
			getCurrentCamera().setLookAt(0, 0, 0);
			
			getCurrentScene().addLight(mLight);
			getCurrentScene().setBackgroundColor(0xffffff);
			
			target = new PostProcessingManager(this);
			
			MyEffect bloomEffect   = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x000000, 0xffffff, BlendMode.ADD);
			target.addEffect(bloomEffect);
			bloomEffect.setRenderToScreen(true);
			
			createScene();
			
		}
		
		private void createScene(){
			
			int numButtons = 8;
			int[] texts = new int[]{
				R.drawable.btn_sc_demo,
				R.drawable.btn_sc_demo,
				R.drawable.btn_sc_demo,
				R.drawable.btn_sc_demo,
				R.drawable.btn_sc_demo,
				R.drawable.btn_sc_demo,
				R.drawable.btn_sc_demo,
				R.drawable.btn_sc_demo
			};
			
 			
			
			
			buttons = new Plane[numButtons];
			nullObject = new Object3D();
			nullObject.setDoubleSided(true);
			Material nullMat = new Material();
			nullObject.setMaterial(nullMat);
			
			int count = 0; 
			
			for (int i=0;i<360; i+=360/numButtons){
			
			double posx = Math.cos(Math.toRadians(i));
			double posz = Math.sin(Math.toRadians(i));
				
			if (i!=0) count = i/90;
			
			Log.d("I" , Integer.toString(i));
			Log.d("count" , Integer.toString(count));
			Log.d("posx" , Double.toString(posx));
			Log.d("posy" , Double.toString(posz));
			
			buttons[count] = new Plane(0.5f,.5f,1,1);
			
			Material m = new Material();
			
			buttons[count].setDoubleSided(true);
			buttons[count].setTransparent(true);
			m.enableLighting(true);
			m.setDiffuseMethod(new DiffuseMethod.Lambert());
			m.setColorInfluence(0);
			
			try{
				m.addTexture(new Texture("text",texts[count]));
				m.addTexture(new Texture("glowmap",R.drawable.btn_back));
			}catch(TextureException e){
				e.printStackTrace();
			}
			
				buttons[count].setMaterial(m);
				buttons[count].setRotation(0,-270+i,0);
				buttons[count].setPosition(posx, 0, posz);
				nullObject.addChild(buttons[count]);
			}
			nullObject.setScale(2);
			getCurrentScene().addChild(nullObject);
		}
		
		public Bitmap textAsBitmap(String text) 
		{
			Bitmap mScoreBitmap = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
			
			Canvas mScoreCanvas = new Canvas(mScoreBitmap);
			Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mTextPaint.setColor(Color.WHITE);
			mTextPaint.setTextSize(50);
			mTextPaint.setTypeface(Typeface.MONOSPACE);
			
			mScoreCanvas.drawColor(0, Mode.CLEAR);
			
			mScoreCanvas.drawText(text, 80,
					148, mTextPaint);
			
			return mScoreBitmap;
	    }
			
		@Override
		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
		}
		
		@Override
		public void onRender(final double deltaTime) {
			target.render(deltaTime);
			super.onRender(deltaTime);
		}
	}	
}
