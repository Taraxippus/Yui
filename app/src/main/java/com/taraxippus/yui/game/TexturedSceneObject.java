package com.taraxippus.yui.game;

import android.opengl.GLES20;
import com.taraxippus.yui.model.Model;
import com.taraxippus.yui.render.Renderer;
import com.taraxippus.yui.texture.Texture;

public class TexturedSceneObject extends SceneObject
{
	protected Texture texture;
	private int textureResource = -1;

	public TexturedSceneObject(World world, Texture texture)
	{
		super(world);
		
		this.texture = texture;
	}

	public TexturedSceneObject(World world, Model model, Texture texture)
	{
		super(world, model);
		
		this.texture = texture;
	}
	
	public TexturedSceneObject setTexture(int textureResource)
	{
		this.textureResource = textureResource;
		
		return this;
	}

	@Override
	public void init()
	{
		super.init();

		if (texture == null && textureResource != -1)
		{
			texture = new Texture();
			texture.init(world.main.resourceHelper.getBitmap(textureResource), GLES20.GL_NEAREST_MIPMAP_LINEAR, GLES20.GL_NEAREST, GLES20.GL_REPEAT);
		}
		
		texture.bind();
	}

	@Override
	public void render(Renderer renderer)
	{
		GLES20.glUniform1i(getPass().getParent().getProgram().getUniform("u_Texture"), texture.getTextureUnit());
		
		super.render(renderer);
	}

	@Override
	public void delete()
	{
		if (textureResource != -1)
			texture.delete();

		super.delete();
	}
}
