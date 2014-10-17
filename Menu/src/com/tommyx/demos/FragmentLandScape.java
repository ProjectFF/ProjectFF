package com.tommyx.demos;

import java.util.Random;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.tommyx.demos.R;
import com.tommyx.demos.AFragment.ARenderer;
import com.tommyx.demos.FragmentMenu.FragmentRenderer;

import rajawali.Camera;
import rajawali.ChaseCamera;
import rajawali.Object3D;
import rajawali.animation.ColorAnimation3D;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.EllipticalOrbitAnimation3D;
import rajawali.animation.RotateAnimation3D;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.animation.SplineTranslateAnimation3D;
import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.curves.CatmullRomCurve3D;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.materials.plugins.FogMaterialPlugin.FogParams;
import rajawali.materials.plugins.FogMaterialPlugin.FogType;
import rajawali.materials.textures.ATexture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.CubeMapTexture;
import rajawali.materials.textures.NormalMapTexture;
import rajawali.materials.textures.SphereMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.VideoTexture;
import rajawali.math.MathUtil;
import rajawali.math.vector.Vector2;
import rajawali.math.vector.Vector3;
import rajawali.parser.Loader3DSMax;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.effects.ShadowEffect;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.postprocessing.passes.RenderPass;
import rajawali.primitives.Cube;
import rajawali.primitives.Line3D;
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;
import rajawali.scene.RajawaliScene;
import rajawali.terrain.SquareTerrain;
import rajawali.terrain.TerrainGenerator;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.os.Bundle;

public class FragmentLandScape extends AFragment implements OnTouchListener {

	
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
	
	public final class FragmentRenderer extends ARenderer 
	{

		DirectionalLight mLight, mLight2;
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
		Object3D flares[];
		
		Texture gTexture; 
		Object3D empty;
		Object3D world;
		 
		float xd,yd, xpos, ypos = 0;
		private SquareTerrain scenery;
	
		Object3D pickedObject;
		Sphere sky;
		
		float oldx, oldy;
		
		
		public FragmentRenderer(Context main) {
			super(main);
		}
		
		public void onFingerDown(float x, float y) {
			yd = (float) sky.getRotY();
			ypos = (float) world.getRotY();
			xd = (float) getCurrentCamera().getY();
		}
		
		public void onFingerUp(float x, float y){
			yd = (float) sky.getRotY();
			ypos = (float) world.getRotY();
			xd = (float) getCurrentCamera().getY();
		}
			
		public void onFingerMove(float x, float y){
			
			if (oldx != x){
				if (oldx > x) {
					yd -= 1.5f; 
					ypos -= 1.5f;
					sky.setRotY(yd);
					scenery.setRotY(yd);
					world.setRotY(ypos);
				}
				else 
				{
					yd+= 1.5f;
					ypos += 1.5f;
					sky.setRotY(yd);
					scenery.setRotY(yd);
					world.setRotY(ypos);
				}
			}
			if (oldy != y){
				if (oldy > y) {
					xd -= 0.5f; 
					getCurrentCamera().setY(xd);
					getCurrentCamera().setLookAt(0,xd,0);
					Log.d("posY", Double.toString(getCurrentCamera().getY()));
				}
				else 
				{
					xd+= 0.5f;
					getCurrentCamera().setLookAt(0,xd,0);
					getCurrentCamera().setY(xd);
				}
		}
			oldy = y;
			oldx = x;
			
		}
			
		@Override
		protected void initScene() {
	
			super.initScene();
			//mPicker = new ObjectColorPicker(this);
			// mPicker.setOnObjectPickedListener(this);
			
			world = new Object3D();
			empty = new Object3D();
			
			mLight = new DirectionalLight(0,0,0); // set the direction
			mLight.setPosition(0,300,-400);
			mLight.setColor(1.0f, 1.0f, 1.0f);
			mLight.setPower(.2f);

			mLight2 = new DirectionalLight(0,0, 0); // set the directio
			mLight.setDirection(1, -1, -1);
			mLight2.setPosition(0,300, 0);
			mLight2.setColor(1.0f, 1.0f, 1.0f);
			mLight2.setPower(2f);

			getCurrentCamera().setPosition(0, 1,250);
			getCurrentCamera().setLookAt(0, 0, 0);
			getCurrentCamera().setFarPlane(2000);

			getCurrentScene().addLight(mLight);
			getCurrentScene().addLight(mLight2);
			getCurrentScene().setBackgroundColor(0x0000000);
			
			target = new PostProcessingManager(this);
			
			createSky("open");
			createClouds(10); 
			createScene();
			createLensFlares();
			world.addChild(empty);
			getCurrentScene().addChild(world);
			
			
			MyShadowEffect shadowEffect = new MyShadowEffect(getCurrentScene(), getCurrentCamera(), mLight2, 1024);
			MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x4444444, 0x999999, BlendMode.ADD);
			shadowEffect.setShadowInfluence(.5f);
			target.addEffect(shadowEffect);
			//target.addEffect(bloomEffect);
			
