package com.taraxippus.yui.model;

import android.opengl.GLES20;
import com.taraxippus.yui.render.Pass;
import com.taraxippus.yui.render.Shape;

public abstract class Model
{
	public final Pass pass;
	final Shape shape = new Shape();
	final Shape smoothShape = new Shape();
	private float[] vertices, verticesSmooth;
	private short[] indices, indicesSmooth;
	private int usesShape, usesSmoothShape;
	boolean generateShapeNormals = false, generateSmoothNormals = true, weightNormals = false;
	boolean freeSpace = true;
	int shapeType = GLES20.GL_TRIANGLES;
	
	public Model(Pass pass)
	{
		this.pass = pass;
		this.generateShapeNormals = pass.getParent().usesAttribute("a_Normal");
	}
	
	public void init()
	{
		if (vertices == null)
		{
			this.vertices = getVertices();
			this.indices = getIndices();
		}
		if (verticesSmooth == null)
		{
			this.verticesSmooth = getSmoothVertices();
			this.indicesSmooth = getSmoothIndices();
		}
	}

	public Shape getShape()
	{
		if (!shape.initialized())
			createShape();

		usesShape++;
		return shape;
	}

	public Shape getSmoothShape()
	{
		if (pass.getParent() == pass)
			return null;
		
		if (!smoothShape.initialized())
			createSmoothShape();

		usesSmoothShape++;
		return smoothShape;
	}

	public void deleteShape()
	{
		usesShape--;

		if (usesShape == 0 && shape.initialized())
			shape.delete();
	}

	public void deleteSmoothShape()
	{
		usesSmoothShape--;

		if (usesSmoothShape == 0 && smoothShape.initialized())
			smoothShape.delete();
	}

	public Shape createShape()
	{
		init();
		
		if (generateShapeNormals)
			shape.initGenerateFlatNormals(shapeType, Shape.addNormals(vertices, pass), indices, pass.getParent());

		else
			shape.init(shapeType, vertices, indices, pass.getParent().getAttributes());
		
		if (freeSpace)
		{
			vertices = null;
			indices = null;
			freeShapeSpace();
		}
		return shape;
	}

	public Shape createSmoothShape()
	{
		init();
		
		if (generateSmoothNormals)
			smoothShape.initGenerateNormals(shapeType, Shape.addNormals(verticesSmooth, pass), indicesSmooth, weightNormals, pass);
		
		else
			smoothShape.init(shapeType, verticesSmooth, indicesSmooth, pass.getAttributes());
		
		if (freeSpace)
		{
			verticesSmooth = null;
			indicesSmooth = null;
			freeSmoothShapeSpace();
		}
		
		return shape;
	}

	protected void freeShapeSpace() {}
	protected void freeSmoothShapeSpace() {}
	
	public abstract float[] getVertices();
	public abstract short[] getIndices();
	
	public float[] getSmoothVertices()
	{
		return vertices;
	}
	
	public short[] getSmoothIndices()
	{
		return indices;
	}
}
