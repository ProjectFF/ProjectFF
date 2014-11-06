package com.tommyx.demos;

import rajawali.materials.shaders.VertexShader;
import rajawali.util.RawShaderLoader;

public class CustomRawVertexShader extends VertexShader {
	public CustomRawVertexShader()
	{
		super();
		mNeedsBuild = false;
		initialize();
	}
	
	@Override
	public void initialize()
	{
		mShaderString = RawShaderLoader.fetch(R.raw.vertexshader);
	}
	
	@Override
	public void main() {

	}
	
	@Override
	public void setLocations(final int programHandle)
	{
		super.setLocations(programHandle);
	}
	
	@Override
	public void applyParams() 
	{
		super.applyParams();
	}
}
