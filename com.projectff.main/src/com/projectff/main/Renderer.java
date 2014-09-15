package com.projectff.main;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import rajawali.Object3D;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.EllipticalOrbitAnimation3D.OrbitDirection;
import rajawali.animation.EllipticalOrbitAnimation3D;
import rajawali.animation.TranslateAnimation3D;
import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.animation.mesh.SkeletalAnimationSequence;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.CubeMapTexture;
import rajawali.materials.textures.NormalMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.math.Matrix4;
import rajawali.math.vector.Vector3;
import rajawali.parser.Loader3DSMax;
import rajawali.parser.ParsingException;
import rajawali.parser.md5.LoaderMD5Anim;
import rajawali.parser.md5.LoaderMD5Mesh;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.effects.ShadowEffect;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.GLU;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;

public class Renderer extends RajawaliRenderer implements OnObjectPickedListener {

	private DirectionalLight mLight;
	private DirectionalLight mLight2;
	Cube lcube1 = new Cube(1);
	Cube lcube2 = new Cube(1);
	Cube c;
	int num = 3;
	boolean cloudsEnabled = false;
	Object3D[] clouds = new Plane[num];
	double deg = Math.PI / 180;
	Plane ground; 
	
	SkeletalAnimationObject3D currentModel;
	
	private SkeletalAnimationObject3D mObject;
	Plane screen;
	float time, timer = 0;
	private PostProcessingManager mPostProcessingManager;
	private int[] mViewport;
	private double[] mNearPos4;
	private double[] mFarPos4;
	private Vector3 mNearPos;
	private Vector3 mFarPos;
	private Vector3 mNewObjPos;
	private Matrix4 mViewMatrix;
	private Matrix4 mProjectionMatrix;
	private ObjectColorPicker mPicker;
	private Object3D mSelectedObject;
	
	public Renderer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SkeletalAnimationObject3D showMonster(String Monster, Vector3 pos, Vector3 rot, Vector3 scale, int fps){
		
		int mesh = getContext().getResources().getIdentifier(Monster + "_mesh", "raw", "com.projectff.main");
		
		scale = scale.multiply(0.0001f); 
		
		try {
			LoaderMD5Mesh meshParser = new LoaderMD5Mesh(this,
					mesh);
			meshParser.parse();

		    SkeletalAnimationObject3D mObject = (SkeletalAnimationObject3D) meshParser
					.getParsedAnimationObject();
			
			mObject.setPosition(pos);
			mObject.setRotation(rot);
			mObject.setScale(scale);
			mObject.setFps(fps);
			mObject.setTransparent(true);
			mPicker.registerObject(mObject);

			getCurrentScene().addChild(mObject);
			
			return mObject;
			
		} catch (ParsingException e) {
			e.printStackTrace();
		}
		
		return mObject;
	}
	
	public void loadAnim2Obj(SkeletalAnimationObject3D obj, String animname, boolean loop)
	{
		
		try{
		
		int anim = getContext().getResources().getIdentifier(animname, "raw", "com.projectff.main");

		LoaderMD5Anim animParser = new LoaderMD5Anim(animname, this,
													 anim);
		animParser.parse();

		SkeletalAnimationSequence sequence = (SkeletalAnimationSequence) animParser
			.getParsedAnimationSequence();
		
		obj.setAnimationSequence(sequence);
		obj.play(loop);
		} catch (ParsingException e) {
			e.printStackTrace();
		}
	
			
	}
	
	public SkeletalAnimationObject3D showMonster(String Monster, Vector3 pos, Vector3 rot, Vector3 scale)
	{	
		return showMonster(Monster,pos,rot,scale , 8);
	}
	
	public SkeletalAnimationObject3D showMonster(String Monster, Vector3 pos, Vector3 rot)
	{	
		return showMonster(Monster,pos,rot,new Vector3(0.0001f), 8);
	}
	
	private void createBackground(String background){
		int id    = getContext().getResources().getIdentifier(background, "drawable", "com.projectff.main");
		int id_nm = getContext().getResources().getIdentifier(background+"_nm", "drawable", "com.projectff.main");
		
		Texture m = new Texture("back", id);
		NormalMapTexture nm = new NormalMapTexture("backnm", id_nm);
		
		Material qm = new Material();
		qm.setDiffuseMethod(new DiffuseMethod.Lambert());
		screen = new Plane(30,20,1,1); 
		screen.setPosition(0,0,-10);
		screen.setRotY(180);
		screen.setDoubleSided(true);
		qm.setColorInfluence(0);
		qm.enableLighting(true);
		
		try {
			qm.addTexture(m);
			qm.addTexture(nm);
		}
		catch(TextureException t) {
			t.printStackTrace();
		}
		screen.setMaterial(qm);
		getCurrentScene().addChild(screen);
	}
	
