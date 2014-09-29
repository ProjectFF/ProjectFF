package com.tommyx.sacredgeometrydemo;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import rajawali.Object3D;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.ColorAnimation3D;
import rajawali.animation.EllipticalOrbitAnimation3D;
import rajawali.animation.RotateAnimation3D;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.CubeMapTexture;
import rajawali.materials.textures.SphereMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.math.vector.Vector2;
import rajawali.math.vector.Vector3;
import rajawali.parser.Loader3DSMax;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.os.Bundle;

public class Fragment2 extends AFragment implements OnTouchListener {

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
		Plane fruit[];
		Sphere fruit3d[];
		
		float xpos, ypos, xd, yd; 
		
		Material onMaterial;
		Material offMaterial;
		
		float timer;
		int numObjects = 12;
		
		int animcount = 300;
		int linecounter = 0;
		boolean counterset = false;
		int counter = 0;
		int devisor = 5;
		int duration = 600;
		MediaPlayer sound1; 
		EllipticalOrbitAnimation3D anim;
		Object3D nullObject;
		
		boolean pickedObject = false;
		
		Vector2[] points2D = new Vector2[13];
		Vector3[] points3D = new Vector3[13];
		
		private ObjectColorPicker mPicker;
		private Object currentObject;
		
		public FragmentRenderer(Context main) {
			super(main);
			
		}
	
		public void getObjectAt(float x, float y) {
			xpos = x;
			ypos = y;
			mPicker.getObjectAt(x, y);
			pickedObject = false;
		}

		public void onObjectPicked(Object3D object) {
			
			pickedObject = true; 
			
			if (object.getMaterial() == onMaterial ) {
				object.setMaterial(offMaterial);
			}else{
				object.setMaterial(onMaterial);
			}
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
			getCurrentCamera().setPosition(0,.1,20);
			getCurrentScene().setBackgroundColor(0x000000);
			
			mPostProcessingManager = new PostProcessingManager(this);
	
			MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x222222, 0xeeeeee, BlendMode.ADD);
			getCurrentCamera().setFarPlane(1000);
			mPostProcessingManager.addEffect(bloomEffect);
			
			bloomEffect.setRenderToScreen(true);
	
			getCurrentCamera().setLookAt(0,0,0); 
			
