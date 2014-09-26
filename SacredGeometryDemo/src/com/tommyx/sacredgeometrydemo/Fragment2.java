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
import rajawali.materials.methods.SpecularMethod;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.math.vector.Vector3;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;
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

public class Fragment2 extends AFragment {

	@Override
	protected ARenderer createRenderer() {
		return new FragmentRenderer(getActivity());
	}
	
	public final class FragmentRenderer extends ARenderer {

		DirectionalLight mLight;
		private PostProcessingManager mPostProcessingManager;
		Plane fruit[];
		Sphere fruit3d[];
		
		float timer;
		int numObjects = 12;
		
		int animcount = 100;
		int linecounter = 0;
		boolean counterset = false;
		int counter = 0;
		int devisor = 5;
		int duration = 600;
		MediaPlayer sound1; 
		
		public FragmentRenderer(Context main) {
			super(main);
			
		}
	
		@Override
		protected void initScene() {
	
			super.initScene();
			
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
			mPostProcessingManager.addEffect(bloomEffect);
			
			bloomEffect.setRenderToScreen(true);
	
			getCurrentCamera().setLookAt(0,0,0); 
			
			EllipticalOrbitAnimation3D anim = new EllipticalOrbitAnimation3D(
					  new Vector3(0, 0, 0), 
					  new Vector3(0, .1, 15), 0, 359);
	
			anim.setRepeatMode(RepeatMode.INFINITE);
			anim.setDurationMilliseconds(20000);
			anim.setTransformable3D(getCurrentCamera());
			getCurrentScene().registerAnimation(anim);
			anim.play();
			createFruit();
			
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
		
		private void createFruit(){
			numObjects = 14;
			fruit = new Plane[numObjects];
			Material m = new Material();
			m.setColorInfluence(0);
			
			try{
				m.addTexture(new Texture("ring", R.drawable.circle));
			}catch(TextureException e){
				e.printStackTrace();
			}
			
			for (int i=0; i< numObjects; i++){
				fruit[i] = new Plane(1,1,10,10); 
				fruit[i].setDoubleSided(true);
				fruit[i].setMaterial(m);
				fruit[i].setPosition(-i/2 * Math.sin(i),-i/2 * Math.cos(i), 0);
				fruit[i].setTransparent(true);
				getCurrentScene().addChild(fruit[i]);
				//fruit[i].setVisible(false);
			}
			numObjects = fruit.length-1;
		}
		
		private void createFruit3d(){
			numObjects = 14;
			fruit3d = new Sphere[numObjects];
			Material m = new Material();
			m.setColorInfluence(.5f);
			SpecularMethod.Phong phongMethod = new SpecularMethod.Phong();
			phongMethod.setShininess(180);
			m.setSpecularMethod(phongMethod);
			m.setAmbientIntensity(0, 0, 0);
			m.enableLighting(true);
			m.setDiffuseMethod(new DiffuseMethod.Lambert());
						
			try {
				m.addTexture(new Texture("sphere",R.drawable.sphere));
			} catch (TextureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
						
			for (int i=0; i< numObjects; i++){
				m.setAmbientColor(0x11*i);
				fruit3d[i] = new Sphere(1,10,10); 
				fruit3d[i].setColor((int)(Math.random() * 0xffffff));
				fruit3d[i].setDoubleSided(true);
				fruit3d[i].setMaterial(m);
				fruit3d[i].setPosition(-i/4 * Math.sin(i),-i/4 * Math.cos(i), 0);
				fruit3d[i].setRenderChildrenAsBatch(true);
				getCurrentScene().addChild(fruit3d[i]);
				//fruit[i].setVisible(false);
			}
			numObjects = fruit3d.length-1;
		}
			
		@Override
		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			
			if (timer > 0 && timer < animcount && counter != numObjects){
			//	if (timer % 1 == 0) 
				if (timer % devisor ==0 ){
							
					if (counter < numObjects && linecounter == 0) 
					{
						counter+=1;
						animate(fruit[counter], duration);
					}
					
					if (counter < numObjects && linecounter == 1)
					{ 
						counter+=1;
					//animate(fruit3d[counter], duration);
					}
				}
				
				
			}
			
			if (counterset && linecounter == 1){
				counterset = false;
				//getCurrentScene().setBackgroundColor(0);
				for (Plane p : fruit){
					getCurrentScene().removeChild(p);
				}
				createFruit3d();
				animcount = 300;
			}
			
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
