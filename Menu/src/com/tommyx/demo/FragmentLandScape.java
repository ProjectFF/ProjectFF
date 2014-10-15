package com.tommyx.demo;

import java.util.HashMap;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.tommyx.demos.R;

import rajawali.Camera;
import rajawali.Object3D;
import rajawali.animation.ColorAnimation3D;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.EllipticalOrbitAnimation3D;
import rajawali.animation.RotateAnimation3D;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.materials.textures.ATexture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.CubeMapTexture;
import rajawali.materials.textures.SphereMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.VideoTexture;
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
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;
import android.graphics.Bitmap;
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
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			((FragmentRenderer) mRenderer).getObjectAt(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
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
		private PostProcessingManager mPostProcessingManager;
		
		Object3D clouds[];
		
		float xpos, ypos, xd, yd; 
		
		float timer;
		
		MediaPlayer sound1; 
		EllipticalOrbitAnimation3D anim;
		Object3D nullObject = new Object3D();
		float rotate = 0;
		boolean pickedObject = false;
		
		Camera cam = new Camera();
		 
		private ObjectColorPicker mPicker;
		private boolean cloudsEnabled = true;
		
		public FragmentRenderer(Context main) {
			super(main);
			
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

		public void rotateCamera(float x, float y){
			
			if (nullObject instanceof Object3D){
				nullObject.setRotX(xd+(y/8));
				nullObject.setRotY(yd+(x/8));
				getCurrentCamera().setRotY(xd+(x/2));
			}else {
				getCurrentCamera().setRotY(xd+(x/2));
				
				
			}
		}
		
		public void onObjectPicked(Object3D object) {
		}
		
		@Override
		protected void initScene() {
	
			super.initScene();
			mPicker = new ObjectColorPicker(this);
			mPicker.setOnObjectPickedListener(this);
			
			mLight = new DirectionalLight(0,1,0); // set the direction
			mLight.setPosition(0,5,5);
			mLight.setColor(1.0f, 1.0f, 1.0f);
			mLight.setDirection(0, 0, 0);
			mLight.setPower(1.5f);
			
			getCurrentScene().addLight(mLight);
			getCurrentScene().setBackgroundColor(0xffffffff);
			getCurrentCamera().setPosition(0, 0.1f, 50);
			
			mPostProcessingManager = new PostProcessingManager(this);
			
			MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x000000, 0xffffff, BlendMode.ADD);
			getCurrentCamera().setFarPlane(1000);
			mPostProcessingManager.addEffect(bloomEffect);
			bloomEffect.setRenderToScreen(true);
			
			createSky("open");
			createClouds(10);
			createLandScape();
			
			getCurrentCamera().setLookAt(0,0,0);
			
			EllipticalOrbitAnimation3D anim = new EllipticalOrbitAnimation3D(
											new Vector3(0, .1, 0), 
											new Vector3(0, .1, 10), 0, 359);

			anim.setRepeatMode(RepeatMode.INFINITE);
			anim.setDurationMilliseconds(100000);
			anim.setTransformable3D(getCurrentCamera());
			getCurrentScene().registerAnimation(anim);
		//	anim.play();
			
		}
		
		public void createLandScape(){
			
		}
		
		private void createClouds(int num){

		    Plane cloud = new Plane(1,1,1,1);
	        cloud.setDoubleSided(true);
	        cloud.setTransparent(true);
	        // cloud.setBlendFunc(GL10.GL_SRC_ALPHA_SATURATE, GL10.GL_ONE_MINUS_SRC_ALPHA); //Night Sky dark clouds
	        cloud.setBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_DST_ALPHA);
	        
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
	        	clouds[i].setColor(0xffffff);
	        	float scale = 100;
	        	
	        	clouds[i].setPosition(-100 + Math.random()*200, -5+i*20, -900 + Math.random()*900);
	        	clouds[i].setRotation(0, Math.random() * (float) Math.PI,90);
	        	clouds[i].setScale(scale,scale,scale);

	        	getCurrentScene().addChild(clouds[i]);
	        }
	    }
		
		private void createSky(String skyname){
			
			
			int [] resourceIds = { 	R.drawable.open_posx,
									R.drawable.open_negx,
									R.drawable.open_posy,
									R.drawable.open_negy,
									R.drawable.open_posz,
									R.drawable.open_negz
								   };
			
			CubeMapTexture m = new CubeMapTexture("skymap", resourceIds);
			
			Material qm = new Material();
			Cube sky = new Cube(999); 
			sky.setDoubleSided(true);
			m.isSkyTexture(true);
			sky.setY(-250);
			sky.setRotY(-180);
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
			rotate=0.5f;
			
			if(cloudsEnabled ){
				for (Object3D i : clouds){
					
					if (i.getZ() > 100){ i.setZ(i.getZ() - 200);}
					float position = (float) i.getZ() + rotate;
					i.setZ(position);
					i.setY(position);
				}
			}
		}
		
		@Override
		public void onRender(final double deltaTime) {
			mPostProcessingManager.render(deltaTime);
			super.onRender(deltaTime);
		}

	}
		
}
