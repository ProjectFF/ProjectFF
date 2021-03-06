package com.tommyx.sacredgeometrydemo;

import java.util.HashMap;
import java.util.Stack;

import com.tommyx.sacredgeometrydemo.Tools;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import rajawali.Camera;
import rajawali.Object3D;
import rajawali.animation.ColorAnimation3D;
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
		private PostProcessingManager mPostProcessingManager;
		
		Sphere objects3D[];
		Plane  objects2D[];
		Line3D objects1D[];
		Plane mirrors[]; 
		
		
		Tools tools = new Tools();
		
		Stack<Vector3> points2D = new Stack<Vector3>();
		Stack<Vector3> points3D = new Stack<Vector3>();
		
		float xpos, ypos, xd, yd; 
		
		Material onMaterial;
		Material offMaterial;
		
		Material mirrorMaterial;

		float timer;
		int numObjects = 12;
		
		int animcount = 600;
		int linecounter = 0;
		boolean counterset = false;
		int counter = 0;
		int devisor = 37;
		int duration = 600;
		MediaPlayer sound1; 
		EllipticalOrbitAnimation3D anim;
		Object3D nullObject = new Object3D();
		Object3D nullLinesObject = new Object3D();
		ZahlenPaar[] pairs;
		float rotate = 0;
		boolean pickedObject = false;
		
		RajawaliScene mOtherScene = new RajawaliScene(this); 
		Camera cam = new Camera();
		 
		private ObjectColorPicker mPicker;
		private ATexture mCurrentTexture;
		
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
			//yd = - y/8 - ypos;
			//xd = - x/8 - xpos;
			
			
			nullObject.setRotX(xd+(y/8));
			nullObject.setRotY(yd+(x/8));
			nullLinesObject.setRotX(xd+(y/8));
			nullLinesObject.setRotY(yd+(x/8));
			
		}
		
		public void onObjectPicked(Object3D object) {
			
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
			getCurrentScene().setBackgroundColor(0xaaaaaaaa);
			getCurrentCamera().setPosition(0, .1f, 5);
			
			createFruit();
			
			mOtherScene = new RajawaliScene(this);
			mOtherScene.setBackgroundColor(0xaaaaaaaa);
			mOtherScene.addLight(mLight);
			
			mPostProcessingManager = new PostProcessingManager(this, 512, 512);
			
			RenderPass renderPass = new RenderPass(getCurrentScene(),
					getCurrentCamera(), 0);
	
			mPostProcessingManager.addPass(renderPass);
	
			MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x222222, 0xeeeeee, BlendMode.ADD);
			getCurrentCamera().setFarPlane(1000);
			mPostProcessingManager.addEffect(bloomEffect);
			
			bloomEffect.setRenderToScreen(true);
	
			createMirrors();
			
			switchScene(mOtherScene);
			
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
		
		private Line3D drawLine(Vector3 start, Vector3 end){
			
			Stack<Vector3> n = new Stack<Vector3>();
			n.add(start);
			n.add(end);
			
			Line3D line = new Line3D(n, 3, 0xffffffff);
			Material material = new Material();
			material.setColorInfluence(1);
			line.setMaterial(material);
			n.clear();
			
			return line;
		}
	
		private void createPoints(){
			points2D.clear();
			for (int i=0;i<objects2D.length;i++){
				points2D.add(objects2D[i].getPosition());
			}
		}
		
		private void createPoints3D(){
			points3D.clear();
			for (int i=0;i<objects3D.length;i++){
				points3D.add(objects3D[i].getPosition());
			}
		}
		
		void clearScene(){
			
			if (objects1D instanceof Line3D[]){ 
				for(int i = 0;i<objects1D.length;i++)
				{
					try{ 
						getCurrentScene().removeChild(objects1D[i]);
						getCurrentScene().removeChild(nullLinesObject);
						
					}catch(Exception e){
					  	Log.d("ERROR", "Object1D not in currentScene!");
					}
				}
			}

			if (objects2D instanceof Plane[]){ 
				for(int i = 0;i<objects2D.length;i++)
				{
					try{ 
						getCurrentScene().removeChild(objects2D[i]);
					}catch(Exception e){
					  	Log.d("ERROR", "Object2D not in currentScene!");
					}
					 
				}
			}

			if ( objects3D instanceof Sphere[])
			{ 
				for(int i = 0;i<objects3D.length;i++)
				{
					try{ 
						getCurrentScene().removeChild(objects3D[i]);
						getCurrentScene().removeChild(nullObject);
					}catch(Exception e){
					  	Log.d("ERROR", "Object3D not in currentScene!");
					}
					 
				}
			}
		
		}
		
		void clearLines(){
			
			if (objects1D instanceof Line3D[]){ 
				for(int i = 0;i<objects1D.length;i++)
				{
					try{ 
						getCurrentScene().removeChild(objects1D[i]);
						getCurrentScene().removeChild(nullLinesObject);
						
					}catch(Exception e){
					  	Log.d("ERROR", "Object1D not in currentScene!");
					}
				}
			}
		}
		
		void drawPlatonic(int r, boolean dim2d){
			
			objects1D = null;
			nullLinesObject= null;
			
			if (dim2d){
				nullLinesObject = new Object3D();
				pairs = tools.readRawTextFile(getContext(), r);
				objects1D = new Line3D[pairs.length];
				for (int i = 0; i<=objects1D.length-1; i++){
					objects1D[i] = drawLine(points2D.get(pairs[i].start), points2D.get(pairs[i].end));
				}
			}else{
				nullLinesObject = new Object3D();
				pairs = tools.readRawTextFile(getContext(), r);
				objects1D = new Line3D[pairs.length];
				for (int i = 0; i<=objects1D.length-1; i++){
					objects1D[i] = drawLine(points3D.get(pairs[i].start), points3D.get(pairs[i].end));
				}
			}
			
			if (objects1D instanceof Line3D[] && nullLinesObject instanceof Object3D){ 
				for(int i = 0;i<objects1D.length;i++){ 
					nullLinesObject.addChild(objects1D[i]);
				} 
					getCurrentScene().addChild(nullLinesObject);
				}
		}
		
		private void createFruit(){
			numObjects = 14;
			objects2D = new Plane[numObjects];
			
			Material m = new Material();
			m.setColorInfluence(0);
			
			try{
				m.addTexture(new Texture("circle", R.drawable.circle2));
			}catch(TextureException e){
				e.printStackTrace();
			}
			
			float xcount = 0;
			float ycount = 2;
			
			for (int i=0; i< numObjects; i++){
			
				objects2D[i] = new Plane(1,1,10,10); 
				objects2D[i].setDoubleSided(true);
				objects2D[i].setMaterial(m);
				objects2D[i].setPosition(xcount,ycount, 0);
				objects2D[i].setTransparent(true);
				nullObject.addChild(objects2D[i]);
				objects2D[i].setVisible(false);
			
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
				if (i==10){ xcount=-2;ycount=-1;}
				if (i==11){ xcount= 2;ycount=-1;}
			
			}
			
			getCurrentScene().addChild(nullObject);
			numObjects = objects2D.length;
			createPoints();
		}
	
		private void createSeed(){
			getCurrentCamera().setPosition(0,.1,5);
			numObjects = 19;
			objects2D = new Plane[numObjects];
			nullObject = new Object3D();
			
			Material m = new Material();
			m.setColorInfluence(0);
			
			try{
				m.addTexture(new Texture("circle", R.drawable.circle));
			}catch(TextureException e){
				e.printStackTrace();
			}
			
			float xcount = 0;
			float ycount = 0;
			
			float vor = 0;
			float nach = 1;
			float erg = 0;
			
			for (int i=0; i< numObjects; i++){
				
				if (i==0){ xcount=  0.0f ;ycount= 0.0f;}
				if (i==1){ xcount=  0.0f ;ycount= 0.5f;}
				if (i==2){ xcount= -0.44f ;ycount= 0.25f;}
				if (i==3){ xcount= -0.44f ;ycount= -0.25f;}
				if (i==4){ xcount= -0.0f ;ycount= -0.5f;}
				if (i==5){ xcount=  0.44f ;ycount= -0.25f;}
				if (i==6){ xcount=  0.44f ;ycount=  0.25f;}

				
				erg = vor+nach;
				nach = vor;
				vor = erg;
				float phi = vor / nach;
				
				Log.d("fibunacci", Float.toString(phi));
				
				objects2D[i] = new Plane(1,1,10,10); 
				objects2D[i].setDoubleSided(true);
				objects2D[i].setMaterial(m);
				objects2D[i].setPosition(xcount,ycount, 0);
				objects2D[i].setTransparent(true);
				//getCurrentScene().addChild(objects2D[i]);
				objects2D[i].setVisible(false);
				nullObject.addChild(objects2D[i]);
				
			}	
			getCurrentScene().addChild(nullObject);
			numObjects = objects2D.length;
			createPoints();
		}
		
		private void createFruit3d(){
			getCurrentCamera().setPosition(0,.1,20);
			numObjects = 30;
			objects3D = new Sphere[numObjects];

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
				
				
				objects3D[i] = new Sphere(1f,30,30); 
				objects3D[i].setColor((int)(Math.random() * 0xffffff)); 
				objects3D[i].setDoubleSided(true);
				objects3D[i].setMaterial(onMaterial);
				objects3D[i].setScale(1f);
				objects3D[i].setTransparent(true);
				objects3D[i].setPosition(xcount,ycount,zcount); 
				
				objects3D[i].setBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_COLOR);
				
				nullObject.addChild(objects3D[i]);
				mPicker.registerObject(nullObject.getChildAt(i));
				
				
			}	
			getCurrentScene().addChild(nullObject);
			createPoints3D();
			numObjects = objects3D.length-1;
		}
			
		private void createMirrors(){
		
			mirrors = new Plane[3];
			
			for(int i=0; i<mirrors.length;i++){
			
				mirrors[i] = new Plane(10,10,1,1);
				mirrors[i].setColor(0x000000);
				mirrors[i].setDoubleSided(true);
				mirrors[i].setPosition(-10f+(i*10), 0, -5);
				if (i==1) mirrors[i].setPosition(-10f+(i*10), 0, -7);
				mirrors[i].setRotation(0, 30-(i*30), 180);
				mirrorMaterial = new Material();
				mirrorMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
				mirrorMaterial.enableLighting(true);
				mirrorMaterial.setColorInfluence(1);
			
				mirrors[i].setMaterial(mirrorMaterial); 
				mOtherScene.addChild(mirrors[i]);
			}
		}		
			
		@Override
		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			rotate+=0.9f;
			if (timer > 0 && timer < animcount && counter != numObjects){
			//	if (timer % 1 == 0) 
				if (timer % devisor == 0 ){
							
					if (counter < numObjects && linecounter == 0) 
					{	
						nullObject.getChildAt(counter).setVisible(true);
   					    //animate(objects2D[counter], 1000);
   					    counter+=1;
					}
					
				}
				if (counter < numObjects && linecounter == 4) 
				{	
						nullObject.getChildAt(counter).setVisible(true);
   					    //animate(objects2D[counter], 1000);
   					    counter+=1;
				}
				if (linecounter == 5) nullObject.setRotation(rotate, 0,  rotate);
				
			}
			
			if (counterset && linecounter == 1){
				counterset = false;
				drawPlatonic(R.raw.coords_tetra, true);
				animcount = 150;
			}
			
			if (counterset && linecounter == 2){
				counterset = false;
				clearLines();						
				drawPlatonic(R.raw.coords_cube, true);
			}
	
			if (counterset && linecounter == 3){
				counterset = false;
				clearLines();	
				drawPlatonic(R.raw.coords_octra, true);	
			}
			
			if (counterset && linecounter == 4){
				counterset = false;
				counter = 0;
				animcount = 150;
				clearScene();
				getCurrentScene().removeChild(nullObject);
				nullObject = null;
				createSeed();
			}
			if (counterset && linecounter == 5){
				counterset = false;
				animcount = 400;
			}
			
			if (counterset && linecounter == 6){
				counterset = false;
				clearScene();
				createFruit3d();
				drawPlatonic(R.raw.coords_cube3d, false);
				animcount = 1000;
			}
	
			
			if (timer == animcount){
				counter = 0;
				linecounter +=1;
				timer = 0;
				counterset = true;
			}
			timer +=1;
		///	Log.d("timer", Float.toString(timer));
			
		}
		
		@Override
		public void onRender(final double deltaTime) {
			setViewPort(512, 512);
			mPostProcessingManager.render(deltaTime);
			try {
			
				if (mCurrentTexture != null)
					for(int i=0; i<mirrors.length;i++){
						mirrors[i].getMaterial().removeTexture(mCurrentTexture);
					}
					
					mCurrentTexture = mPostProcessingManager.getTexture();
					
					for(int i=0; i<mirrors.length;i++){
						mirrors[i].getMaterial().addTexture(mCurrentTexture);
					}
				} catch (TextureException e) {
					e.printStackTrace();
				}
			
			//
			// -- Change the viewport back to full screen
			//
			
			setViewPort(mViewportWidth, mViewportHeight);
			getCurrentCamera().setPosition(0,0.1f,15);
			super.onRender(deltaTime);

		}

	}
		
}
