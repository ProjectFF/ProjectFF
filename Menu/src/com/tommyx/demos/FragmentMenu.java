package com.tommyx.demos;

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
import rajawali.extras.LensFlare;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.plugins.FogMaterialPlugin.FogParams;
import rajawali.materials.plugins.FogMaterialPlugin.FogType;
import rajawali.materials.textures.ASingleTexture;
import rajawali.materials.textures.ATexture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.CubeMapTexture;
import rajawali.materials.textures.SphereMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.math.vector.Vector3;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.passes.RenderPass;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;
import rajawali.renderer.RenderTarget;
import rajawali.renderer.plugins.LensFlarePlugin;
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
			((FragmentRenderer) mRenderer).onFingerDown(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			((FragmentRenderer) mRenderer).onFingerMove(event.getX(),
					event.getY());
			break;
		case MotionEvent.ACTION_UP:
			((FragmentRenderer) mRenderer).onFingerUp(event.getX(),
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
		//Plane line0[];
		private boolean cloudsEnabled = true;
		Object3D clouds[];
		
		float timer, rotate = 0;
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
		
		Object3D pickedObject;
		Sphere sky;
		
		float half_widht;
		float oldx, oldy;
		public FragmentRenderer(Context main) {
			super(main);
		}
		
		public void onFingerDown(float x, float y) {
			
			if (nullObject.getNumChildren()>0){
				yd = (float) nullObject.getRotY();
				xd = (float) nullObject.getRotX();
			}
			mPicker.getObjectAt(x, y);
		}
		
		public void onFingerUp(float x, float y){
			yd = (float) nullObject.getRotY();
		}
			
		public void onFingerMove(float x, float y){
			Log.d("sky",Double.toString(sky.getRotY()));
			if (oldx != x){
				if (oldx > x) {
					yd += 1.5f; 
					nullObject.setRotY(yd);
				}
				else 
				{
					yd-= 1.5f;
					nullObject.setRotY(yd);
				}
			}
			if (oldy != y){
				if (oldy > y) {
					xd += 0.1f; 
					nullObject.setRotX(xd);
				}
				else 
				{
					xd-= 0.1f;
					nullObject.setRotX(xd);
				}
			}
			oldy = y;
			oldx = x;
			
		}
			
		@Override 
		public void onObjectPicked(Object3D object) {
			
			Log.d("Name", object.getName());
			
			if (object.getName() == "btn_ls_demo.png") 
				Log.d("HIT", "hit");
				((MenuActivity) mContext).launchFragment(new FragmentLandScape());
			if (object.getName() == "btn_rs_demo.png") 
				((MenuActivity) mContext).launchFragment(new FragmentLandScape());
			if (object.getName() == "btn_sc_demo.png") 
				((MenuActivity) mContext).launchFragment(new FragmentLandScape());
			if (object.getName() == "btn_ar_demo.png") 
				((MenuActivity) mContext).launchFragment(new FragmentLandScape());
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

			getCurrentCamera().setPosition(0, 1,50);
			getCurrentCamera().setLookAt(0, 0, 0);
			getCurrentCamera().setFarPlane(1000);
			
			getCurrentScene().addLight(mLight);
			getCurrentScene().setBackgroundColor(0x0000000);
			
			target = new PostProcessingManager(this);
			
			MyEffect bloomEffect   = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x000000, 0xffffff, BlendMode.ADD);
			target.addEffect(bloomEffect);
			bloomEffect.setRenderToScreen(true);
			
			//getCurrentScene().setFog(new FogParams(FogType.LINEAR, 0xffffff, 10, 800));
			
			createSky("open");
			createClouds(20);
			createScene();
			half_widht =  mViewportWidth / 2;
		}
		
		private void createScene(){
			float sFactor = 20;
			int numButtons = 4;
			int[] texts = new int[]{
				R.drawable.btn_sc_demo,
				R.drawable.btn_ls_demo,
				R.drawable.btn_rs_demo,
				R.drawable.btn_ar_demo,
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
			
			buttons[count] = new Plane(0.5f*sFactor,.5f*sFactor,1,1);
			
			Material m = new Material();
			
			buttons[count].setDoubleSided(true);
			buttons[count].setTransparent(true);
			//m.enableLighting(true);
			//m.setDiffuseMethod(new DiffuseMethod.Lambert());
			m.setColorInfluence(0.0f);
			
			try{
				m.addTexture(new Texture("text",texts[count]));
				m.addTexture(new Texture("glowmap",R.drawable.btn_back));
			}catch(TextureException e){
				e.printStackTrace();
			}
			
				String id = getContext().getResources().getString(texts[count]);
				String[] subs = id.split("/");
				for (String s: subs)
					id = s;
				buttons[count].setName(id);
				Log.d("NAMES", buttons[count].getName());
				buttons[count].setColor(0xffffff);
				buttons[count].setMaterial(m);
				buttons[count].setRotation(0,-270+i,0);
				buttons[count].setPosition(posx*sFactor, -8f, posz*sFactor);
				nullObject.addChild(buttons[count]);
			}
				nullObject.setRotX(25);
				for(int i = 0; i<nullObject.getNumChildren(); i++){
					mPicker.registerObject(nullObject.getChildAt(i));
					//getCurrentScene().addChild(nullObject.getChildAt(i));	
				}
				getCurrentScene().addChild(nullObject);
				
		}
		
		public void createLensFlares(){
			
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
			
		
		private void createClouds(int num){

		    Plane cloud = new Plane(1,1,1,1);
	        cloud.setDoubleSided(true);
	        cloud.setTransparent(true);
	        // cloud.setBlendFunc(GL10.GL_SRC_ALPHA_SATURATE, GL10.GL_ONE_MINUS_SRC_ALPHA); //Night Sky dark clouds
	        cloud.setBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_DST_ALPHA); // bright Sky 
	        
	        cloud.setRotation(90,0,90);
	        cloud.setPosition(0,20,-10);
	        Texture texture = new Texture("cloud", R.drawable.cloud2);
	        Material cloudMat = new Material(); 
	        cloudMat.setColorInfluence(.50f);
	        cloudMat.setAmbientIntensity(2, 2, 2);
	        
	        try{
	        	cloudMat.addTexture(texture);
	        }catch(TextureException t){
	        	t.printStackTrace();
	        }
	        
	        cloud.setMaterial(cloudMat);
	        clouds = new Object3D[num];
	        
	        for ( int i = 0; i < num; i++ ) {

	        	clouds[i] = cloud.clone();
	        	clouds[i].setDoubleSided(true);
	        	clouds[i].setColor(0xffffff);
	        	float scale = 200;
	        	
	        	clouds[i].setPosition(-250 + Math.random()*500, 50+Math.random()*100, -500 + Math.random()*1000);
	        	clouds[i].setRotation(0,0,  Math.random() * i);
	        	clouds[i].setScale(scale,scale,scale);

	        	getCurrentScene().addChild(clouds[i]);
	        }
	    }
		
		private void createSky(String skyname){
			
			Texture m = new Texture("skymap", R.drawable.atmosphere);
			
			Material qm = new Material();
			sky = new Sphere(800,10,10); 
			sky.setDoubleSided(true);
			sky.setRotY(257);
			sky.setRotX(23);
			qm.setColorInfluence(0);
			
			try{
				qm.addTexture(m);
			}catch(Exception e){
				
			}
			
			sky.setMaterial(qm);
			getCurrentScene().addChild(sky);
		}
		
		
		@Override
		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			timer=0.1f;
			rotate=0.01f;
			sky.setRotY(sky.getRotY()+rotate);
			if(cloudsEnabled ){
				
				for (Object3D i : clouds){
					
					if (i.getZ() > 100){ i.setZ(i.getZ() - -500);}
					float position = (float) i.getZ() + timer;
					i.setZ(position);
					//i.setY(position);
				}
			}
			
		}
		
		@Override
		public void onRender(final double deltaTime) {
			target.render(deltaTime);
			super.onRender(deltaTime);
		}
	}	
}