	private void createSky(String sky){
		
		int [] resourceIds = { 	getContext().getResources().getIdentifier(sky +"_posx", "drawable", "com.projectff.main"), 
								getContext().getResources().getIdentifier(sky +"_negx", "drawable", "com.projectff.main"), 
								getContext().getResources().getIdentifier(sky +"_posy", "drawable", "com.projectff.main"), 
								getContext().getResources().getIdentifier(sky +"_negy", "drawable", "com.projectff.main"), 
								getContext().getResources().getIdentifier(sky +"_posz", "drawable", "com.projectff.main"), 
								getContext().getResources().getIdentifier(sky +"_negz", "drawable", "com.projectff.main"), 
							   };
		
		CubeMapTexture m = new CubeMapTexture("sky", resourceIds);
		
		Material qm = new Material();
		c = new Cube(140); 
		c.setDoubleSided(true);
		m.isSkyTexture(true);
		qm.setColorInfluence(0);
		
		Material gMat = new Material(); 
		ground = new Plane(100,100,1,1);
		ground.setDoubleSided(true);
		gMat.setColorInfluence(0);
		ground.setRotX(90);
		ground.setY(-2.2f);
		
		
		try {
			qm.addTexture(m);
			Texture t = new Texture("ground", resourceIds[3]);
			t.setRepeat(10,10);
			gMat.addTexture(t);
		}
		catch(TextureException t) {
			t.printStackTrace();

		}
	
		ground.setMaterial(gMat);
		c.setMaterial(qm);
		getCurrentScene().addChild(c);
		getCurrentScene().addChild(ground);
	}
	
