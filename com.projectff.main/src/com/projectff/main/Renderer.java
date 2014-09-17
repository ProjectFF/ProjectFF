package com.projectff.main;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
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
import rajawali.math.vector.Vector3;
import rajawali.parser.Loader3DSMax;
import rajawali.parser.ParsingException;
import rajawali.parser.md5.LoaderMD5Anim;
import rajawali.parser.md5.LoaderMD5Mesh;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;


public class Renderer extends RajawaliRenderer {

	private DirectionalLight mLight;
	private PointLight mLight2;
	Cube lcube1 = new Cube(1);
	Cube lcube2 = new Cube(1);
	Cube c;
	int num = 3;
	boolean cloudsEnabled = true;
	Object3D[] clouds = new Plane[num];
	double deg = Math.PI / 180;
	Plane ground; 
	
	SkeletalAnimationObject3D currentModel, currentEnemy;
	
	private SkeletalAnimationObject3D mObject;
	Plane screen;
	float time, timer = 0;
	private PostProcessingManager mPostProcessingManager;
	private float xd;
	private float yd;
	float xpos, ypos;
	
	
	public Renderer(Context context) {
		super(context);
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
		obj.play(false);
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
        	
        	clouds[i].setPosition(-30 + Math.random()*60,3+Math.random()*10, -20 + Math.random()*40);
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
	
	public void btn_attack(){
		
		Vector3 mPos = currentModel.getPosition().clone();
		Vector3 ePos = currentEnemy.getPosition().clone();
		ePos.y = ePos.y+1.5f;
		ePos.x = ePos.x-3;
		
		TranslateAnimation3D ta = new TranslateAnimation3D(mPos, ePos);
		ta.setRepeatMode(RepeatMode.NONE);
		ta.setDurationMilliseconds(2000);
		ta.setTransformable3D(currentModel);
		getCurrentScene().registerAnimation(ta);
		ta.play();
		
		loadAnim2Obj(currentModel, "squall_anim", false);
		
		TranslateAnimation3D ta2 = new TranslateAnimation3D(ePos,mPos);
		ta2.setRepeatMode(RepeatMode.NONE);
		ta2.setDurationMilliseconds(2000);
		ta2.setTransformable3D(currentModel);
		getCurrentScene().registerAnimation(ta2);
		ta2.play();
		
		loadAnim2Obj(currentModel, "squall_stand_anim", false);
		
	}
	
	public void btn_magic(String kindof){
		
	}
	
	public void btn_item(){
		
	}
	
	public boolean onTouch(MotionEvent event) {

		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			xpos = event.getX();
		    break;
		case MotionEvent.ACTION_MOVE:
			
			xd = - event.getX()/8 - xpos  ;
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return true;
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		super.onSurfaceChanged(gl, width, height);
	}
	
	
	@Override
	protected void initScene() {

		super.initScene();
		
		mLight = new DirectionalLight(0,1,0); // set the direction
		mLight.setPosition(0,5,0);
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(2f);
		
		mLight2 = new PointLight(); // set the direction
		mLight.setPosition(0,.1,0);
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(2f);
		
		
		getCurrentScene().addLight(mLight);
		getCurrentCamera().setY(.1);
		getCurrentCamera().setX(-7);
		getCurrentCamera().setZ(7);
		
		//createBackground("mansion");
		createSky("china");
		createClouds(num);
		
		SkeletalAnimationObject3D anaconda = showMonster("anaconda", new Vector3( 6f,-1.5f,0), new Vector3(0,90,75), new Vector3(4f));
		anaconda.setFps(24);
		currentEnemy = anaconda;
		loadAnim2Obj(anaconda,"anaconda_stand_anim", true); 
		SkeletalAnimationObject3D squall = showMonster("squall", new Vector3(-4,0,0), new Vector3(90,0,-90), new Vector3(1));
		squall.setFps(24);
		currentModel = squall;
		loadAnim2Obj(squall,"squall_stand_anim", true); 
		
		mPostProcessingManager = new PostProcessingManager(this);

		MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x111111, 0xffffff, BlendMode.SCREEN);
		mPostProcessingManager.addEffect(bloomEffect);
		
		bloomEffect.setRenderToScreen(true);

		getCurrentCamera().setLookAt(0,0,0); 
		
//		EllipticalOrbitAnimation3D anim = new EllipticalOrbitAnimation3D(
//				  new Vector3(0, .1, 0), 
//				  new Vector3(0, .1, 10), 0, 359);
//
//		anim.setRepeatMode(RepeatMode.INFINITE);
//		anim.setDurationMilliseconds(100000);
//		anim.setTransformable3D(getCurrentCamera());
//		getCurrentScene().registerAnimation(anim);
//		anim.play();
//		
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
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
//		c.setRotY(timer);
		// ground.setRotZ(timer);
		
		    getCurrentCamera().setRotY(xd);
	}
	
	@Override
	public void onRender(final double deltaTime) {
		super.onRender(deltaTime);
		mPostProcessingManager.render(deltaTime);
	}
}