			createFruit3d();
			
//			anim = new EllipticalOrbitAnimation3D(
//					  new Vector3(0, 0, 0), 
//					  new Vector3(0, .1, 12), 0, 359);
//
//
//			anim.setRepeatMode(RepeatMode.INFINITE);
//			anim.setDurationMilliseconds(10000);
//			anim.setTransformable3D(getCurrentCamera());
//			getCurrentScene().registerAnimation(anim);
//			anim.play();
						
		}
		
		
		public void animate(Object3D o, int duration){
			
			ColorAnimation3D anim3 = new ColorAnimation3D(0x00000000, 0xFF000000);
			anim3.setDurationMilliseconds(duration);
			anim3.setRepeatMode(RepeatMode.NONE);
			anim3.setTransformable3D(o);
			getCurrentScene().registerAnimation(anim3);
			anim3.play();
			
			ColorAnimation3D anim2 = new ColorAnimation3D(0xff000000, 0x00000000);
			anim2.setDurationMilliseconds(duration);
			anim2.setDelayMilliseconds(duration);
			anim2.setRepeatMode(RepeatMode.NONE);
			anim2.setTransformable3D(o);
			getCurrentScene().registerAnimation(anim2);
			anim2.play();
			
		}
		
		public void rotateCamera(float x, float y){
			if (!pickedObject){
			yd = - y/8 - ypos  ;
			xd = - x/8 - xpos;
			nullObject.setRotX(xd);
			nullObject.setRotY(yd);
			}
			
		}
		
		private void createFruit(){
			numObjects = 13;
			fruit = new Plane[numObjects];
			Material m = new Material();
			m.setColorInfluence(0);
			
			try{
				m.addTexture(new Texture("ring", R.drawable.circle));
			}catch(TextureException e){
				e.printStackTrace();
			}
			
			float xcount = 0;
			float ycount = 2;
			
			for (int i=0; i< numObjects; i++){
				fruit[i] = new Plane(1,1,10,10); 
				fruit[i].setDoubleSided(true);
				fruit[i].setMaterial(m);
				fruit[i].setPosition(xcount,ycount, 0);
				fruit[i].setTransparent(true);
				getCurrentScene().addChild(fruit[i]);
				//fruit[i].setVisible(false);
			
			if (i>=0 && i<4){
					ycount-=1;
					xcount=0;
				}
				if (i==4){ xcount=-2;ycount= 1;}
				if (i==5){ xcount= 2;ycount= 1;}
				if (i==6){ xcount=-1;ycount=.5f;}
				if (i==7){ xcount= 1;ycount=.5f;}
				if (i==8){ xcount=-1;ycount=-.5f;}
				if (i==9){ xcount= 1;ycount=-.5f;}
				if (i==10){ xcount=-1;ycount=-1;}
				if (i==11){ xcount= 1;ycount=-1;}
				if (i==10){ xcount=-2;ycount=-1;}
				if (i==11){ xcount= 2;ycount=-1;}
				
				
			}			
			numObjects = fruit.length-1;
		}
		
		private void drawLines(String type){
			
			if (type == "cube")
			{
				
				
				
			}
			
		}
		
		private void createFruit3d(){
			numObjects = 30;
			fruit3d = new Sphere[numObjects];

			onMaterial = new Material();
			onMaterial.setColorInfluence(0.5f);
			onMaterial.enableLighting(true);
			onMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
			
			offMaterial = new Material();
			offMaterial.setColorInfluence(0.0f);
			offMaterial.enableLighting(true);
			offMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
			
			Material n = new Material();
			
			try {
				int[] cubemaps = new int[6];
				cubemaps[0] = R.drawable.posy;
				cubemaps[1] = R.drawable.negy;
				cubemaps[2] = R.drawable.negz;
				cubemaps[3] = R.drawable.posy;
				cubemaps[4] = R.drawable.posy;
				cubemaps[5] = R.drawable.posy;
				CubeMapTexture texture = new CubeMapTexture("cubemaps", cubemaps);
//				SphereMapTexture texture = new SphereMapTexture("map",R.drawable.spheremap);
				texture.isEnvironmentTexture(true);
				texture.setInfluence(1.f);
				Texture t = new Texture("sphere", R.drawable.sphere);
				t.setInfluence(.55f);
				onMaterial.addTexture(t);
				onMaterial.addTexture(texture);
				offMaterial.addTexture(t);
				
			} catch (TextureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
			
			float xcount = 0;
			float ycount = 0;
			float zcount = 0;
			
			nullObject = new Object3D();
			nullObject.setMaterial(n);
			
			for (int i=0; i< numObjects; i++){
				
				if (i==0){ xcount=  0; ycount=  6;}
				if (i==1){ xcount=  0; ycount= -6;}
				if (i==2){ xcount= -6; ycount=  0;}
				if (i==3){ xcount=  6; ycount=  0;}
				if (i==4){ xcount=  0; ycount=  0; zcount = 6;}
				if (i==5){ xcount=  0; ycount=  0; zcount =-6;}
				if (i==6){ xcount= -2; ycount=  4; zcount =  2; }
				if (i==7){ xcount=  2; ycount=  4; zcount =  2; }
				if (i==8){ xcount= -2; ycount=  4; zcount = -2; }
				if (i==9){ xcount=  2; ycount=  4; zcount = -2; }
				if (i==10){ xcount= -2; ycount=  -4; zcount =  2; }
				if (i==11){ xcount=  2; ycount=  -4; zcount =  2; }
				if (i==12){ xcount= -2; ycount=  -4; zcount = -2; }
				if (i==13){ xcount=  2; ycount=  -4; zcount = -2; }
				if (i==14){ xcount= -2; ycount=  2; zcount =  4; }
				if (i==15){ xcount=  2; ycount=  2; zcount =  4; }
				if (i==16){ xcount= -2; ycount=  2; zcount = -4; }
				if (i==17){ xcount=  2; ycount=  2; zcount = -4; }
				if (i==18){ xcount= -2; ycount=  -2; zcount =  4; }
				if (i==19){ xcount=  2; ycount=  -2; zcount =  4; }
				if (i==20){ xcount= -2; ycount=  -2; zcount = -4; }
				if (i==21){ xcount=  2; ycount=  -2; zcount = -4; }
				if (i==22){ xcount= -4; ycount=  2; zcount =  2; }
				if (i==23){ xcount=  4; ycount=  2; zcount =  2; }
				if (i==24){ xcount= -4; ycount=  2; zcount = -2; }
				if (i==25){ xcount=  4; ycount=  2; zcount = -2; }
				if (i==26){ xcount= -4; ycount=  -2; zcount =  2; }
				if (i==27){ xcount=  4; ycount=  -2; zcount =  2; }
				if (i==28){ xcount= -4; ycount=  -2; zcount = -2; }
				if (i==29){ xcount=  4; ycount=  -2; zcount = -2; }
				
				
				fruit3d[i] = new Sphere(1f,30,30); 
				fruit3d[i].setColor((int)(Math.random() * 0xffffff)); 
				fruit3d[i].setDoubleSided(true);
				fruit3d[i].setMaterial(onMaterial);
				fruit3d[i].setScale(1f);
				fruit3d[i].setTransparent(true);
				fruit3d[i].setBlendingEnabled(true);
				fruit3d[i].setPosition(xcount,ycount,zcount); 
				fruit3d[i].setBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_COLOR);
				
				mPicker.registerObject(fruit3d[i]);
				nullObject.addChild(fruit3d[i]);
				getCurrentScene().addChild(fruit3d[i]);
				
			}	
		
			numObjects = fruit3d.length-1;
		}
			
		@Override
		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			
			
			
