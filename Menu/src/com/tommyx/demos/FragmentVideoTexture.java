package com.tommyx.demos;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import rajawali.animation.TranslateAnimation3D;
import rajawali.animation.Animation.RepeatMode;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.plugins.SpriteSheetMaterialPlugin;
import rajawali.materials.textures.AnimatedGIFTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.VideoTexture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.vector.Vector3;
import rajawali.primitives.Plane;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.media.MediaPlayer;
import android.os.Bundle;

public class FragmentVideoTexture extends AFragment {

	@Override
	protected ARenderer createRenderer() {
		return new FragmentRenderer(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return mLayout;
	}
	public final class FragmentRenderer extends ARenderer{

		DirectionalLight mLight;
		
		SpriteSheetMaterialPlugin spriteSheet = new SpriteSheetMaterialPlugin(5, 4, 15, 20);
		
		public FragmentRenderer(Context main) {
			super(main);
		}
		
		
		@Override
		protected void initScene() {
	
			super.initScene();
			
			mLight = new DirectionalLight(0,0,0); // set the direction
			mLight.setPosition(0,5,0);
			mLight.setColor(1.0f, 1.0f, 1.0f);
			mLight.setPower(5f);

			getCurrentCamera().setPosition(0, .1,15);
			getCurrentCamera().setLookAt(0, 0, 0);
			getCurrentCamera().setFarPlane(1000);
			
			getCurrentScene().addLight(mLight);
			getCurrentScene().setBackgroundColor(0xaaaaaa);
			
			createScene();
			
		}
		
		private void createScene(){
				
			Plane p = new Plane(1,1,1,1);
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
			
			getCurrentScene().addChild(p);
			
			TranslateAnimation3D lightAnim = new TranslateAnimation3D(
					new Vector3(-3, 3, 10), // from
					new Vector3(3, 1, 3)); // to
			lightAnim.setDurationMilliseconds(5000);
			lightAnim.setRepeatMode(RepeatMode.RESTART);
			lightAnim.setTransformable3D(p);
			lightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
			getCurrentScene().registerAnimation(lightAnim);
			lightAnim.play();

		}
		
		@Override
		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			
		}
		
		
		@Override
		public void onRender(final double deltaTime) {
			super.onRender(deltaTime);
		}
	}	
}
