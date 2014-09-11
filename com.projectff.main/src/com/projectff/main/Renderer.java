package com.projectff.main;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import rajawali.Object3D;
import rajawali.animation.Animation.RepeatMode;
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
	
	private SkeletalAnimationObject3D mObject;
	Plane screen;
	float time = 0;
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

	
	
	public void showMonster(String Monster, String animation, Vector3 pos, Vector3 rot, Vector3 scale){
		
		int mesh = getContext().getResources().getIdentifier(Monster + "_mesh", "raw", "com.projectff.main");
		int anim = getContext().getResources().getIdentifier(Monster + "_anim", "raw", "com.projectff.main");
		
		scale = scale.multiply(0.0001f); 
		
		try {
			LoaderMD5Mesh meshParser = new LoaderMD5Mesh(this,
					mesh);
			meshParser.parse();

			LoaderMD5Anim animParser = new LoaderMD5Anim(animation, this,
					anim);
			animParser.parse();

			SkeletalAnimationSequence sequence = (SkeletalAnimationSequence) animParser
					.getParsedAnimationSequence();

			mObject = (SkeletalAnimationObject3D) meshParser
					.getParsedAnimationObject();
			mObject.setAnimationSequence(sequence);
			mObject.setPosition(pos);
			mObject.setRotation(rot);
			mObject.setScale(scale);
			mObject.setFps(8);
			mObject.setTransparent(true);
			mPicker.registerObject(mObject);
			mObject.play(true);

			getCurrentScene().addChild(mObject);
		} catch (ParsingException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void showMonster(String Monster, String animation, Vector3 pos, Vector3 rot)
	{	
		showMonster(Monster,animation,pos,rot,new Vector3(0.0001f));
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
		Cube c = new Cube(140); 
		c.setDoubleSided(true);
		m.isSkyTexture(true);
		qm.setColorInfluence(0);
		
		try {
			qm.addTexture(m);
		}
		catch(TextureException t) {
			t.printStackTrace();
		}
		
		c.setMaterial(qm);
		getCurrentScene().addChild(c);
	}
	
	@Override
	protected void initScene() {

		// TODO Auto-generated method stub
		super.initScene();
		initPicking();
		
		mLight2 = new DirectionalLight(0,1,0); // set the direction
		mLight2.setPosition(0,5,-5);
		mLight2.setColor(1.0f, 1.0f, 1.0f);
		mLight2.setPower(1f);
		
		mLight = new DirectionalLight(0, 1, 0); // set the direction
		mLight.setPosition(0,-5, -5);
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(1f);

		getCurrentScene().addLight(mLight);
		getCurrentScene().addLight(mLight2);
		getCurrentCamera().setY(1);
		getCurrentCamera().setZ(10);
		
		createBackground("mansion");
	//	showMonster("anaconda", "Action", new Vector3( 5,0,0), new Vector3(0,-0,-0), new Vector3(3));
	//	showMonster("squall", "Action", new Vector3(2,0,0), new Vector3(0,0,0), new Vector3(3));
		showMonster("bomb", "Action", new Vector3(5,2,0), new Vector3(0,90,75), new Vector3(2));
		
		Material m = new Material();
		lcube1.setMaterial(m);
		lcube1.setScale(.5f);
		lcube1.setColor(Color.BLACK);
		lcube1.setPosition(mLight.getPosition());
		mPicker.registerObject(lcube1);
		getCurrentScene().addChild(lcube1);
		
		lcube2.setMaterial(m);
		lcube2.setScale(.5f);
		lcube2.setColor(Color.BLACK);
		lcube2.setPosition(mLight2.getPosition());
		mPicker.registerObject(lcube2);
		getCurrentScene().addChild(lcube2);
		
		mPostProcessingManager = new PostProcessingManager(this);

		MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x000000, 0xffffff, BlendMode.ADD);
		mPostProcessingManager.addEffect(bloomEffect);
		
		bloomEffect.setRenderToScreen(true);
		
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
	
	public boolean onTouch(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			y = event.getY();
			getObjectAt(x,y);
			break;
		case MotionEvent.ACTION_MOVE:
		//	moveSelectedObject(x, y);
			break;
		case MotionEvent.ACTION_UP:
		//	stopMovingSelectedObject();
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
	public void onDrawFrame(GL10 glUnused) {
		// TODO Auto-generated method stub
		super.onDrawFrame(glUnused);
		time+=0.1;
	}
	
	@Override
	public void onRender(final double deltaTime) {
		super.onRender(deltaTime);
//		mPostProcessingManager.render(deltaTime);
//		mLight.setPosition(lcube1.getPosition());
//		mLight2.setPosition(lcube2.getPosition());
	}
}