//			nullObject.setRotY(xd);
//			nullObject.setRotX(yd);
//			
			if (timer > 0 && timer < animcount && counter != numObjects){
			//	if (timer % 1 == 0) 
				if (timer % devisor ==0 ){
							
					if (counter < numObjects && linecounter == 0) 
					{
						counter+=1;
					}
					
					if ( linecounter == 1)
					{ 
						
					}
				}
				
				
			}
			
			if (counterset && linecounter == 1){
				counterset = false;
				//getCurrentScene().setBackgroundColor(0);
//				for (int i=0;i<numObjects+1;i++)getCurrentScene().removeChild(fruit[i]);
//				getCurrentScene().unregisterAnimation(anim);
//				createFruit3d();
								
//				RotateOnAxisAnimation anim2 = new RotateOnAxisAnimation(new Vector3(0,1,0) , 360);
//				anim2.setRepeatMode(RepeatMode.INFINITE);
//				anim2.setDurationMilliseconds(10000);
//				anim2.setTransformable3D(nullObject);
//				getCurrentScene().registerAnimation(anim2);
//				anim2.play();
//				animcount = 600;
			}
//			
//			if (counterset && linecounter == 2){
//				counterset = false;
//				//getCurrentScene().setBackgroundColor(0);
//				//for (int i=0;i<numObjects+1;i++)getCurrentScene().removeChild(fruit3d[i]);
//				getCurrentScene().removeChild(nullObject);
//				getCurrentCamera().setPosition(0, .1, 7);
//				createFruit();
//				drawlines("Cube");
//			}
			
			if (timer == animcount){
				counter = 0;
				linecounter +=1;
				timer = 0;
				counterset = true;
			}
			timer +=1;
			
			
		}
		
		@Override
		public void onRender(final double deltaTime) {
			super.onRender(deltaTime);
			
			//mPostProcessingManager.render(deltaTime);
		}

	}
		
}
