package com.tommyx.demos;

import java.io.ObjectInputStream;
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
import rajawali.SerializedObject3D;
import rajawali.animation.ColorAnimation3D;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.EllipticalOrbitAnimation3D;
import rajawali.animation.RotateAnimation3D;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.animation.SplineTranslateAnimation3D;
import rajawali.animation.TranslateAnimation3D;
import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.curves.CatmullRomCurve3D;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.materials.plugins.SpriteSheetMaterialPlugin;
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
import rajawali.math.Matrix4;
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
import rajawali.util.GLU;
import rajawali.util.MeshExporter;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;
import rajawali.util.exporter.SerializationExporter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.os.Bundle;

public class FragmentLandScape extends AFragment implements OnTouchListener {

	public PostProcessingManager top;
	public RajawaliScene bottomscene;
	public ObjectInputStream ois;
	public ObjectColorPicker mPicker;
	SpriteSheetMaterialPlugin spriteSheet = new SpriteSheetMaterialPlugin(5, 4, 45, 20);
	
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
		Object3D clouds[], clouds2[], birds[];
		
		float timer, time, rotate = 0;
		float maxY = 27.5f;
		int numBirds = 20;
		float minY = 5;
		int numObjects = 0;
		Material qm;
		int animcount = 100;
		int linecounter = 0;
		boolean counterset = false;
		int counter = 0;
		int devisor = 5;
		int duration = 600;
		MediaPlayer sound1; 
		Object3D flares[];
		Cube cube;
		
		Texture gTexture; 
		Object3D empty;
		Object3D world;
		
		float xd,yd, xpos, ypos = 0;
		
		Object3D pickedObject;
		Sphere sky;
		
		float oldx, oldy;
		
		public FragmentRenderer(Context main) {
			super(main);
		}
		
		public void onFingerDown(float x, float y) {
			yd = (float) getCurrentCamera().getRotY();
			ypos = (float) world.getRotY();
			xd = (float) getCurrentCamera().getY();
		}
		
		public void onFingerUp(float x, float y){
			yd = (float) getCurrentCamera().getRotY();
			ypos = (float) world.getRotY();
			xd = (float) getCurrentCamera().getY();
		}
			
