package com.tommyx.sacredgeometrydemo;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import rajawali.Object3D;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.AlphaAnimation3D;
import rajawali.animation.EllipticalOrbitAnimation3D;
import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.math.vector.Vector3;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.util.Log;
import android.media.MediaPlayer;

public class IntroRenderer extends RajawaliRenderer {

	DirectionalLight mLight;
	private PostProcessingManager mPostProcessingManager;
	Plane line0[];
	
	float timer;
	int numObjects = 0;
	
	int animcount = 100;
	int linecounter = 0;
	boolean counterset = false;
	int counter = 0;
	int devisor = 5;
	int duration = 600;
	MediaPlayer sound1; 
	MediaPlayer sound2; 
	
	public IntroRenderer(Context context) {
		super(context);
		sound1 = MediaPlayer.create(context, R.raw.type);
		sound2 = MediaPlayer.create(context, R.raw.bing);
	}

	@Override
	protected void initScene() {

		super.initScene();
		
		mLight = new DirectionalLight(0,1,0); // set the direction
		mLight.setPosition(5,5,0);
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setDirection(0, 0, 0);
		mLight.setPower(2f);
		
		getCurrentScene().addLight(mLight);
		getCurrentCamera().setPosition(0,.1,20);
		
		mPostProcessingManager = new PostProcessingManager(this);

		MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x000000, 0xffffff, BlendMode.ADD);
		mPostProcessingManager.addEffect(bloomEffect);
		
		bloomEffect.setRenderToScreen(true);

		getCurrentCamera().setLookAt(0,0,0); 
		showAnimatedText( "_The_Flower_of_Life_", new Vector3(15,0,0));
		
		EllipticalOrbitAnimation3D anim = new EllipticalOrbitAnimation3D(
				  new Vector3(0, 0, 0), 
				  new Vector3(0, .5, 5), 0, 359);

		anim.setRepeatMode(RepeatMode.INFINITE);
		anim.setDurationMilliseconds(10000);
		anim.setTransformable3D(mLight);
		getCurrentScene().registerAnimation(anim);
		anim.play();
		
	}
	
	public void showAnimatedText(String text, Vector3 position){
		
		line0 = new Plane[text.length()];
		
		for(int i = 0; i < text.length();i++){
			
			line0[i] = new Plane(16,16,1,1);
			Log.d("charAT", Character.toString(text.charAt(i)));
			String charname = Character.toString(text.charAt(i));
			Bitmap curChar = textAsBitmap(charname);
			Material m = new Material();
			m.setDiffuseMethod(new DiffuseMethod.Lambert());
			m.enableLighting(true);
			m.setColorInfluence(1);
			line0[i].setColor(0x0000000);
			
			try{
				m.addTexture(new AlphaMapTexture(charname, curChar));
			}catch(TextureException r){
				r.printStackTrace();
			}
			
			line0[i].setDoubleSided(true);
			line0[i].setScale(-1, 1, 1);
			line0[i].setTransparent(true);
			line0[i].setMaterial(m);
			line0[i].setPosition(-(text.length())+(i)+position.x, position.y, position.z);
			getCurrentScene().addChild(line0[i]);
		}
		numObjects = line0.length-1;
	}
	
	public void animate(Object3D o, int duration){
		
		AlphaAnimation3D anim = new AlphaAnimation3D(0x00000000, 0xFF000000);
		anim.setDurationMilliseconds(duration);
		anim.setRepeatMode(RepeatMode.NONE);
		anim.setTransformable3D(o);
		getCurrentScene().registerAnimation(anim);
		anim.play();
		
		AlphaAnimation3D anim2 = new AlphaAnimation3D(0xff000000, 0x00000000);
		anim2.setDurationMilliseconds(duration);
		anim2.setDelayMilliseconds(duration);
		anim2.setRepeatMode(RepeatMode.NONE);
		anim2.setTransformable3D(o);
		getCurrentScene().registerAnimation(anim2);
		anim2.play();
		
		
	}
		
	public Bitmap textAsBitmap(String text) 
	{
		Bitmap mScoreBitmap = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		
		Canvas mScoreCanvas = new Canvas(mScoreBitmap);
		Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(25);
		mTextPaint.setTypeface(Typeface.MONOSPACE);

		mScoreCanvas.drawColor(0, Mode.CLEAR);
		
		mScoreCanvas.drawText(text, 0,
				30, mTextPaint);
		
		return mScoreBitmap;
    }
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		
		
		
		if (timer > 0 && timer < animcount && counter != numObjects){
		//	if (timer % 1 == 0) 
			if (timer % devisor ==0 ){
						
				if (counter < numObjects) counter+=1;
				sound1.start();
				animate(line0[counter], duration);
			}
			
		}
		
		if (timer == animcount){
			sound1.pause();
			sound2.start();
			counter = 0;
			linecounter +=1;
			timer = 0;
			counterset = true;
		}
		
		if (counterset && linecounter == 1){
			counterset = false;
			showAnimatedText("_was_created_by_", new Vector3(10,-5,0));	
		}
		
		if (counterset && linecounter == 2){
			counterset = false;
			showAnimatedText("_the_gods_who_taught_", new Vector3(20,-5,0));	
		}
		
		if (counterset && linecounter == 3){
			counterset = false;
			showAnimatedText("_Drunvalo_Melchizedek_", new Vector3(20,-7,0));	
		}
		if (counterset && linecounter == 4){
			counterset = false;
			animcount = 50;
			duration = 500;
			showAnimatedText("oOoOoOoOoOoOo", new Vector3(20,-7,0));	
		}
		
		
		
		timer +=1;
		
		Log.d("timer", Float.toString(timer));
	}
	
	@Override
	public void onRender(final double deltaTime) {
		super.onRender(deltaTime);
		mPostProcessingManager.render(deltaTime);
	}
	
}
