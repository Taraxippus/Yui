package com.taraxippus.yui.util;

import java.util.ArrayList;

public class Vertex
{
	final ArrayList<VectorF> normals = new ArrayList<>();
	final ArrayList<Short> instances = new ArrayList<>();
	
	final float[] vertexData;
	
	public Vertex(float[] vertices, int stride, int offset)
	{
		vertexData = new float[stride];
		System.arraycopy(vertices, offset, vertexData, 0, stride);
	}
	
	public void add(ArrayList<Float> vertices, ArrayList<Short> indices, int indicesIndex, VectorF normal, int normalIndex)
	{
		if (normals.isEmpty())
		{
			normals.add(normal);
			instances.add(indices.get(indicesIndex));
			vertices.set((indices.get(indicesIndex) & 0xffff) * vertexData.length + 3, normal.x);
			vertices.set((indices.get(indicesIndex) & 0xffff) * vertexData.length + 4, normal.y);
			vertices.set((indices.get(indicesIndex) & 0xffff) * vertexData.length + 5, normal.z);
			return;
		}
		
		for (int i = 0; i < normals.size(); ++i)
			if (normals.get(i).equals(normal))
			{
				indices.set(indicesIndex, instances.get(i));
				return;
			}
		
		
		normals.add(normal);
		instances.add((short) (vertices.size() / vertexData.length));
		indices.set(indicesIndex, instances.get(instances.size() - 1));
		
		vertexData[normalIndex] = normal.x;
		vertexData[normalIndex + 1] = normal.y;
		vertexData[normalIndex + 2] = normal.z;
		
		for (int i = 0; i < vertexData.length; ++i)
			vertices.add(vertexData[i]);
	}
}
