package com.taraxippus.yui.texture;
import android.opengl.GLES20;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Random;

public class NoiseTexture extends ProceduralTexture
{
	final int min, max;
	
	public NoiseTexture(Random random, int width, int height)
	{
		this(random, width, height, 0, 255);
	}
	
	public NoiseTexture(Random random, int width, int height, int min, int max)
	{
		super(random, width, height, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, GLES20.GL_LUMINANCE, GLES20.GL_NEAREST, GLES20.GL_NEAREST, GLES20.GL_CLAMP_TO_EDGE);
		this.min = min;
		this.max = max;
	}
	
	@Override
	protected Buffer generate()
	{
		final ByteBuffer buffer = ByteBuffer.allocate(width * height);
		
		for (int i = 0; i < width * height; ++i)
		{
			buffer.put((byte) (min + random.nextInt(max - min)));
		}
		
		return buffer;
	}
}
