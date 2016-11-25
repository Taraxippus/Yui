package com.taraxippus.yui.game;

import android.opengl.GLES20;
import com.taraxippus.yui.game.World;
import com.taraxippus.yui.render.Pass;
import com.taraxippus.yui.render.Shape;
import java.util.Arrays;
import com.taraxippus.yui.render.Renderer;

public class FullscreenQuad extends GameObject
{
	final Pass[] passes;
	
	public FullscreenQuad(World world, Pass... passes)
	{
		super(world);
		
		this.passes = passes;
		this.setPass(passes[0]);
	}

	@Override
	public boolean renderPass(Pass pass)
	{
		for (Pass p : passes)
			if (p == pass)
				return true;
			
		return false;
	}
	
	public static final float[] vertices = new float[] 
	{
		-1, -1,
		1, -1,
		-1, 1,
		1, 1
	};
	
	@Override
	public Shape createShape()
	{
		Shape shape = new Shape();
		shape.init(GLES20.GL_TRIANGLE_STRIP, vertices, 4, getPass().getAttributes());
		return shape;
	}
	
}
