package com.taraxippus.yui.texture;

import android.opengl.GLES20;
import java.nio.Buffer;
import java.util.Random;

public abstract class ProceduralTexture extends Texture
{
	final int width, height, format, type, pixelFormat, minFilter, magFilter, wrapping;
	Buffer buffer;
	public final Random random;
	
	public ProceduralTexture(Random random, int width, int height, int format, int type, int pixelFormat, int minFilter, int magFilter, int wrapping)
	{
		this.random = random;
		this.width = width;
		this.height = height;
		this.format = format;
		this.type = type;
		this.pixelFormat = pixelFormat;
		this.minFilter = minFilter;
		this.magFilter = magFilter;
		this.wrapping = wrapping;
	}
	
	protected abstract Buffer generate();
	
	public void buffer()
	{
		buffer = generate();
	}
	
	public void init()
	{
		if (buffer == null)
			buffer();
		
		if (this.initialized())
			delete();

		GLES20.glGenTextures(1, texture, 0);

		this.bind();

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, magFilter);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wrapping);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapping);

		buffer.position(0);
		
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height, 0, pixelFormat, type, buffer);
		
		if (minFilter == GLES20.GL_LINEAR_MIPMAP_LINEAR
			|| minFilter == GLES20.GL_LINEAR_MIPMAP_NEAREST
			|| minFilter == GLES20.GL_NEAREST_MIPMAP_LINEAR
			|| minFilter == GLES20.GL_NEAREST_MIPMAP_NEAREST)
			GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

		buffer.limit(0);
		buffer = null;
		
		if (!initialized())
		{
			delete();
			throw new RuntimeException("Error creating texture");
		}
	}
}
