package com.taraxippus.yui.model;

import com.taraxippus.yui.render.Pass;

public class UVBoxModel extends Model
{
	public UVBoxModel(Pass pass)
	{
		super(pass);

		if (pass.getAttributeIndex("a_UV") != 3)
			throw new RuntimeException("A UVBox always needs a pass that supports uvs at index 3+!");
	}

	@Override
	public float[] getVertices()
	{
		return new float[]
		{
			-0.5F, -0.5F, -0.5F,
			0, 1,
			0.5F, -0.5F, -0.5F,
			1, 1,
			-0.5F, -0.5F, 0.5F,
			0, 0,
			0.5F, -0.5F, 0.5F,
			1, 0,

			-0.5F, 0.5F, -0.5F,
			0, 0,
			0.5F, 0.5F, -0.5F,
			1, 0,
			-0.5F, 0.5F, 0.5F,
			0, 1,
			0.5F, 0.5F, 0.5F,
			1, 1,
			

			-0.5F, -0.5F, -0.5F,
			0, 1,
			-0.5F, 0.5F, -0.5F,
			0, 0,
			0.5F, -0.5F, -0.5F,
			1, 1,
			0.5F, 0.5F, -0.5F,
			1, 0,
			
			0.5F, -0.5F, -0.5F,
			0, 1,
			0.5F, 0.5F, -0.5F,
			0, 0,
			0.5F, -0.5F, 0.5F,
			1, 1,
			0.5F, 0.5F, 0.5F,
			1, 0,
			
			
			0.5F, -0.5F, 0.5F,
			1, 1,
			0.5F, 0.5F, 0.5F,
			1, 0,
			-0.5F, -0.5F, 0.5F,
			0, 1,
			-0.5F, 0.5F, 0.5F,
			0, 0,
			
			-0.5F, -0.5F, 0.5F,
			0, 1,
			-0.5F, 0.5F, 0.5F,
			0, 0,
			-0.5F, -0.5F, -0.5F,
			1, 1,
			-0.5F, 0.5F, -0.5F,
			1, 0,
		};
	}

	@Override
	public short[] getIndices()
	{
		return new short[]
		{
			0, 1, 2,
			1, 3, 2,

			4, 6, 5,
			5, 6, 7,

			8, 9, 10,
			9, 11, 10,

			12, 13, 14,
			13, 15, 14,

			16, 17, 18,
			17, 19, 18,

			20, 21, 22,
			21, 23, 22,
		};
	}
}
