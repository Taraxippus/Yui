package com.taraxippus.yui.model;

import android.opengl.*;
import com.taraxippus.yui.game.*;
import com.taraxippus.yui.render.*;

public class RingModel extends Model
{
	public final float WIDTH = 0.025F;
	
	public final int sectors;
	public final float percentage;

	public RingModel(Pass pass, int sectors, float percentage)
	{
		super(pass);

		this.sectors = sectors;
		this.percentage = percentage;
	}

	@Override
	public float[] getVertices()
	{
		final float[] vertices = new float[sectors * 3 * 4];

		final float S = (float) Math.PI * 2.0F * percentage / (percentage == 1 ? sectors : sectors - 1);
		int r, s, offset = 0;
		float x, y, z;
		
		for (s = 0; s < sectors; s++) 
		{
			for (r = 0; r < 4; ++r)
			{
				y = (float) (Math.sin(s * S));
				x = (float) (Math.cos(s * S));
				z = r / 2;
				
				vertices[offset++] = x * (0.5F - WIDTH * (r % 2));
				vertices[offset++] = y * (0.5F - WIDTH * (r % 2));
				vertices[offset++] = z * (0.5F - WIDTH * (r % 2));
			}
		}

		return vertices;
	}

	@Override
	public short[] getIndices()
	{
		int s1, s, offset = 0;

		final short[] indices = new short[(sectors - 1) * 4 * 6 + (percentage == 1 ? 24 : 12)];
		
		for (s = 0; s < (percentage == 1 ? sectors : sectors - 1); s++)
		{
			if (percentage == 1)
				s1 = (s + 1) % (sectors);
			else
				s1 = s + 1;
				
			indices[offset++] = (short) (s * 4 + 1);
			indices[offset++] = (short) (s * 4 + 3);
			indices[offset++] = (short) (s1 * 4 + 1);
			
			indices[offset++] = (short) (s * 4 + 3);
			indices[offset++] = (short) (s1 * 4 + 3);
			indices[offset++] = (short) (s1 * 4 + 1);
			
			indices[offset++] = (short) (s * 4 + 0);
			indices[offset++] = (short) (s1 * 4 + 0);
			indices[offset++] = (short) (s * 4 + 2);

			indices[offset++] = (short) (s * 4 + 2);
			indices[offset++] = (short) (s1 * 4 + 0);
			indices[offset++] = (short) (s1 * 4 + 2);
			
			
			indices[offset++] = (short) (s * 4 + 0);
			indices[offset++] = (short) (s * 4 + 1);
			indices[offset++] = (short) (s1 * 4 + 0);

			indices[offset++] = (short) (s * 4 + 1);
			indices[offset++] = (short) (s1 * 4 + 1);
			indices[offset++] = (short) (s1 * 4 + 0);
			
			indices[offset++] = (short) (s * 4 + 2);
			indices[offset++] = (short) (s1 * 4 + 2);
			indices[offset++] = (short) (s * 4 + 3);

			indices[offset++] = (short) (s * 4 + 3);
			indices[offset++] = (short) (s1 * 4 + 2);
			indices[offset++] = (short) (s1 * 4 + 3);
		}
		
		if (percentage != 1)
		{
			indices[offset++] = 0;
			indices[offset++] = 2;
			indices[offset++] = 1;

			indices[offset++] = 1;
			indices[offset++] = 2;
			indices[offset++] = 3;
			
			indices[offset++] = (short) ((sectors - 1) * 4);
			indices[offset++] = (short) ((sectors - 1) * 4 + 1);
			indices[offset++] = (short) ((sectors - 1) * 4 + 2);

			indices[offset++] = (short) ((sectors - 1) * 4 + 1);
			indices[offset++] = (short) ((sectors - 1) * 4 + 3);
			indices[offset++] = (short) ((sectors - 1) * 4 + 2);
		}

		return indices;
	}
}
