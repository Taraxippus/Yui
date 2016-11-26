package com.taraxippus.yui.render;

import android.opengl.GLES20;
import com.taraxippus.yui.util.VectorF;
import com.taraxippus.yui.util.Vertex;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class Shape
{
	final int[] vbo = new int[2];
	int[] attributes;
	
	int indexCount;
	int type;
	
	boolean hasIndices = true;
	
	public Shape()
	{
		
	}
	
	public static final float[] addNormals(float[] vertices, Pass pass)
	{
		int index = pass.getAttributeIndex("a_Normal"), stride = pass.getStride();
		
		float[] vertices1 = new float[vertices.length / (stride - 3) * stride];
		for (int i = 0; i < vertices.length; i += stride - 3)
		{
			if (index != 0)
				System.arraycopy(vertices, i, vertices1, i / (stride - 3) * stride, index);
			
			if (index != stride - 3)
				System.arraycopy(vertices, i + index, vertices1, i / (stride - 3) * stride + index + 3, stride - index - 3);
		}
			
		return vertices1;
	}
	
	public void initGenerateNormals(int type, float[] vertices, short indices[], boolean weight, Pass pass)
	{
		int positionOffset = pass.getAttributeIndex("a_Position"), normalOffset = pass.getAttributeIndex("a_Normal"), stride = pass.getStride();
		
		final VectorF tmp1 = VectorF.obtain(), tmp2 = VectorF.obtain(), tmp3 = VectorF.obtain(), tmp4 = VectorF.obtain();
		final VectorF[] normals = new VectorF[vertices.length / stride];

		for (int i = 0; i < normals.length; i++)
			normals[i] = VectorF.obtain();

		for (int i = 0; i < indices.length; i += 3)
		{
			tmp1.set(vertices, indices[i], stride, positionOffset);
			tmp2.set(vertices, indices[i + 1], stride, positionOffset);
			tmp3.set(vertices, indices[i + 2], stride, positionOffset);

			tmp4.set(tmp2).subtract(tmp1).cross(tmp3.subtract(tmp1));
			
			if (!weight)
				tmp4.normalize();
			
			if (!tmp4.isNaN())
			{
				normals[indices[i] & 0xffff].add(tmp4);
				normals[indices[i + 1] & 0xffff].add(tmp4);
				normals[indices[i + 2] & 0xffff].add(tmp4);
			}
		}

		for (int i = 0; i < normals.length; i++)
		{
			normals[i].normalize();
			vertices[i * stride + normalOffset] = normals[i].x;
			vertices[i * stride + normalOffset + 1] = normals[i].y;
			vertices[i * stride + normalOffset + 2] = normals[i].z;
		}

		for (int i = 0; i < normals.length; i++)
			normals[i].release();

		tmp1.release();
		tmp2.release();
		tmp3.release();
		tmp4.release();
		
		init(type, vertices, indices, pass.getAttributes());
	}
	
	public void initGenerateFlatNormals(int type, float[] vertices, short indices[], Pass pass)
	{
		int positionOffset = pass.getAttributeIndex("a_Position"),  normalOffset = pass.getAttributeIndex("a_Normal"), stride = pass.getStride();
		
		final VectorF tmp1 = VectorF.obtain(), tmp2 = VectorF.obtain(), tmp3 = VectorF.obtain(), tmp4;
		final ArrayList<Vertex> vertexObjects = new ArrayList<Vertex>();
		final ArrayList<Float> vertices2 = new ArrayList<Float>();
		final ArrayList<Short> indices2 = new ArrayList<Short>();
		final ArrayList<VectorF> normals = new ArrayList<VectorF>();
		
		for (float f : vertices)
			vertices2.add(f);
			
		for (short s : indices)
			indices2.add(s);
			
		for (int i = 0; i < vertices.length; i += stride)
			vertexObjects.add(new Vertex(vertices, stride, i));
		
			
		for (int i = 0; i < indices.length; i += 3)
		{
			tmp1.set(vertices, indices[i], stride, positionOffset);
			tmp2.set(vertices, indices[i + 1], stride, positionOffset);
			tmp3.set(vertices, indices[i + 2], stride, positionOffset);
			
			tmp4 = VectorF.obtain();
			normals.add(tmp4);
			tmp4.set(tmp2).subtract(tmp1).cross(tmp3.subtract(tmp1)).normalize();

			vertexObjects.get(indices[i] & 0xffff).add(vertices2, indices2, i, tmp4, normalOffset);
			vertexObjects.get(indices[i + 1] & 0xffff).add(vertices2, indices2, i + 1, tmp4, normalOffset);
			vertexObjects.get(indices[i + 2] & 0xffff).add(vertices2, indices2, i + 2, tmp4, normalOffset);
		}

		for (int i = 0; i < normals.size(); i++)
			normals.get(i).release();

		tmp1.release();
		tmp2.release();
		tmp3.release();
		
		FloatBuffer vertices3 = FloatBuffer.allocate(vertices2.size());
		for (int i = 0; i < vertices2.size(); ++i)
			vertices3.put(vertices2.get(i));
		
		ShortBuffer indices3 = ShortBuffer.allocate(indices2.size());
		for (int i = 0; i < indices2.size(); ++i)
			indices3.put(indices2.get(i));
			
		init(type, vertices3, indices3, false, pass.getAttributes());
	}
	
	public void init(int type, float[] vertices, short[] indices,  int... attributes)
	{
		init(type, FloatBuffer.wrap(vertices), ShortBuffer.wrap(indices), 0, false, attributes);
	}
	
	public void init(int type, FloatBuffer vertices, ShortBuffer indices, boolean dynamic, int... attributes)
	{
		init(type, vertices, indices, 0, dynamic, attributes);
	}
	
	public void init(int type, float[] vertices, int indexCount, int... attributes)
	{
		init(type, FloatBuffer.wrap(vertices), null, indexCount, false, attributes);
	}
	
	public void init(int type, FloatBuffer vertices, int indexCount, boolean dynamic, int... attributes)
	{
		init(type, vertices, null, indexCount, dynamic, attributes);
	}
	
	private void init(int type, FloatBuffer vertices, ShortBuffer indices, int indexCount, boolean dynamic, int... attributes)
	{
		if (initialized())
			delete();
		
		this.type = type;
		this.hasIndices = indices != null;
		this.attributes = attributes;
		
		if (hasIndices)
		{
			this.indexCount = indices.capacity();

			GLES20.glGenBuffers(2, vbo, 0);

			if (initialized()) 
			{
				vertices.position(0);
				indices.position(0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.capacity() * 4, vertices, dynamic ? GLES20.GL_DYNAMIC_DRAW : GLES20.GL_STATIC_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo[1]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indices.capacity() * 2, indices, dynamic ? GLES20.GL_DYNAMIC_DRAW : GLES20.GL_STATIC_DRAW);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

				if (!dynamic)
				{
					vertices.limit(0);
					indices.limit(0);
				}
			}
			else 
			{
				throw new RuntimeException("Error creating VBO");
			}
		}
		else
		{
			this.indexCount = indexCount;

			GLES20.glGenBuffers(1, vbo, 0);

			if (initialized()) 
			{
				vertices.position(0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.capacity() * 4, vertices, dynamic ? GLES20.GL_DYNAMIC_DRAW : GLES20.GL_STATIC_DRAW);
				
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			
				if (!dynamic)
					vertices.limit(0);
			}
			else 
				throw new RuntimeException("Error creating VBO");
		}
	}
	
	public void buffer(FloatBuffer vertices, ShortBuffer indices)
	{
		if (initialized())
		{
			if (hasIndices)
			{
				if (vertices != null)
				{
					vertices.position(0);
					
					GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
					GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertices.capacity() * 4, vertices);

					GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				}
					
				if (indices != null)
				{
					indices.position(0);
					
					GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo[1]);
					GLES20.glBufferSubData(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0, indices.capacity() * 2, indices);
					
					GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
				}
			}
			else
			{
				vertices.position(0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
				GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertices.capacity() * 4, vertices);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			}
		}
		else 
			throw new RuntimeException("Tried to buffer unitialized shape");
	}
	
	public boolean initialized()
	{
		return vbo[0] != 0 && (!hasIndices || vbo[1] != 0);
	}
	
	int stride;
	int offset;
	
	public void render()
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
		
		stride = offset = 0;
		
		for (int i = 0; i < attributes.length; ++i)
		{
			stride += attributes[i] * 4;
		}
		
		for (int i = 0; i < attributes.length; ++i)
		{
			GLES20.glVertexAttribPointer(i, attributes[i], GLES20.GL_FLOAT, false, stride, offset);
			GLES20.glEnableVertexAttribArray(i);
			
			offset += attributes[i] * 4;
		}

		if (hasIndices)
		{
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo[1]);
			GLES20.glDrawElements(type, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		else
		{
			GLES20.glDrawArrays(type, 0, indexCount);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		}
	}
	
	public void delete()
	{
		if (!initialized())
		{
			System.err.println("Tried to delete uninitialized vbo");
			return;
		}
		
		GLES20.glDeleteBuffers(hasIndices ? 2 : 1, vbo, 0);
		vbo[0] = vbo[1] = 0;
	}
}
