package com.taraxippus.yui.model;

import android.opengl.GLES20;
import com.taraxippus.yui.render.Pass;
import com.taraxippus.yui.render.Shape;

public abstract class Model
{
	public final Pass pass;
	final Shape shape = new Shape();
	final Shape outlineShape = new Shape();
	private float[] vertices, verticesOutline;
	private short[] indices, indicesOutline;
	private int usesShape, usesOutline;
	boolean generateOutlineNormals = true, weightNormals = false;
	boolean freeSpace = true;
	int shapeType = GLES20.GL_TRIANGLES;
	
	public Model(Pass pass)
	{
		this.pass = pass;
	}
	
	public void init()
	{
		if (vertices == null)
		{
			this.vertices = getVertices();
			this.indices = getIndices();
		}
		if (verticesOutline == null)
		{
			this.verticesOutline = getOutlineVertices();
			this.indicesOutline = getOutlineIndices();
		}
	}

	public Shape getShape()
	{
		if (!shape.initialized())
			createShape();

		usesShape++;
		return shape;
	}

	public Shape getOutlineShape()
	{
		if (pass.getParent() == pass)
			return null;
		
		if (!outlineShape.initialized())
			createOutlineShape();

		usesOutline++;
		return outlineShape;
	}

	public void deleteShape()
	{
		usesShape--;

		if (usesShape == 0 && shape.initialized())
			shape.delete();
	}

	public void deleteOutlineShape()
	{
		usesOutline--;

		if (usesOutline == 0 && outlineShape.initialized())
			outlineShape.delete();
	}

	public Shape createShape()
	{
		init();
		
		shape.init(shapeType, vertices, indices, pass.getParent().getAttributes());
		
		if (freeSpace)
		{
			vertices = null;
			indices = null;
		}
		return shape;
	}

	public Shape createOutlineShape()
	{
		init();
		
		if (generateOutlineNormals)
			outlineShape.initGenerateNormals(shapeType, Shape.addNormals(verticesOutline, pass.getAttributes()), indicesOutline, weightNormals, pass.getAttributes());
		
		else
			outlineShape.init(shapeType, verticesOutline, indicesOutline, pass.getAttributes());
		
		if (freeSpace)
		{
			verticesOutline = null;
			indicesOutline = null;
		}
		
		return shape;
	}

	public abstract float[] getVertices();
	public abstract short[] getIndices();
	
	public float[] getOutlineVertices()
	{
		return vertices;
	}
	
	public short[] getOutlineIndices()
	{
		return indices;
	}
}
