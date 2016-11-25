package com.taraxippus.yui.model;

import android.opengl.*;
import com.taraxippus.yui.game.*;
import com.taraxippus.yui.render.*;

public class SphereModel extends Model
{
	public final int rings;
	public final int sectors;

	public SphereModel(Pass pass, int rings, int sectors)
	{
		super(pass);
		
		this.rings = rings;
		this.sectors = sectors;
		
		this.generateOutlineNormals = false;
	}

	@Override
	public float[] getVertices()
	{
		final float[] vertices = new float[rings * sectors * 3];

		final float R = 1.0F / (float) (rings - 1);
		final float S = 1.0F / (float) (sectors - 1);
		int r, s, offset = 0;
		float x, y, z;

		for (r = 0; r < rings; r++) 
			for (s = 0; s < sectors; s++) 
			{
				y = (float) (Math.sin(-Math.PI / 2 + Math.PI * r * R));
				x = (float) (Math.cos(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));
				z = (float) (Math.sin(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));

				vertices[offset++] = x * 0.5F;
				vertices[offset++] = y * 0.5F;
				vertices[offset++] = z * 0.5F;
			}
			
		return vertices;
	}

	@Override
	public float[] getOutlineVertices()
	{
		final float[] vertices = new float[rings * sectors * 6];

		final float R = 1.0F / (float) (rings - 1);
		final float S = 1.0F / (float) (sectors - 1);
		int r, s, offset = 0;
		float x, y, z;

		for (r = 0; r < rings; r++) 
			for (s = 0; s < sectors; s++) 
			{
				y = (float) (Math.sin(-Math.PI / 2 + Math.PI * r * R));
				x = (float) (Math.cos(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));
				z = (float) (Math.sin(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));

				vertices[offset++] = x * 0.5F;
				vertices[offset++] = y * 0.5F;
				vertices[offset++] = z * 0.5F;
				
				vertices[offset++] = x;
				vertices[offset++] = y;
				vertices[offset++] = z;
			}

		return vertices;
	}

	@Override
	public short[] getIndices()
	{
		int r, s, offset = 0;

		final short[] indices = new short[(rings - 1) * (sectors - 1) * 6];

		for (r = 0; r < rings - 1; r++)
			for (s = 0; s < sectors - 1; s++)
			{
				indices[offset++] = (short) (r * sectors + s);
				indices[offset++] = (short) ((r + 1) * sectors + (s + 1));
				indices[offset++] = (short) (r * sectors + (s + 1));

				indices[offset++] = (short) (r * sectors + s);
				indices[offset++] = (short) ((r + 1) * sectors + s);
				indices[offset++] = (short) ((r + 1) * sectors + (s + 1));
			}

		return indices;
	}
}