	private void createClouds(int num){

	    Plane cloud = new Plane(1,1,1,1);
        cloud.setDoubleSided(true);
        cloud.setTransparent(true);
        //cloud.setBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        cloud.setRotation(90,90,0);
        Texture texture = new Texture("cloud", R.drawable.cloud);
        Material cloudMat = new Material(); 
        cloudMat.setColorInfluence(.0f);
        
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
        	//clouds[i].setColor(0x000000 + random.nextInt(0xfffffff));
        	float scale = 5 + (float) (Math.random() * 30.f);
        	
        	clouds[i].setPosition(-20 + Math.random()*40,3+Math.random()*10, -20 + Math.random()*10);
        	clouds[i].setRotation(30*deg, 30*deg, Math.random() * (float) Math.PI);
        	clouds[i].setScale(scale*2,scale,0);

        	getCurrentScene().addChild(clouds[i]);
        }
    }
	
	public void loadScenery(String name){
		
		int resourceIds = getContext().getResources().getIdentifier(name +"_scenery", "drawable", "com.projectff.main");

		Loader3DSMax loader = new Loader3DSMax(this, R.raw.china_houses);
		
		Material qm = new Material(); 
		qm.setColorInfluence(0);
				
		try {
			loader.parse();
			Object3D scenery = loader.getParsedObject();
			
			qm.addTexture(new Texture("scenery", resourceIds));
			scenery.setTransparent(true);
			scenery.setDoubleSided(true);
			scenery.setMaterial(qm);
			getCurrentScene().addChild(scenery);
			
		}catch(Exception t) {
		t.printStackTrace();
		}
		
	}
	
	public void initPicking(){
		
		mViewport = new int[] { 0, 0, mViewportWidth, mViewportHeight };
		mNearPos4 = new double[4];
		mFarPos4 = new double[4];
		mNearPos = new Vector3();
		mFarPos = new Vector3();
		mNewObjPos = new Vector3();
		mViewMatrix = getCurrentCamera().getViewMatrix();
		mProjectionMatrix = getCurrentCamera().getProjectionMatrix();

		mPicker = new ObjectColorPicker(this);
		mPicker.setOnObjectPickedListener(this);
	}
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
	
		switch (event.getAction()) {
		
		case KeyEvent.KEYCODE_SPACE:
			Log.d("dsfgadsgasdg", "dsagdasgdasgsdaga");
			loadAnim2Obj(currentModel ,"squall_hit_anim", false);
			break;
		}
		return true;
	}
	
	public boolean onTouch(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			y = event.getY();
		//	getObjectAt(x,y);
			break;
		case MotionEvent.ACTION_MOVE:
			moveSelectedObject(x, y);
			break;
		case MotionEvent.ACTION_UP:
			stopMovingSelectedObject();
			loadAnim2Obj(currentModel ,"squall_hit_anim", false);
			break;
		}
		return true;
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		super.onSurfaceChanged(gl, width, height);
		mViewport[2] = mViewportWidth;
		mViewport[3] = mViewportHeight;
		mViewMatrix = getCurrentCamera().getViewMatrix();
		mProjectionMatrix = getCurrentCamera().getProjectionMatrix();
	}
	
	private void getObjectAt(float x, float y) {
		mPicker.getObjectAt(x, y);
	}

	public void onObjectPicked(Object3D object)
	{
		mSelectedObject = object;
	}
	
	private void moveSelectedObject(float x, float y) {

		if (mSelectedObject == null)
			return;
		
		GLU.gluUnProject(x, mViewportHeight - y, 0, mViewMatrix.getDoubleValues(), 0,
				mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mNearPos4, 0);

		GLU.gluUnProject(x, mViewportHeight - y, 1.f, mViewMatrix.getDoubleValues(), 0,
				mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mFarPos4, 0);

		mNearPos.setAll(mNearPos4[0] / mNearPos4[3], mNearPos4[1]
				/ mNearPos4[3], mNearPos4[2] / mNearPos4[3]);
		mFarPos.setAll(mFarPos4[0] / mFarPos4[3],
				mFarPos4[1] / mFarPos4[3], mFarPos4[2] / mFarPos4[3]);

		double factor = (Math.abs(mSelectedObject.getZ()) + mNearPos.z)
				/ (getCurrentCamera().getFarPlane() - getCurrentCamera()
						.getNearPlane());

		mNewObjPos.setAll(mFarPos);
		mNewObjPos.subtract(mNearPos);
		mNewObjPos.multiply(factor);
		mNewObjPos.add(mNearPos);

		mSelectedObject.setX(mNewObjPos.x);
		mSelectedObject.setY(mNewObjPos.y);
	}
	
	private void stopMovingSelectedObject() {
		mSelectedObject = null;
	}
	
	@Override
	protected void initScene() {

		// TODO Auto-generated method stub
		super.initScene();
		initPicking();
		
		mLight = new DirectionalLight(0,1,0); // set the direction
		mLight.setPosition(0,5,0);
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(2f);
		
		getCurrentScene().addLight(mLight);
		getCurrentCamera().setY(1);
		getCurrentCamera().setX(-7);
		getCurrentCamera().setZ(7);
		
		//createBackground("mansion");
		createSky("china");
		loadScenery("china");
		//createClouds(num);
		
		
		SkeletalAnimationObject3D anaconda = showMonster("anaconda", new Vector3( 4,-1.5f,0), new Vector3(0,90,75), new Vector3(2));
		loadAnim2Obj(anaconda,"anaconda_anim", true); 
		SkeletalAnimationObject3D squall = showMonster("squall", new Vector3(-4,0,0), new Vector3(90,0,-90), new Vector3(1));
		currentModel = squall;
		loadAnim2Obj(squall,"squall_anim", true); 
		
		mPostProcessingManager = new PostProcessingManager(this);

		MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x111111, 0xffffff, BlendMode.SCREEN);
		mPostProcessingManager.addEffect(bloomEffect);
		
		bloomEffect.setRenderToScreen(true);

		getCurrentCamera().setLookAt(0,0,0); 
		
		EllipticalOrbitAnimation3D anim = new EllipticalOrbitAnimation3D(
				  new Vector3(0, .1, 0), 
				  new Vector3(0, .1, 10), 0, 359);

		anim.setRepeatMode(RepeatMode.INFINITE);
		anim.setDurationMilliseconds(100000);
		anim.setTransformable3D(getCurrentCamera());
		getCurrentScene().registerAnimation(anim);
		anim.play();
//		
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		// TODO Auto-generated method stub
		super.onDrawFrame(glUnused);
		time=0.1f;
		timer+=0.1f;
		if(cloudsEnabled){
			for (Object3D i : clouds){
				
				if (i.getX() > 30){ i.setX(i.getX() - 30);}
				float position = (float) i.getX() + time;
				i.setX(position);
				i.setRotY(0);
			}
		}
		// c.setRotY(timer);
		// ground.setRotZ(timer);
	}
	
	@Override
	public void onRender(final double deltaTime) {
		super.onRender(deltaTime);
		mPostProcessingManager.render(deltaTime);
		mLight.setPosition(lcube1.getPosition());
		
	}
}