		public void onFingerMove(float x, float y){
			
			if (oldx != x){
				if (oldx > x) {
					yd -= 1.5f; 
					ypos -= 1.5f;
					getCurrentCamera().setRotY(yd);
					//scenery.setRotY(yd);
					world.setRotY(ypos);
				}
				else 
				{
					yd+= 1.5f;
					ypos += 1.5f;
					getCurrentCamera().setRotY(yd);
					//scenery.setRotY(yd);
					world.setRotY(ypos);
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
		protected void initScene() {
	
			super.initScene();
	
			world = new Object3D();
			empty = new Object3D();
			
			mLight = new DirectionalLight();
			mLight.setDirection(0, 0, 0);
			mLight.setPosition(0,30,0);
			mLight.setPower(1.5f);
			
			getCurrentCamera().setPosition(0, 15,160);
			getCurrentCamera().setRotation(0, -150,0);
			getCurrentCamera().setLookAt(0, 10, 0);
			getCurrentCamera().setFarPlane(1000);
			getCurrentCamera().setFieldOfView(70);
			
			
			FogParams fp = new FogParams(FogType.LINEAR, 0xffffff, 20, 250);
		
			getCurrentScene().setFog(fp);
			
			getCurrentScene().addLight(mLight);
			
			//getCurrentScene().setBackgroundColor(0x0000000);
			
			//createLensFlares();
			try{
				getCurrentScene().setSkybox(R.drawable.atmosphere2, 1000);
			}catch(Exception e){}
			
			createScene();
			createClouds(20); 
			addBirds();
			world.addChild(empty);
			getCurrentScene().addChild(world);
			
			target = new PostProcessingManager(this);
			
			ShadowEffect shadowEffect = new ShadowEffect(getCurrentScene(), getCurrentCamera(), mLight, 2048);
			shadowEffect.setShadowInfluence(.5f);
			target.addEffect(shadowEffect);
			shadowEffect.setRenderToScreen(true);
			
		}
		
		private void addBirds(){
			
			birds = new Object3D[numBirds];
			
			Plane p = new Plane(15,15,1,1);
			p.setTransparent(true);
			p.setDoubleSided(true);
			p.setRotX(0);
			Material material = new Material();
			material.setColorInfluence(0);
			
			try {
				
				material.addTexture(new Texture("flickrPics", R.drawable.image1));
				material.setColorInfluence(0);
			} catch (TextureException e) {
				e.printStackTrace();
			}
			
			spriteSheet.play();
			material.addPlugin(spriteSheet);
			p.setMaterial(material);
			
	        for ( int i = 0; i < numBirds; i++ ) {
	        	
	        	birds[i] = p.clone();
				birds[i].setTransparent(true);
				birds[i].setDoubleSided(true);
				birds[i].setRotY(90);
				birds[i].setMaterial(material);
				birds[i].setPosition(-100 + Math.random()*200, 0+Math.random()*50, -100 + Math.random()*200);
				getCurrentScene().addChild(birds[i]);
	        }
			
        }
		
		public void createScene(){ 
			
			qm = new Material(new CustomRawVertexShader(),new CustomRawFragmentShader()); 
			qm.setDiffuseMethod(new DiffuseMethod.Lambert());
			qm.enableLighting(false);	
			qm.setColorInfluence(0f);
			qm.enableTime(true);
			
			try {
//			
//				Loader3DSMax loader = new Loader3DSMax(this,  R.raw.terrain);
//				loader.parse();
//				Object3D terrain = loader.getParsedObject(); 
//				
//				MeshExporter exp = new MeshExporter(terrain);
//				SerializationExporter s = new SerializationExporter();
//				exp.export("terrain", s.getClass());
//				
			    ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.terrainser));
				Object3D terrain = new Object3D((SerializedObject3D) ois.readObject());

			    Material m = new Material();
				m.enableLighting(true);
				m.setDiffuseMethod(new DiffuseMethod.Lambert());
				m.addTexture(new Texture("terrain", R.drawable.terrain2 ));
				m.addTexture(new NormalMapTexture("terrain_nm", R.drawable.terrain_hm));
				terrain.setMaterial(m);
				m.setColorInfluence(0);
				terrain.setDoubleSided(true);
				terrain.setScale(10);
				//terrain.setRotX(90);
				terrain.setPosition(0, -10,-0);
				getCurrentScene().addChild(terrain);
			
				Plane p = new Plane(300,300,1,1);
				p.setDoubleSided(true);
				p.setRotX(90);
				p.setTransparent(true);
				p.setPosition(0,-3.0,0);
				p.setBlendFunc(GL10.GL_SRC_COLOR, GL10.GL_ONE_MINUS_SRC_ALPHA); // bright Sky 
					
				Texture t = new Texture("water", R.drawable.water);
				t.setRepeat(3, 3);
				qm.addTexture(t);
				
				p.setMaterial(qm);
				getCurrentScene().addChild(p);
			
				Plane p2 = new Plane(300,300,1,1);
				p2.setDoubleSided(true);
				p2.setRotY(180);
				p2.setRotX(90);
				p2.setTransparent(true);
				p2.setPosition(0,-1.0,0);
				p2.setBlendFunc(GL10.GL_SRC_COLOR, GL10.GL_SRC_ALPHA); // bright Sky 
					
				p2.setMaterial(qm);
				getCurrentScene().addChild(p2);
			
			}catch(Exception t) {
				t.printStackTrace();
			}
		}
		
		public void createLensFlares(){
			
			Random random = new Random();
			flares = new Object3D[4];
			empty.setPosition(0,0,-100);
			
			Material cubeMat = new Material();
			cubeMat.setColorInfluence(0.5f);
			
			Plane flare = new Plane(10,10,1,1);
			flare.setPosition(0,0,-100);
			flare.setTransparent(true);
			
			for (int i=0;i<flares.length;i++){
			
				flares[i] = flare.clone();
				flares[i].setPosition(0, 200, -100);
				flares[i].setTransparent(true);
				flares[i].setScale(5);
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
	        //cloud.setBlendFunc(GL10.GL_SRC_ALPHA_SATURATE, GL10.GL_ONE_MINUS_SRC_ALPHA); //Night Sky dark clouds
	        //cloud.setBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_DST_ALPHA); // bright Sky 
	        
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
	        clouds2 = new Object3D[num];
	        float scale = 75;
	        
	        for ( int i = 0; i < num; i++ ) {

	        	clouds[i] = cloud.clone();
	        	clouds[i].setDoubleSided(true);
	        	clouds[i].setColor(0xffffff);
	        	
	        	
	        	clouds[i].setPosition(-100 + Math.random()*200, 20+Math.random()*40, -100 + Math.random()*200);
	        	clouds[i].setRotation(90,0,  Math.random() * i);
	        	clouds[i].setScale(scale,scale,scale);

	        	getCurrentScene().addChild(clouds[i]);
	        }

	        for ( int i = 0; i < num; i++ ) {

	        	clouds2[i] = clouds[i].clone();
	        	clouds2[i].setDoubleSided(true);
	        	clouds2[i].setColor(0xffffff);
	        	
	        	clouds2[i].setPosition(clouds[i].getX(),-1.0, clouds[i].getZ() );
	        	clouds2[i].setRotation(clouds[i].getRotation());
	        	clouds2[i].setScale(scale-.5,scale-.5,scale-.5);

	        	getCurrentScene().addChild(clouds2[i]);
	        }
	        	
		}
		
		@Override
		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			time+=.05f;
			qm.setTime(time);
		}
		
		@Override
		public void onRender(final double deltaTime) { 
			target.render(deltaTime);
			timer=.1f;
			rotate=0.01f;
			
			if(cloudsEnabled ){
				for (Object3D i : clouds){
					if (i.getZ() > 200){ i.setZ(i.getZ() - 400);}
					float position = (float) i.getZ() + timer;
					i.setZ(position);
					//i.setY(position);
				}
				for (Object3D i : clouds2){
					
					if (i.getZ() > 200){ i.setZ(i.getZ() - 400);}
					float position = (float) i.getZ() + timer;
					i.setZ(position);
					//i.setY(position);
				}
				for (Object3D i : birds){
					
					if (i.getZ() > 200){ i.setZ(i.getZ() - 400);}
					float position = (float) i.getZ() + timer*10;
					i.setZ(position);
					//i.setY(position);
				}
			}
			
			//super.onRender(deltaTime);
		}
		
	}
}