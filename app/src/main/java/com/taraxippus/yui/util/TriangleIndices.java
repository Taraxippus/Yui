package com.taraxippus.yui.util;
import java.util.ArrayList;

public class TriangleIndices
{
	public final short v1, v2, v3;

	public TriangleIndices(int x, int y, int z)
	{
		this.v1 = (short) x;
		this.v2 = (short) y;
		this.v3 = (short) z;
	}
	
	public TriangleIndices copy(int offset)
	{
		return new TriangleIndices((v1 & 0xFFFF) + offset, (v2 & 0xFFFF) + offset, (v3 & 0xFFFF) + offset);
	}
	
	public static final ArrayList<TriangleIndices> copyToList(ArrayList<TriangleIndices> faces, ArrayList<TriangleIndices> newFaces, int offset)
	{
        for (int i = 0; i < newFaces.size(); ++i)
			faces.add(newFaces.get(i).copy(offset));
            
		return faces;
	}
	
	public static final short[] toIndices(ArrayList<TriangleIndices> faces, int offset)
	{
		final short[] indices = new short[faces.size() * 3];
		TriangleIndices tri;

        for (int i = 0; i < faces.size(); ++i)
        {
			tri = faces.get(i);
            indices[i * 3] = (short) (tri.v1 & 0xFFFF + offset);
			indices[i * 3 + 1] = (short) (tri.v2 & 0xFFFF + offset);
			indices[i * 3 + 2] = (short) (tri.v3 & 0xFFFF + offset);
        }
		
		return indices;
	}
	
	public static final short[] toIndices(short[] indices, int index, ArrayList<TriangleIndices> faces, int offset)
	{
		TriangleIndices tri;

        for (int i = 0; i < faces.size(); ++i)
        {
			tri = faces.get(i);
            indices[i * 3 + index] = (short) ((tri.v1 & 0xFFFF) + offset);
			indices[i * 3 + 1 + index] = (short) ((tri.v2 & 0xFFFF) + offset);
			indices[i * 3 + 2 + index] = (short) ((tri.v3 & 0xFFFF) + offset);
        }

		return indices;
	}
}
