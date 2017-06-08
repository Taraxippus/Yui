package com.taraxippus.yui.model;

import com.taraxippus.yui.render.Pass;
import com.taraxippus.yui.util.TriangleIndices;
import com.taraxippus.yui.util.VectorF;
import java.util.ArrayList;

public class ProceduralModel extends Model
{
	public ArrayList<VectorF> vertices;
	public ArrayList<TriangleIndices> faces;
	
	public ProceduralModel(Pass pass)
	{
		super(pass);
	}
	
	public void generate()
	{
		vertices = new ArrayList<VectorF>();
		faces = new ArrayList<TriangleIndices>();
	}
	
	@Override
	public float[] getVertices()
	{
		generate();

		return VectorF.toArrayList(vertices, true);
	}

	@Override
	public short[] getIndices()
	{
		return TriangleIndices.toIndices(faces, 0);
	}

	@Override
	protected void freeShapeSpace()
	{
		super.freeShapeSpace();

		if (vertices != null)
			for (VectorF v : vertices)
				v.release();
				
		vertices = null;
		faces = null;
	}
}
