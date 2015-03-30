package com.tommyx.demos;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import com.tommyx.demos.R;
import android.content.Context;

import rajawali.Camera;
import rajawali.Object3D;
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

public class FragmentNightMenu extends AFragment implements OnTouchListener {

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
		float position =0;
		int numObjects = 0;
		
		int animcount = 100;
		float maxY = 30; 
		float minY = -2;
				
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
		Random random = new Random();
		Object3D pickedObject;
		Sphere sky;
		
		float half_widht;
		float oldx, oldy;
		public FragmentRenderer(Context main) {
			super(main);
		}
		
		public void onFingerDown(float x, float y) {
			yd = (float) getCurrentCamera().getRotY();
			xd = (float) getCurrentCamera().getY();
		}
		
		public void onFingerUp(float x, float y){
			yd = (float) getCurrentCamera().getRotY();
			xd = (float) getCurrentCamera().getY();
		}
		
		public void onFingerMove(float x, float y){
			
			if (oldx != x){
				if (oldx > x) { 
					yd -= 1.5f; 
					ypos -= 1.5f;
					getCurrentCamera().setRotY(yd);
				}
				else 
				{
					yd+= 1.5f;
					ypos += 1.5f;
					getCurrentCamera().setRotY(yd);
				}
			}
			if (oldy != y){
				if (oldy > y) {
					if (xd > minY){
						xd -= 0.5f; 
						getCurrentCamera().setY(xd);
						getCurrentCamera().setLookAt(0,xd,0);
					}
					Log.d("posY", Double.toString(getCurrentCamera().getRotY()));
				}
				else 
				{
					if (xd < maxY){
						xd += 0.5f; 
						getCurrentCamera().setLookAt(0,xd,0);
						getCurrentCamera().setY(xd);
					}
				}
		}
			oldy = y;
			oldx = x;
		}
			
		@Override 
		public void onObjectPicked(Object3D object) {
			
			Log.d("Name", object.getName());
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
			createClouds(10);
			half_widht =  mViewportWidth / 2;
		}
		
		
		private void createClouds(int num){

		    Plane cloud = new Plane(1,1,1,1);
	        cloud.setDoubleSided(true);
	        cloud.setTransparent(true);
	       // cloud.setBlendFunc(GL10.GL_SRC_ALPHA_SATURATE, GL10.GL_ONE_MINUS_SRC_ALPHA); //Night Sky dark clouds
	       // cloud.setBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_DST_ALPHA); // bright Sky 
	        cloud.setBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_COLOR); // bright Sky
	        cloud.setRotation(90,0,90);
	        cloud.setPosition(0,20,-10);
	        Texture texture = new Texture("cloud", R.drawable.fog);
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
//	        	float hsv[] = new float[] { random.nextFloat(),0.0f,1.0f};
//	        	clouds[i].setColor(Color.HSVToColor(hsv));
	        	float scale = 500;
	        	
	        	clouds[i].setPosition(-500 + Math.random()*1000, -500+Math.random()*1000, -500 + Math.random()*250);
	        	clouds[i].setRotation(0,0, i*30* Math.random() * 100);
	        	clouds[i].setScale(scale,scale,scale);

	        	getCurrentScene().addChild(clouds[i]);
	        }
	    }
		
		private void createSky(String skyname){
			
			Texture m = new Texture("skymap", R.drawable.atmosphere_night);
			
			Material qm = new Material();
			sky = new Sphere(800,10,10); 
			sky.setDoubleSided(true);
			sky.setRotY(42);
			//sky.setRotX(23);
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
			float timer2 = 0.2f;
			float timer3 = 0.3f;
			rotate=0.01f;
			sky.setRotY(sky.getRotY()+rotate);
			if(cloudsEnabled ){
				
				for (Object3D i : clouds){
					
					if (i.getY() > 1000){ i.setY(i.getY() -2000);}
					if (i.getZ() < -250){
						position = (float) i.getY() + timer3;
					}
					if (i.getZ() > -250 && i.getZ() < -150){								
						position = (float) i.getY() + timer2;
					}
					if (i.getZ() > -150){								
						position = (float) i.getY() + timer;
					}
					i.setY(position);
					i.setRotZ(rotate*10);
					
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