			//bloomEffect.setRenderToScreen(true);
			shadowEffect.setRenderToScreen(true);
			
		}
		
		public void createScene(){
			
			Material qm = new Material(); 
			qm.setDiffuseMethod(new DiffuseMethod.Lambert());
			qm.enableLighting(true);	
			qm.setColorInfluence(0);
			
			Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.terrain);
			
			try {
				SquareTerrain.Parameters terrainParams = SquareTerrain.createParameters(bmp);
				terrainParams.setScale(4f, 50f, 4f);
				terrainParams.setDivisions(128);
				terrainParams.setTextureMult(1);
			
				terrainParams.setColorMapBitmap(bmp);
				
				scenery = TerrainGenerator.createSquareTerrainFromBitmap(terrainParams);
			
				scenery.setY(-50);
				qm.addTexture(new Texture("scenery", R.drawable.grass));
				//qm.addTexture(new NormalMapTexture("scenery2", R.drawable.grass_b));
					
				scenery.setDoubleSided(true);
				scenery.setMaterial(qm);
				getCurrentScene().addChild(scenery);
				
			}catch(Exception t) {
				t.printStackTrace();
			}
		}
		
		public void createLensFlares(){
			Random random = new Random();
			flares = new Object3D[1];
			empty.setPosition(0,0,-400);
			
			Material cubeMat = new Material();
			cubeMat.setColorInfluence(0.5f);
			
			Plane flare = new Plane(10,10,1,1);
			flare.setPosition(0,0,-400);
			flare.setTransparent(true);
			
			for (int i=0;i<flares.length;i++){
			
				flares[i] = flare.clone();
				flares[i].setPosition(0, 200, -400);
				flares[i].setTransparent(true);
				flares[i].setScale(50);
				flares[i].setDoubleSided(true);
				flares[i].setColor(0x666666 + random.nextInt(0x999999));
				Material flareMat = new Material();
				try{
					
					int id = getContext().getResources().getIdentifier("flare" + Integer.toString(i), "drawable", "com.tommyx.demos"); 
					Log.d("ID", "flare" + Integer.toString(i) + " - " + Integer.toString(id));
					
					Texture t = new Texture("flare"+Integer.toString(i), id);
					flareMat.addTexture(t);
				}catch(Exception e){}
				
				flares[i].setMaterial(flareMat);
				
				empty.addChild(flares[i]);
			}
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
	        	float scale = 400;
	        	
	        	clouds[i].setPosition(-800 + Math.random()*1600, 100+Math.random()*100, -800 + Math.random()*1600);
	        	clouds[i].setRotation(90,0,  Math.random() * i);
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
			sky.setY(-50);
			qm.setColorInfluence(0);
			
			try{
				qm.addTexture(m);
			}catch(Exception e){
				
			}
			
			sky.setMaterial(qm);
			// world.addChild(sky);
			getCurrentScene().addChild(sky);
			
		}
		
		@Override
		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			
			
		}
		
		@Override
		public void onRender(final double deltaTime) {
			target.render(deltaTime);
			timer=1.f;
			rotate=0.01f;
			if(cloudsEnabled ){
				mLight.setPosition(empty.getPosition());
				sky.setRotY(sky.getRotY()+rotate);
				for (Object3D i : clouds){
					
					if (i.getZ() > 100){ i.setZ(i.getZ() - 800);}
					float position = (float) i.getZ() + timer;
					i.setZ(position);
					//i.setY(position);
				}
			}
		}

	}
}